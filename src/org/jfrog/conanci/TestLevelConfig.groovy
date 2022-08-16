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

                        for (sl in ["Windows", "Macos", "M1Macos", "Linux"]) {
                            pyVers[sl] = []
                            pyVers[sl].addAll(info["pyvers"][sl])
                        }
                    }
                }
            }
        }
    }


    List<String> getEffectivePyvers(String nodeLabel){

        def allPyvers = ["Macos": ['py39', 'py38', 'py36'],
                         "M1Macos": ['py39', 'py38', 'py36'],
                         "Linux": ['py39', 'py38', 'py37', 'py36'],
                         "Windows": ['py39', 'py38', 'py36']]

        def developPyvers  = ["Macos": ['py36'],
                              "M1Macos": ['py36'],
                              "Linux": ['py36'],
                              "Windows": ['py36']]

        def reducedPyvers  = ["Macos": ['py36'],
                              "M1Macos": ['py36'],
                              "Linux": ['py36'],
                              "Windows": ['py36']]

        if (script.env.BRANCH_NAME =~ /(^release.*)|(^master)/ || script.env.JOB_NAME == "ConanNightly") {
            return allPyvers[nodeLabel]
        }
        else if (script.env.BRANCH_NAME == "develop") {
            return developPyvers[nodeLabel]
        }
        else {
            reducedPyvers[nodeLabel].addAll(this.pyVers[nodeLabel])
            return reducedPyvers[nodeLabel]
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
