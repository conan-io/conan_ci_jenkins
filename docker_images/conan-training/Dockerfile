FROM ubuntu:jammy

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    sudo \
    ca-certificates \
    build-essential \
    libssl-dev \
    zlib1g-dev \
    libbz2-dev \
    libreadline-dev \
    libsqlite3-dev \
    wget \
    curl \
    git \
    unzip \
    software-properties-common \
    asciinema \
    ffmpeg \
    python3-pip \
    python3-venv && \
    rm -rf /var/lib/apt/lists/*

RUN add-apt-repository -y ppa:ubuntu-toolchain-r/test && \
    apt-get update && \
    apt-get install -y gcc-13 g++-13 && \
    rm -rf /var/lib/apt/lists/*

RUN update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-13 100 && \
    update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-13 100

RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get update && apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

RUN wget https://github.com/Kitware/CMake/releases/download/v3.27.1/cmake-3.27.1-linux-x86_64.tar.gz && \
    tar -xzf cmake-3.27.1-linux-x86_64.tar.gz && \
    mv cmake-3.27.1-linux-x86_64 /usr/local/cmake && \
    ln -s /usr/local/cmake/bin/cmake /usr/local/bin/cmake && \
    rm cmake-3.27.1-linux-x86_64.tar.gz

RUN ln -s /usr/bin/python3 /usr/bin/python

RUN useradd -ms /bin/bash conan && echo "conan:conan" | chpasswd && adduser conan sudo && \
    echo "conan ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

RUN mkdir -p /home/training && chown conan:conan /home/training

USER conan
WORKDIR /home/conan

RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y

ENV PATH="/home/conan/.cargo/bin:${PATH}"

RUN cargo install --git https://github.com/asciinema/agg

USER root
COPY jenkins-slave /usr/local/bin/jenkins-slave
COPY entrypoint.sh /opt/entrypoint.sh
ARG AGENT_VERSION=3248.v65ecb_254c298
RUN apt-get -qq update && \
    apt-get -q install -y openjdk-17-jre-headless curl && \
    apt-get -q clean -y && \
    rm -rf /var/lib/apt/lists/* && \
    rm -f /var/cache/apt/*.bin && \
    pip3 install --no-cache virtualenv && \
    curl --create-dirs -sSLo /usr/share/jenkins/slave.jar https://repo.jenkins-ci.org/public/org/jenkins-ci/main/remoting/${AGENT_VERSION}/remoting-${AGENT_VERSION}.jar && \
    chmod 755 /usr/share/jenkins && \
    chmod 644 /usr/share/jenkins/slave.jar && \
    chmod +x /opt/entrypoint.sh /usr/local/bin/jenkins-slave

ENTRYPOINT ["/opt/entrypoint.sh"]
USER conan
