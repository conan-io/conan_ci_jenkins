#!/usr/bin/env groovy
import groovy.json.JsonSlurper


def call(String branchName) {
    echo "conanci::readPRTags(branchName=${branchName})"

    final String TAG_PYVERS = "#PYVERS:"
    final String TAG_TAGS = "#TAGS:"
    final String TAG_REVISIONS = "#REVISIONS:"

    if (!branchName.startsWith("PR-")) {
        error("branchName '${branchName}' is not a PR");
    }
    String prNumber = branchName.substring(3);

    // GET /repos/:owner/:repo/pulls/:pull_number
    def get = new URL("https://api.github.com/repos/conan-io/conan/pulls/${prNumber}").openConnection();
    def getRC = get.getResponseCode();
    if(getRC.equals(200)) {
        def json = new JsonSlurper().parseText(get.getInputStream().getText())
        echo json.body
    }

    //String getResult = new URL("https://api.github.com/repos/conan-io/conan/pulls/${prNumber}").text
    //List json = new JsonSlurper().parseText(getResult)
    //echo json


    //URL apiUrl = "https://some.website/api/someFunction".toURL()
    //List json = new JsonSlurper().parse(apiUrl.newReader())

}
