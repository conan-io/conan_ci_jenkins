FROM ubuntu:eoan

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

ENV PY27 /root/.pyenv/versions/2.7.17/bin/python
ENV PY35 /root/.pyenv/versions/3.5.9/bin/python
ENV PY37 /root/.pyenv/versions/3.7.6/bin/python
ENV PY38 /root/.pyenv/versions/3.8.1/bin/python

ENV CMAKE_3_16_4 /root/conan_packages/.conan/data/cmake/3.16.4/_/_/package/44fcf6b9a7fb86b2586303e3db40189d3b511830/bin/cmake
ENV CMAKE_3_16_3 /root/conan_packages/.conan/data/cmake/3.16.3/_/_/package/44fcf6b9a7fb86b2586303e3db40189d3b511830/bin/cmake

ENV GCC_7 /usr/bin/gcc-7
ENV GXX_7 /usr/bin/g++-7
ENV GCC_9 /usr/bin/gcc-9
ENV GXX_9 /usr/bin/g++-9

ENV CLANG_7 /usr/bin/clang-7
ENV CLANGXX_7 /usr/bin/clang++-7
ENV CLANG_9 /usr/bin/clang-9
ENV CLANGXX_9 /usr/bin/clang++-9


RUN apt-get -qq update \
    && apt-get -qq install -y --no-install-recommends \
       sudo ca-certificates \
       # pyenv requires
       make build-essential libssl-dev zlib1g-dev libbz2-dev \
       libreadline-dev libsqlite3-dev wget curl llvm libncurses5-dev libncursesw5-dev \
       xz-utils tk-dev libffi-dev liblzma-dev python-openssl git \
    \
    # Install pyenv stuff
    && curl -L https://github.com/pyenv/pyenv-installer/raw/master/bin/pyenv-installer | bash \
    && export PATH="/root/.pyenv/bin:$PATH" \
    && pyenv init - \
    && pyenv virtualenv-init - \
    && pyenv update \
    && pyenv install 3.8.1 \
    && pyenv install 3.7.6 \
    && pyenv install 3.5.9 \
    && pyenv install 2.7.17

# Install CMake versions (using Conan) and register alternatives
RUN $PY38 -m venv ./conan_tmp && cd ./conan_tmp \
    && ./bin/pip install conan \
    && export CONAN_USER_HOME=/root/conan_packages/ \
    && ./bin/conan install cmake/3.16.4@ \
    && ./bin/conan install cmake/3.16.3@ \
    && ./bin/pip uninstall -y conan \
    && cd .. && rm -fr ./conan_tmp

# Alternatives for CMake
COPY select_cmake.sh /root/select_cmake.sh
RUN chmod +x /root/select_cmake.sh \
    && update-alternatives --install /usr/bin/cmake cmake /root/conan_packages/.conan/data/cmake/3.16.4/_/_/package/44fcf6b9a7fb86b2586303e3db40189d3b511830/bin/cmake 10 \
    && update-alternatives --install /usr/bin/cmake cmake /root/conan_packages/.conan/data/cmake/3.16.3/_/_/package/44fcf6b9a7fb86b2586303e3db40189d3b511830/bin/cmake 20

# Install compilers: g++ and clang++
RUN apt-get -qq install -y --no-install-recommends \
    clang-9 clang-7 \
    g++-9-multilib g++-7-multilib

# Alternatives for GCC
COPY select_gcc.sh /root/select_gcc.sh
RUN chmod +x /root/select_gcc.sh &&\
    update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-9 10 &&\
    update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-7 20 &&\
    update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-9 10 &&\
    update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-7 20

# Alternatives for Clang
COPY select_clang.sh /root/select_clang.sh
RUN chmod +x /root/select_clang.sh &&\
    update-alternatives --install /usr/bin/clang clang /usr/bin/clang-9 10 &&\
    update-alternatives --install /usr/bin/clang clang /usr/bin/clang-7 20 &&\
    update-alternatives --install /usr/bin/clang++ clang++ /usr/bin/clang++-9 10 &&\
    update-alternatives --install /usr/bin/clang++ clang++ /usr/bin/clang++-7 20

COPY select_alternatives.sh /root/select_alternatives.sh
RUN chmod +x /root/select_alternatives.sh
ENTRYPOINT ["/root/select_alternatives.sh" ]
CMD ["gcc", "9", "clang", "9", "cmake", "3.16.4"]
