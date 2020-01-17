FROM conanio/tests-base

LABEL maintainer="Conan.io <info@conan.io>"

RUN apt-get -qq update \
    && apt-get -qq install -y --no-install-recommends \
       ninja-build \
       g++-9-multilib
 
RUN echo "/usr/bin/g++-9" >> /root/environment.sh