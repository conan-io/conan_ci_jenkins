FROM ubuntu:focal

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

ENV PY38 /home/conan/.pyenv/versions/3.8.6/bin/python

RUN apt-get -qq update \
    && apt-get -qq install -y --no-install-recommends \
       sudo ca-certificates \
       # pyenv requires
       make build-essential libssl-dev zlib1g-dev libbz2-dev \
       libreadline-dev libsqlite3-dev wget curl llvm libncurses5-dev libncursesw5-dev \
       xz-utils tk-dev libffi-dev liblzma-dev python-openssl git

RUN useradd -ms /bin/bash conan
USER conan
WORKDIR /home/conan
ENV PATH /home/conan/.pyenv/shims:/home/conan/.pyenv/bin:$PATH
RUN curl -L https://github.com/pyenv/pyenv-installer/raw/master/bin/pyenv-installer | bash \
    && pyenv init - \
    && pyenv virtualenv-init - \
    && pyenv update \
    && pyenv install 3.8.6 \
    && pyenv global 3.8.6 \
    && pip install "virtualenv<20.0.0" \
    && pip install tox \
    && pip install tox-venv \
    && pip install requests

USER root

# install docker
RUN apt-get -qq update \
    && apt-get -qq install -y --no-install-recommends \
    apt-transport-https \
    curl \
    gnupg-agent \
    software-properties-common

RUN curl -kfsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - \
    && add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable" \
    && apt-get update \
    && apt-get install -y docker-ce docker-ce-cli containerd.io \
    && rm -rf /var/lib/apt/lists/* \
    && usermod -aG docker conan \
    && systemctl enable docker \
    && pip install docker-compose && pip install docker==6.1.3

RUN pip install PyGithub \
    && pip install meson

COPY jenkins-slave /usr/local/bin/jenkins-slave
COPY entrypoint.sh /opt/entrypoint.sh

ARG AGENT_VERSION=3309.v27b_9314fd1a_4

RUN apt-get -qq update \
    && apt-get -q install -y openjdk-17-jdk openjdk-17-jre-headless curl \
    && apt-get -q clean -y \
    && rm -rf /var/lib/apt/lists/* \
    && rm -f /var/cache/apt/*.bin \
    && pip3 install --no-cache virtualenv \
    && curl --create-dirs -sSLo /usr/share/jenkins/slave.jar https://repo.jenkins-ci.org/public/org/jenkins-ci/main/remoting/${AGENT_VERSION}/remoting-${AGENT_VERSION}.jar \
    && chmod 755 /usr/share/jenkins \
    && chmod 644 /usr/share/jenkins/slave.jar \
    && chmod +x /opt/entrypoint.sh /usr/local/bin/jenkins-slave

ENTRYPOINT ["/opt/entrypoint.sh"]
