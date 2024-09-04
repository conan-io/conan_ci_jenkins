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
            script.echo(this.toString())
        }
    }

    List<String> getEffectivePyvers(String nodeLabel){

        def allPyvers = ["M2Macos": ['py39', 'py38', 'py36'],
                         "Linux": ['py39', 'py38', 'py37', 'py36'],
                         "Windows": ['py39', 'py38', 'py36']]

        def developPyvers  = ["M2Macos": ['py36'],
                              "Linux": ['py36'],
                              "Windows": ['py36']]

        def reducedPyvers  = ["M2Macos": ['py36'],
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
