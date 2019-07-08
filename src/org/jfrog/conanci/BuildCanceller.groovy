package org.jfrog.conanci


class BuildCanceller {

    static def cancelPrevious(script){
        def jenkins = script.get_jenkins_instance()
        if(!jenkins){
            return
        }
        // Iterate through current project runs
        def jobname = script.env.JOB_NAME
        def buildnum = script.env.BUILD_NUMBER.toInteger()
        def names = script.get_jenkins_instance().getJobNames()

        for (name in names){
            if(name.startsWith(jobname)){
                def job = jenkins.getItemByFullName(name)
                for (build in job.builds) {
                    if (!build.isBuilding()) { continue; }
                    if (buildnum > build.getNumber().toInteger()){
                        script.echo "Stopping previous build: ${build}"
                        build.doStop();
                    }
                }
            }
        }
    }
}
