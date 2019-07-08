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

    def init(){
        if (script.env.BRANCH_NAME =~ /(^PR-.*)/) {
            readPRTags()
        }
    }

    private void readPRTags(){
        script.node("Linux"){
            script.stage("Check PR tags"){
                script.withCredentials([script.string(credentialsId: 'GH_TOKEN', variable: 'GH_TOKEN')]) {
                    script.checkout(script.scm)
                    script.sh("docker pull conanio/conantests")
                    script.docker.image('conanio/conantests').inside("-e GH_TOKEN=${script.GH_TOKEN}"){
                        script.sh(script: "python .ci/jenkins/pr_tags.py out.json ${script.env.BRANCH_NAME}")
                        List<String> info = script.readJSON file: 'out.json'

                        excludedTags.addAll(jsonToStringList(info["tags"]))
                        revisions = jsonToStringList(info["revisions"])

                        for (sl in ["Windows", "Macos", "Linux"]) {
                            pyVers[sl].addAll(jsonToStringList(info["pyvers"][sl]))
                        }
                    }
                }
            }
        }
    }

    private List<String> jsonToStringList(object){
        List<String> ret = []
        for(o in object){
            ret.add(o.toString())
        }
        return ret
    }

    List<String> getEffectivePyvers(String nodeLabel){

        def allPyvers = ["Macos": ['py37', 'py36', 'py34', 'py27'],
                         "Linux": ['py37', 'py36', 'py34', 'py27'],
                         "Windows": ['py37', 'py36', 'py34', 'py27']]

        if (script.env.BRANCH_NAME =~ /(^release.*)|(^master)/) {
            return allPyvers[nodeLabel]
        }

        if (script.env.JOB_NAME == "ConanNightly"){
            return allPyvers[nodeLabel]
        }

        if (script.env.BRANCH_NAME =~ /(^PR-.*)/) {

            def reducedPyvers  = ["Macos": ['py36'],
                                  "Linux": ['py36', 'py27'],
                                  "Windows": ['py36']]

            reducedPyvers[nodeLabel].addAll(this.pyVers[nodeLabel])
            return reducedPyvers[nodeLabel]
        }
        else{
            return allPyvers[nodeLabel]
        }
    }

    List<Boolean> getEffectiveRevisionsConfigurations(){

        if (this.revisions || script.env.BRANCH_NAME =~ /(^release.*)|(^master)/ || script.env.JOB_NAME == "ConanNightly") {
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
