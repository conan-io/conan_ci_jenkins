FROM ubuntu:eoan

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

ENV PY27 /root/.pyenv/versions/2.7.17/bin/python
ENV PY35 /root/.pyenv/versions/3.5.9/bin/python
ENV PY37 /root/.pyenv/versions/3.7.6/bin/python
ENV PY38 /root/.pyenv/versions/3.8.1/bin/python

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

# Install compilers: g++ and clang++
RUN apt-get -qq install -y --no-install-recommends \
    clang-9 clang-7 \
    g++-9-multilib g++-7-multilib

#ENTRYPOINT ["/bin/bash"]
