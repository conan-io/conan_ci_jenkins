FROM ubuntu:focal

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

ENV PY27 /home/conan/.pyenv/versions/2.7.17/bin/python
ENV PY35 /home/conan/.pyenv/versions/3.5.9/bin/python
ENV PY37 /home/conan/.pyenv/versions/3.7.6/bin/python
ENV PY38 /home/conan/.pyenv/versions/3.8.1/bin/python

ENV CMAKE_2_8_12 /usr/share/cmake-2.8.12/bin/cmake
ENV CMAKE_3_7_2 /usr/share/cmake-3.7.2/bin/cmake
ENV CMAKE_3_15_7 /usr/share/cmake-3.15.7/bin/cmake
ENV CMAKE_3_19_2 /usr/share/cmake-3.19.2/bin/cmake

ENV GCC_7 /usr/bin/gcc-7
ENV GXX_7 /usr/bin/g++-7
ENV GCC_9 /usr/bin/gcc-9
ENV GXX_9 /usr/bin/g++-9

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
       autotools-dev automake linux-libc-dev subversion subversion-tools \
    # fix: asm/errno.h: No such file or directory
    && ln -s /usr/include/asm-generic/ /usr/include/asm \

    # Install CMake versions
    && wget https://cmake.org/files/v2.8/cmake-2.8.12.2-Linux-i386.tar.gz   \
    && tar -xvzf cmake-2.8.12.2-Linux-i386.tar.gz && mv cmake-2.8.12.2-Linux-i386 /usr/share/cmake-2.8.12 \

    && wget https://github.com/Kitware/CMake/releases/download/v3.7.2/cmake-3.7.2-Linux-x86_64.tar.gz \
    && tar -xvzf cmake-3.7.2-Linux-x86_64.tar.gz && mv cmake-3.7.2-Linux-x86_64 /usr/share/cmake-3.7.2  \

    && wget https://github.com/Kitware/CMake/releases/download/v3.15.7/cmake-3.15.7-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.15.7-Linux-x86_64.tar.gz && mv cmake-3.15.7-Linux-x86_64 /usr/share/cmake-3.15.7 \

    && wget https://github.com/Kitware/CMake/releases/download/v3.19.2/cmake-3.19.2-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.19.2-Linux-x86_64.tar.gz && mv cmake-3.19.2-Linux-x86_64 /usr/share/cmake-3.19.2 \

    # Alternatives for CMake
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_2_8_12 10 \
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_3_7_2 20 \
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_3_15_7 30 \
    && update-alternatives --install /usr/bin/cmake cmake $CMAKE_3_19_2 40 \
    #
    # Install compilers: g++ and clang++
    && apt-get -qq install -y --no-install-recommends \
       clang-9 clang-7 \
       g++-9-multilib g++-7-multilib \
    #
    # Alternatives for GCC
    && update-alternatives --install /usr/bin/gcc gcc $GCC_9 10 \
    && update-alternatives --install /usr/bin/gcc gcc $GCC_7 20 \
    && update-alternatives --install /usr/bin/g++ g++ $GXX_9 10 \
    && update-alternatives --install /usr/bin/g++ g++ $GXX_7 20 \
    #
    # Alternatives for Clang
    && update-alternatives --install /usr/bin/clang clang $CLANG_9 10 \
    && update-alternatives --install /usr/bin/clang clang $CLANG_7 20 \
    && update-alternatives --install /usr/bin/clang++ clang++ $CLANGXX_9 10 \
    && update-alternatives --install /usr/bin/clang++ clang++ $CLANGXX_7 20 \
    # Clean the image
    && rm -rf /var/lib/apt/lists && rm -rf /root/*.tar.gz

RUN useradd -ms /bin/bash conan && echo "conan:conan" | chpasswd && adduser conan sudo
RUN echo "ALL ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers
USER conan
WORKDIR /home/conan
ENV PATH /home/conan/.pyenv/shims:/home/conan/.pyenv/bin:$PATH
RUN curl -L https://github.com/pyenv/pyenv-installer/raw/master/bin/pyenv-installer | bash \
    && pyenv init - \
    && pyenv virtualenv-init - \
    && pyenv update \
    && pyenv install 3.8.1 \
    && pyenv install 3.7.6 \
    && pyenv install 3.5.9 \
    && pyenv install 2.7.17

COPY --chown=conan \
     select_cmake.sh \
     select_gcc.sh \
     select_clang.sh \
     select_alternatives.sh \
     /home/conan/

RUN chmod +x /home/conan/*.sh
RUN sudo /home/conan/select_alternatives.sh gcc 9 clang 9 cmake 3.15.7

ENTRYPOINT ["/home/conan/select_alternatives.sh" ]
CMD ["gcc", "9", "clang", "9", "cmake", "3.15.7"]
