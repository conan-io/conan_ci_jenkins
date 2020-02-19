#!/usr/bin/env groovy
import groovy.json.JsonSlurper


def call(String branchName, String GH_TOKEN) {
    echo "conanci::readPRTags(branchName=${branchName}, GH_TOKEN=${GH_TOKEN})"

    final String TAG_PYVERS = "#PYVERS:"
    final String TAG_TAGS = "#TAGS:"
    final String TAG_REVISIONS = "#REVISIONS:"

    if (!branchName.startsWith("PR-")) {
        error("branchName '${branchName}' is not a PR");
    }
    String prNumber = branchName.substring(3);

    // GET /repos/:owner/:repo/pulls/:pull_number
    String getResult = new URL('http://api.github.com/repos/conan-io/conan/pulls/${prNumber}').text
    List json = new JsonSlurper().parseText(getResult)
    echo json


    //URL apiUrl = "https://some.website/api/someFunction".toURL()
    //List json = new JsonSlurper().parse(apiUrl.newReader())

}
