@Grab('org.yaml:snakeyaml:1.8')

import hudson.plugins.git.GitSCM
import hudson.plugins.git.UserRemoteConfig
import hudson.plugins.git.BranchSpec
import jenkins.model.Jenkins
import org.jfrog.conanci.TestRunner

String causeClass = currentBuild.rawBuild.getCauses()[0].getClass().getName()
echo("Cause: ${causeClass}")
if(causeClass == "jenkins.branch.BranchIndexingCause"){
    exit("Branch indexing builds are blocked")
}


def runBuild(script, branch) {
    script.get_jenkins_instance = {
        return Jenkins.instance
    }

    new TestRunner(script, branch).run()
}

