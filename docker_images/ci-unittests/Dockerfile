FROM ubuntu:eoan

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

#COPY environment.sh /root/environment.sh

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
    && curl -L https://github.com/pyenv/pyenv-installer/raw/master/bin/pyenv-installer | bash \
    && echo 'export PATH="/root/.pyenv/bin:$PATH"' >> ~/.bashrc \
    && echo 'eval "$(pyenv init -)"' >> ~/.bashrc \
    && echo 'eval "$(pyenv virtualenv-init -)"' >> ~/.bashrc \
    && . ~/.bashrc \
    && pyenv update \
    && pyenv install 3.8.1 \
    && pyenv install 3.7.6 \
    && pyenv install 3.5.9 \
    && pyenv install 2.7.17

RUN apt-get -qq remove make build-essential git

#ENTRYPOINT ["/bin/bash"]
