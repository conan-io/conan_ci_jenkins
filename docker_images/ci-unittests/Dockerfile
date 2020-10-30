FROM ubuntu:eoan

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

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
    && pyenv install 3.8.1 \
    && pyenv install 3.7.6 \
    && pyenv install 3.5.9 \
    && pyenv install 2.7.17

# Remove not needed things
USER root
RUN apt-get -qq remove make build-essential git gcc \
    && rm -rf /var/lib/apt/lists
ENTRYPOINT ["/bin/bash"]

USER conan
WORKDIR /home/conan

# To run the tests
# Launch the container:
#   docker run -it --rm IMAGENAME -vPATH_WITH_CONAN_SOURCES:/home/conan/sources /bin/bash
# Select the python version:
#   pyenv global 3.8.1
# Upgrade pip (recommended):
#    pip install --upgrade pip
# Install requirements:
#    pip install -r source/conans/requirements.txt
#    pip install -r source/conans/requirements_dev.txt
#    pip install -r source/conans/requirements_server.txt
# Prepare PYTHONPATH:
#   export PYTHONPATH=$(pwd)
# Run pytest:
#   cd source && pytest
