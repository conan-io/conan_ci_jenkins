# conan_ci_jenkins

This repo contains the tools we use to run Conan test suite:
 * Groovy library
 * Python scripts
 * Docker images


## Groovy library

It is available in our Jenkins machine and can be [loaded within a Jenkinsfile](https://github.com/conan-io/conan/blob/develop/.ci/jenkins/Jenkinsfile):

```
@Library('conan_ci') _
conanCI.runBuild(this)
```

This library generates the pipeline dynamically based on the branch name, flags activated
in the PR itself,...


## Python scripts

Python code in this repo is responsible of running the actual test suite. It receives the 
information from the job and creates the command line to run the tests in the Conan repo.


## Docker images

Our Jenkins uses docker to isolate the environment where the tests run. This repository is
responsible of generating the docker images we are going to use.

**Force generation of docker images**: docker images are only generated if there is a change
in the `./docker_images` subfolder (typically a change in the Dockerfile). This behavior can
be overridden adding the string `docker_images` to the commit message.

### [conanio/conantests](https://hub.docker.com/r/conanio/conantests)

Legacy docker image based on `conanio/gcc5` image. It adds several Python versions and
the `meson` tool.
