FROM ubuntu:focal

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

ENV PY27 2.7.18
ENV PY36 3.6.12
ENV PY38 3.8.6
ENV PY39 3.9.2

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
    && pyenv install $PY39 \
    && pyenv install $PY38 \
    && pyenv install $PY36 \
    && pyenv install $PY27

# Remove not needed things
USER root
RUN apt-get -qq remove make build-essential git gcc \
    && rm -rf /var/lib/apt/lists
ENTRYPOINT ["/bin/bash"]

USER conan
WORKDIR /home/conan
