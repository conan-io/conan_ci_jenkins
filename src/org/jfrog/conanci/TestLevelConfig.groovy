package org.jfrog.conanci

class TestLevelConfig {

    List<String> excludedTags;
    Map<String, List<String>> pyVers;
    Boolean revisions
    private script

    TestLevelConfig(script){
        this.script = script

        this.excludedTags = []
        this.pyVers = [:]
        this.revisions = false
    }

    boolean shouldPublishTestPypi(){
        return script.env.BRANCH_NAME == "develop"
    }

    String toString(){
        return "- Forced tags: ${this.excludedTags}\n- Forced pyVers: ${this.pyVers}\n- Forced revisions: ${this.revisions}"
    }

    def init(){
        if (script.env.BRANCH_NAME =~ /(^PR-.*)/) {
            readPRTags()
            script.echo(this.toString())
        }
    }

    private void readPRTags(){
        script.node("Linux"){
            script.stage("Check PR tags"){
                script.withCredentials([script.usernamePassword(credentialsId: 'conanci-gh-token', usernameVariable: 'GH_USER', passwordVariable: 'GH_TOKEN')]) {
                    script.checkout(script.scm)
                    script.sh("docker pull conanio/conantests")
                    script.docker.image('conanio/conantests').inside("-e GH_TOKEN=${script.GH_TOKEN}"){
                        def pr_tags = script.libraryResource('org/jfrog/conanci/python_runner/pr_tags.py')
                        script.writeFile file: "pr_tags.py", text: pr_tags
                        script.sh(script: "python pr_tags.py out.json ${script.env.BRANCH_NAME}")
                        def info = script.readJSON file: 'out.json'

                        excludedTags.addAll(info["tags"])
                        revisions = info["revisions"]

                        for (sl in ["Windows", "Macos", "Linux"]) {
                            pyVers[sl] = []
                            pyVers[sl].addAll(info["pyvers"][sl])
                        }
                    }
                }
            }
        }
    }


    List<String> getEffectivePyvers(String nodeLabel){

        def allPyvers = ["Macos": ['py38', 'py36', 'py27'],
                         "Linux": ['py38', 'py37', 'py36', 'py27'],
                         "Windows": ['py38', 'py36', 'py27']]

        if (script.env.BRANCH_NAME =~ /(^release.*)|(^master)/) {
            return allPyvers[nodeLabel]
        }

        if (script.env.JOB_NAME == "ConanNightly"){
            return allPyvers[nodeLabel]
        }

        if (script.env.BRANCH_NAME =~ /(^PR-.*)/) {

            def reducedPyvers  = ["Macos": ['py36'],
                                  "Linux": ['py38', 'py36', 'py27'],
                                  "Windows": ['py36']]

            reducedPyvers[nodeLabel].addAll(this.pyVers[nodeLabel])
            return reducedPyvers[nodeLabel]
        }
        else{
            return allPyvers[nodeLabel]
        }
    }

    List<Boolean> getEffectiveRevisionsConfigurations(){

        if (this.revisions || script.env.BRANCH_NAME =~ /(^release.*)|(^master)|(^develop)/ || script.env.JOB_NAME == "ConanNightly") {
            return [true, false]
        }

        return [false]
    }

    List<String> getEffectiveExcludedTags(){
        if (script.env.BRANCH_NAME =~ /(^PR-.*)/) {
            List<String> tmp = ["slow", "svn"]
            tmp.removeAll(this.excludedTags)
            return tmp
        }
        return []
    }

}
