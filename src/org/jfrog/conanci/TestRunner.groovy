package org.jfrog.conanci


class TestRunner {

    private static final String winTmpBase = "D:/J/t/"
    private static final String restTmpBase = "/tmp/"
    private static final String numCores = "3"
    private script;
    private TestLevelConfig testLevelConfig

    TestRunner(script){
        testLevelConfig = new TestLevelConfig(script)
        this.script = script
    }


    void run(){
        cancelPreviousCommits()
        testLevelConfig.init() // This will read the tags from the PR if this is a PR
        runRESTTests()
        script.echo("Branch: ${script.env.BRANCH_NAME}")
        if(script.env.JOB_NAME == "ConanNightly" || script.env.BRANCH_NAME =~ /(^release.*)|(^master)/) {
            runReleaseTests()
        }
        else{
            runRegularBuildTests()
        }
    }

    void cancelPreviousCommits(){
        script.stage("Cancelling previous") {
            BuildCanceller.cancelPrevious(script)
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

    void runRESTTests(){
        List<String> excludedTags = []
        List<String> includedTags = ["rest_api", "local_bottle"]
        String testModule = "\"conans.test\""
        Map<String, Closure> restBuilders = [:]
        for (slaveLabel in ["Windows", "Linux"]) {
            List<String> pyVers = testLevelConfig.getEffectivePyvers("Linux")
            for (def pyver in pyVers) {
                String stageLabel = "${slaveLabel} Rest API Test - ${pyver}"
                restBuilders[stageLabel] = getTestClosure(testModule, slaveLabel, stageLabel, false, pyver, excludedTags, includedTags)
            }
        }
        script.parallel(restBuilders)
    }


    void runRegularBuildTests(){
        String testModule = "\"conans.test\""
        List<String> excludedTags = testLevelConfig.getEffectiveExcludedTags()
        excludedTags.add("rest_api")
        excludedTags.add("local_bottle")
        for(revisionsEnabled in testLevelConfig.getEffectiveRevisionsConfigurations()) {
            // First (revisions or not) for linux
            Map<String, Closure> builders = [:]
            List<String> pyVers = testLevelConfig.getEffectivePyvers("Linux")
            for (def pyver in pyVers) {
                String stageLabel = getStageLabel("Linux", revisionsEnabled, pyver, excludedTags)
                builders[stageLabel] = getTestClosure(testModule, "Linux", stageLabel, revisionsEnabled, pyver, excludedTags, [])
            }
            script.parallel(builders)

            // Seconds (revisions or not) for Mac and windows
            builders = [:]
            for (def slaveLabel in ["Macos", "Windows"]) {
                pyVers = testLevelConfig.getEffectivePyvers(slaveLabel)
                for (def pyver in pyVers) {
                    String stageLabel = getStageLabel(slaveLabel, revisionsEnabled, pyver, excludedTags)
                    builders[stageLabel] = getTestClosure(testModule, slaveLabel, stageLabel, revisionsEnabled, pyver, excludedTags, [])
                }
            }
            script.parallel(builders)
        }
    }

    void runReleaseTests(){
        String testModule = "\"conans.test\""
        List<String> excludedTags = testLevelConfig.getEffectiveExcludedTags()
        excludedTags.add("rest_api")
        excludedTags.add("local_bottle")
        for(revisionsEnabled in [true, false]) {
            Map<String, Closure> builders = [:]
            for (slaveLabel in ["Linux", "Macos", "Windows"]) {
                def pyVers = testLevelConfig.getEffectivePyvers(slaveLabel)
                for (def pyver in pyVers) {
                    String stageLabel = getStageLabel(slaveLabel, revisionsEnabled, pyver, excludedTags)
                    builders[stageLabel] = getTestClosure(testModule, slaveLabel, stageLabel, revisionsEnabled, pyver, excludedTags, [])
                }
            }
            script.parallel(builders)
        }
    }


    private Closure getTestClosure(String testModule, String slaveLabel, String stageLabel, boolean revisionsEnabled, String pyver,
                                   List<String> excludedTags, List<String> includedTags){
        String eTags = ""
        if(excludedTags){
            eTags = "-e " + excludedTags.join(' -e ')
        }
        if(includedTags){
            eTags += " -i " + includedTags.join(' -i ')
        }
        String flavor = getFlavor(revisionsEnabled)

        def ret = {
            script.node(slaveLabel) {
                script.stage(stageLabel) {
                    def workdir
                    def sourcedir
                    def base_source
                    script.lock('source_code') { // Prepare a clean new directory with the sources
                        try {
                            script.step([$class: 'WsCleanup'])
                        }
                        catch (ignore) {
                            script.echo "Cannot clean WS"
                        }

                        Map<String, String> vars = script.checkout(script.scm)

                        def commit = vars["GIT_COMMIT"].substring(0, 4)
                        script.echo "Starting ${script.env.JOB_NAME} with branch ${script.env.BRANCH_NAME}"
                        String base_dir = (slaveLabel == "Windows") ? winTmpBase : restTmpBase

                        workdir = "${base_dir}${commit}/${pyver}/${flavor}"
                        base_source = "${base_dir}source/${commit}"
                        sourcedir = "${base_source}/${pyver}/${flavor}"
                        while (script.fileExists(sourcedir)) {
                            sourcedir = sourcedir + "_"
                        }

                        script.dir(base_source) { // Trick to create the parent
                            def escaped_ws = "${script.WORKSPACE}".toString().replace("\\", "/")
                            String cmd = "python -c \"import shutil; shutil.copytree('${escaped_ws}', '${sourcedir}')\"".toString()
                            if (slaveLabel == "Windows") {
                                script.bat(script: cmd)
                            } else {
                                script.sh(script: cmd)
                            }
                        }
                    }

                    String numcores = "--num_cores=${numCores}"

                    if (slaveLabel == "Windows") {
                        try {

                            script.withEnv(["CONAN_TEST_FOLDER=${workdir}"]) {
                                script.bat(script: "rd /s /q conan_ci_jenkins", returnStatus: true)
                                script.bat(script: 'git clone https://github.com/conan-io/conan_ci_jenkins')
                                script.bat(script: "python conan_ci_jenkins/python_runner/runner.py ${testModule} ${pyver} ${sourcedir} \"${workdir}\" ${numcores} --flavor ${flavor} ${eTags}")
                            }
                        }
                        finally {
                            script.bat(script: "rd /s /q \"${workdir}\"")
                            script.bat(script: "rd /s /q \"${sourcedir}\"")
                        }
                    } else if (slaveLabel == "Macos") {
                        try {
                            script.withEnv(['PATH=/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin']) {
                                script.sh(script: 'rm -rf conan_ci_jenkins')
                                script.sh(script: 'git clone https://github.com/conan-io/conan_ci_jenkins')
                                script.sh(script: "python conan_ci_jenkins/python_runner/runner.py ${testModule} ${pyver} ${sourcedir} ${workdir} ${numcores} --flavor ${flavor} ${eTags}")
                            }
                        }
                        finally {
                            script.sh(script: "rm -rf ${workdir}")
                            script.sh(script: "rm -rf ${sourcedir}")
                        }
                    }
                    else if (slaveLabel == "Linux"){
                        try {
                            script.docker.image('conanio/conantests').inside("-e CONAN_USER_HOME=${sourcedir} -v${sourcedir}:${sourcedir}") {
                                script.sh(script: 'rm -rf conan_ci_jenkins')
                                script.sh(script: 'git clone https://github.com/conan-io/conan_ci_jenkins')
                                script.sh(script: "python conan_ci_jenkins/python_runner/runner.py ${testModule} ${pyver} ${sourcedir} /tmp ${numcores} --flavor ${flavor} ${eTags}")
                            }
                        }
                        finally {
                            script.sh(script: "rm -rf ${sourcedir}")
                        }
                    }
                }
            }
        }
        return ret
    }

}
