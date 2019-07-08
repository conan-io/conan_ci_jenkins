package org.jfrog.conanci


class TestRunner {

    private static final String winTmpBase = "D:/J/t/"
    private static final String restTmpBase = "/tmp/"
    private static final String runnerPath = ".ci/jenkins/runner.py"
    private static final String numCores = "3"
    private script;
    private static boolean isPR = false
    private TestLevelConfig testLevelConfig

    TestRunner(script){
        testLevelConfig = new TestLevelConfig(script)
        testLevelConfig.init()
        this.script = script
    }


    void run(){
        runRestTests()

        if(script.env.JOB_NAME == "ConanNightly" || script.env.BRANCH_NAME =~ /(^release.*)|(^master)/) {
            runReleaseTests()
        }
        else{
            runRegularBuildTests()
        }
    }


    private static String getStageLabel(String slaveLabel, boolean enabledRevisions, String pyver, List<String> excludedTags){
        String eTags = "-e " + excludedTags.join(' -e ')
        String ret = "${slaveLabel} - ${getFlavor(enabledRevisions)} - ${pyver} - '${eTags}'"
        return ret
    }


    private static String getFlavor(boolean revisionsEnabled){
        return revisionsEnabled ? "enabled_revisions" : "disabled_revisions"
    }

    void runRestTests(){
        Map<String, Closure> restBuilders = [:]
        for (slaveLabel in ["Windows", "Linux"]) {
            String stageLabel = "${slaveLabel} Rest API Test"
            restBuilders[stageLabel] = runTestSuite(slaveLabel, stageLabel, false, "py36", [])
        }
        script.parallel(restBuilders)
    }


    void runRegularBuildTests(){
        List<String> excludedTags = testLevelConfig.getEffectiveExcludedTags()
        for(revisionsEnabled in testLevelConfig.getEffectiveRevisionsConfigurations()) {
            // First (revisions or not) for linux
            Map<String, Closure> builders = [:]
            List<String> pyVers = testLevelConfig.getEffectivePyvers("Linux")
            for (def pyver in pyVers) {
                String stageLabel = getStageLabel("Linux", revisionsEnabled, pyver, excludedTags)
                builders[stageLabel] = runTestSuite("Linux", stageLabel, false, pyver, excludedTags)
            }
            script.parallel(builders)

            // Seconds (revisions or not) for Mac and windows
            builders = [:]
            for (def slaveLabel in ["Macos", "Windows"]) {
                pyVers = testLevelConfig.getEffectivePyvers(slaveLabel)
                for (def pyver in pyVers) {
                    String stageLabel = getStageLabel(slaveLabel, revisionsEnabled, pyver, excludedTags)
                    builders[stageLabel] = runTestSuite(slaveLabel, stageLabel, false, pyver, excludedTags)
                }
            }
            script.parallel(builders)
        }
    }

    void runReleaseTests(){
        List<String> excludedTags = testLevelConfig.getEffectiveExcludedTags()
        Map<String, Closure> builders = [:]
        for(revisionsEnabled in [true, false]) {
            for (slaveLabel in ["Linux", "Macos", "Windows"]) {
                def pyVers = testLevelConfig.getEffectivePyvers(slaveLabel)
                for (def pyver in pyVers) {
                    String stageLabel = getStageLabel(slaveLabel, revisionsEnabled, pyver, excludedTags)
                    builders[stageLabel] = runTestSuite(slaveLabel, stageLabel, revisionsEnabled, pyver, excludedTags)
                }
            }
        }
        script.parallel(builders)
    }

    private Closure runTestSuite(String slaveLabel, String stageLabel, boolean revisionsEnabled, String pyver, List<String> excludedTags){
        String eTags
        if(excludedTags){
            eTags = "-e " + excludedTags.join(' -e ')
        }
        String flavor = getFlavor(revisionsEnabled)

        def ret = script.node(slaveLabel) {
            script.stage(stageLabel){
                def workdir
                def sourcedir
                def base_source
                script.lock('source_code') { // Prepare a clean new directory with the sources
                    try{
                        script.step([$class: 'WsCleanup'])
                    }
                    catch(ignore){
                        script.echo "Cannot clean WS"
                    }

                    Map<String, String> vars = script.checkout(script.scm)
                    def commit = vars["GIT_COMMIT"].substring(0, 4)
                    script.echo "Starting ${script.env.JOB_NAME} with branch ${script.env.BRANCH_NAME}"
                    String base_dir = (slaveLabel == "Windows") ? winTmpBase : restTmpBase

                    workdir = "${base_dir}${commit}/${pyver}/${flavor}"
                    base_source = "${base_dir}source/${commit}"
                    sourcedir = "${base_source}/${pyver}/${flavor}"
                    while(script.fileExists(sourcedir)){
                        sourcedir = sourcedir + "_"
                    }

                    script.dir(base_source){ // Trick to create the parent
                        def escaped_ws = "${script.WORKSPACE}".toString().replace("\\", "/")
                        String cmd = "python -c \"import shutil; shutil.copytree('${escaped_ws}', '${sourcedir}')\"".toString()
                        if (slaveLabel == "Windows"){
                            script.bat(script: cmd)
                        }
                        else{
                            script.sh(script: cmd)
                        }
                    }
                }

                String testModule = "\"conans.test\""
                String numcores = "--num_cores=${numCores}"

                if(slaveLabel == "Windows"){
                    try{

                        script.withEnv(["CONAN_TEST_FOLDER=${workdir}"]){
                            script.bat(script: "python ${runnerPath} ${testModule} ${pyver} ${sourcedir} \"${workdir}\" -e rest_api ${numcores} --flavor ${flavor} ${eTags}")
                        }
                    }
                    finally{
                        script.bat(script: "rd /s /q \"${workdir}\"")
                        script.bat(script: "rd /s /q \"${sourcedir}\"")
                    }
                }
                else if(slaveLabel == "Macos"){
                    try{
                        script.withEnv(['PATH=/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin']) {
                            script.sh(script: "python ${runnerPath} ${testModule} ${pyver} ${sourcedir} ${workdir} -e rest_api ${numcores} --flavor ${flavor} ${eTags}")
                        }
                    }
                    finally{
                        script.sh(script: "rm -rf ${workdir}")
                        script.sh(script: "rm -rf ${sourcedir}")
                    }
                }
            }
        }
        return ret
    }

}
