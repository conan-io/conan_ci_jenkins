FROM conanio/gcc5

LABEL maintainer="Luis Martinez de Bartolome <luism@jfrog.com>"

RUN sudo apt-get update \
    && sudo apt-get -qq install -y --no-install-recommends software-properties-common \
    && sudo add-apt-repository ppa:deadsnakes/ppa -y \
    && sudo apt-get update \
    && sudo apt-get -qq install -y --no-install-recommends \
    python-software-properties \
    python3.4 \
    python3.5 \
    python3.6 \
    python3.7 \
    python3.8 \
    python-setuptools \
    python-dev \
    python3.4-dev \
    python3.5-dev \
    python3.6-dev \
    python3.7-dev \
    python3.8-dev \
    python3.8-distutils \
    golang \
    pkg-config \
    && sudo rm -rf /var/lib/apt/lists/* \
    && sudo pip install --upgrade pip \
    && wget -P tmp/ https://bootstrap.pypa.io/get-pip.py \
    && sudo python3 tmp/get-pip.py \
    && sudo pip install virtualenv==16.7.9 \
    && pip3 install PyGithub==1.45 \
    && pip3 install meson==0.53.1

USER conan
WORKDIR /home/conan
RUN mkdir -p /home/conan/.conan
