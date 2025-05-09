FROM ubuntu:focal

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

ENV PY36 3.6.15
ENV PY38 3.8.6
ENV PY39 3.9.2
ENV PY312 3.12.3

ENV CMAKE_3_15_7 /usr/share/cmake-3.15.7/bin/cmake
ENV CMAKE_3_16_9 /usr/share/cmake-3.16.9/bin/cmake
ENV CMAKE_3_17_5 /usr/share/cmake-3.17.5/bin/cmake
ENV CMAKE_3_19_7 /usr/share/cmake-3.19.7/bin/cmake
ENV CMAKE_3_23_5 /usr/share/cmake-3.23.5/bin/cmake

ENV GCC_7 /usr/bin/gcc-7
ENV GXX_7 /usr/bin/g++-7
ENV GCC_9 /usr/bin/gcc-9
ENV GXX_9 /usr/bin/g++-9
ENV GCC_11 /usr/bin/gcc-11
ENV GXX_11 /usr/bin/g++-11

ENV CLANG_7 /usr/bin/clang-7
ENV CLANGXX_7 /usr/bin/clang++-7
ENV CLANG_9 /usr/bin/clang-9
ENV CLANGXX_9 /usr/bin/clang++-9

WORKDIR /root
RUN apt-get -qq update \
    && apt-get -qq install -y --no-install-recommends \
       sudo ca-certificates \
       # pyenv requires
       make build-essential libssl-dev zlib1g-dev libbz2-dev \
       libreadline-dev libsqlite3-dev wget curl llvm libncurses5-dev libncursesw5-dev \
       xz-utils tk-dev libffi-dev liblzma-dev python-openssl git wget sudo file \
       libtool autoconf \
       autotools-dev automake linux-libc-dev subversion subversion-tools ninja-build \
       gcc-arm-linux-gnueabihf g++-arm-linux-gnueabihf unzip \
    # fix: asm/errno.h: No such file or directory
    && ln -s /usr/include/asm-generic/ /usr/include/asm \
    # Added repository for GCC 11
    && apt-get -qq install -y software-properties-common \
    && add-apt-repository -y ppa:ubuntu-toolchain-r/test \
    && apt-get -qq update \

    # Install CMake versions
    && wget https://github.com/Kitware/CMake/releases/download/v3.15.7/cmake-3.15.7-Linux-x86_64.tar.gz \
    && tar -xvzf cmake-3.15.7-Linux-x86_64.tar.gz && mv cmake-3.15.7-Linux-x86_64 /usr/share/cmake-3.15.7  \
    && wget https://github.com/Kitware/CMake/releases/download/v3.16.9/cmake-3.16.9-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.16.9-Linux-x86_64.tar.gz && mv cmake-3.16.9-Linux-x86_64 /usr/share/cmake-3.16.9 \
    && wget https://github.com/Kitware/CMake/releases/download/v3.17.5/cmake-3.17.5-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.17.5-Linux-x86_64.tar.gz && mv cmake-3.17.5-Linux-x86_64 /usr/share/cmake-3.17.5 \
    && wget https://github.com/Kitware/CMake/releases/download/v3.19.7/cmake-3.19.7-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.19.7-Linux-x86_64.tar.gz && mv cmake-3.19.7-Linux-x86_64 /usr/share/cmake-3.19.7 \
    && wget https://github.com/Kitware/CMake/releases/download/v3.23.5/cmake-3.23.5-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.23.5-Linux-x86_64.tar.gz && mv cmake-3.23.5-linux-x86_64/ /usr/share/cmake-3.23.5 \

    # Alternatives for CMake
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_3_15_7 10 \
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_3_16_9 20 \
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_3_17_5 30 \
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_3_19_7 40 \
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_3_23_5 50 \
    #
    # Install compilers: g++ and clang++
    && apt-get -qq install -y --no-install-recommends \
       clang-9 clang-7 \
       g++-11-multilib g++-9-multilib g++-7-multilib \
    #
    # Alternatives for GCC
    && update-alternatives --install /usr/bin/gcc gcc $GCC_9 10 \
    && update-alternatives --install /usr/bin/gcc gcc $GCC_7 20 \
    && update-alternatives --install /usr/bin/g++ g++ $GXX_9 10 \
    && update-alternatives --install /usr/bin/g++ g++ $GXX_7 20 \
    && update-alternatives --install /usr/bin/gcc gcc $GCC_11 30 \
    && update-alternatives --install /usr/bin/g++ g++ $GXX_11 30 \
    #
    # Alternatives for Clang
    && update-alternatives --install /usr/bin/clang clang $CLANG_9 10 \
    && update-alternatives --install /usr/bin/clang clang $CLANG_7 20 \
    && update-alternatives --install /usr/bin/clang++ clang++ $CLANGXX_9 10 \
    && update-alternatives --install /usr/bin/clang++ clang++ $CLANGXX_7 20

RUN wget https://dl.google.com/android/repository/android-ndk-r23c-linux.zip && \
    unzip android-ndk-r23c-linux.zip -d /opt/ && \
    rm android-ndk-r23c-linux.zip

# Bazel installation
ENV BAZEL_6_3_2 6.3.2
ENV BAZEL_7_1_2 7.1.2

RUN apt install -y apt-transport-https curl gnupg \
    # Bazel 6.3.2
    && curl -LO https://github.com/bazelbuild/bazel/releases/download/$BAZEL_6_3_2/bazel-$BAZEL_6_3_2-linux-x86_64 \
    && chmod 755 bazel-$BAZEL_6_3_2-linux-x86_64 \
    && mv bazel-$BAZEL_6_3_2-linux-x86_64 bazel \
    && mkdir -p /usr/share/bazel-$BAZEL_6_3_2/bin \
    && mv bazel /usr/share/bazel-$BAZEL_6_3_2/bin/ \
    # Bazel 7.1.2
    && curl -LO https://github.com/bazelbuild/bazel/releases/download/$BAZEL_7_1_2/bazel-$BAZEL_7_1_2-linux-x86_64 \
    && chmod 755 bazel-$BAZEL_7_1_2-linux-x86_64 \
    && mv bazel-$BAZEL_7_1_2-linux-x86_64 bazel \
    && mkdir -p /usr/share/bazel-$BAZEL_7_1_2/bin \
    && mv bazel /usr/share/bazel-$BAZEL_7_1_2/bin/

# Fish installation
RUN apt-add-repository ppa:fish-shell/release-3 \
    && apt -qq update \
    && apt -qq install -y fish --no-install-recommends --no-install-suggests

    # Clean the image
RUN rm -rf /var/lib/apt/lists && rm -rf /root/*.tar.gz

RUN useradd -ms /bin/bash conan && echo "conan:conan" | chpasswd && adduser conan sudo
RUN echo "ALL ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers
USER conan
WORKDIR /home/conan
ENV PATH /home/conan/.pyenv/shims:/home/conan/.pyenv/bin:$PATH
RUN curl -L https://github.com/pyenv/pyenv-installer/raw/master/bin/pyenv-installer | bash \
    && pyenv init - \
    && pyenv virtualenv-init - \
    && pyenv update \
    && pyenv install $PY312 \
    && pyenv install $PY39 \
    && pyenv install $PY38 \
    && pyenv install $PY36 \
    && pyenv global $PY39

COPY --chown=conan \
     select_cmake.sh \
     select_gcc.sh \
     select_clang.sh \
     select_alternatives.sh \
     /home/conan/

RUN chmod +x /home/conan/*.sh
RUN sudo /home/conan/select_alternatives.sh gcc 9 clang 9 cmake 3.15.7

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
    && systemctl enable docker

COPY jenkins-slave /usr/local/bin/jenkins-slave
COPY entrypoint.sh /opt/entrypoint.sh

ARG AGENT_VERSION=3248.v65ecb_254c298

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

USER conan
