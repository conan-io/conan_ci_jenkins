FROM ubuntu:22.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && ln -fs /usr/share/zoneinfo/Etc/UTC /etc/localtime \
    && apt-get install -y tzdata \
    && dpkg-reconfigure --frontend noninteractive tzdata

RUN apt-get install -y software-properties-common \
    && add-apt-repository ppa:deadsnakes/ppa \
    && apt-get update

RUN apt-get install -y python3.12 python3.12-venv python3.12-dev \
    python3.12-distutils \  
    enchant-2 git make latexmk texlive-latex-base texlive-fonts-recommended \
    texlive-fonts-extra texlive-latex-extra texlive-xetex xindy graphviz \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

RUN update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.12 1

RUN curl -sS https://bootstrap.pypa.io/get-pip.py -o get-pip.py \
    && python3.12 get-pip.py \
    && rm get-pip.py

RUN python3.12 -m pip install --upgrade pip setuptools

COPY jenkins-slave /usr/local/bin/jenkins-slave
COPY entrypoint.sh /opt/entrypoint.sh

ARG AGENT_VERSION=3248.v65ecb_254c298

RUN apt-get -qq update \
    && apt-get -q install -y openjdk-17-jre-headless curl \
    && apt-get -q clean -y \
    && rm -rf /var/lib/apt/lists/* \
    && rm -f /var/cache/apt/*.bin \
    && curl --create-dirs -sSLo /usr/share/jenkins/slave.jar https://repo.jenkins-ci.org/public/org/jenkins-ci/main/remoting/${AGENT_VERSION}/remoting-${AGENT_VERSION}.jar \
    && chmod 755 /usr/share/jenkins \
    && chmod 644 /usr/share/jenkins/slave.jar \
    && chmod +x /opt/entrypoint.sh /usr/local/bin/jenkins-slave

ENTRYPOINT ["/opt/entrypoint.sh"]
