FROM ubuntu:eoan

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

COPY environment.sh /root/environment.sh

RUN apt-get -qq update \
    && apt-get -qq install -y --no-install-recommends \
       sudo ca-certificates \
       make \
       build-essential \
       libssl-dev \
       zlib1g-dev \
       libbz2-dev \
       libreadline-dev \
       libsqlite3-dev \
       wget \
       curl \
       llvm \
       libncurses5-dev \
       libncursesw5-dev \
       xz-utils \
       tk-dev \
       libffi-dev \
       liblzma-dev \
       python-openssl \
       git \
       python3 \
    \
    && update-alternatives --install /usr/bin/python python /usr/bin/python3 1\
    \
    && curl -L https://github.com/pyenv/pyenv-installer/raw/master/bin/pyenv-installer | bash \
    && echo 'export PATH="/root/.pyenv/bin:$PATH"' >> ~/.bashrc \
    && echo 'eval "$(pyenv init -)"' >> ~/.bashrc \
    && echo 'eval "$(pyenv virtualenv-init -)"' >> ~/.bashrc \
    && . ~/.bashrc \
    && pyenv update \
    && pyenv install 3.8.1

#ENTRYPOINT ["/bin/bash"]
