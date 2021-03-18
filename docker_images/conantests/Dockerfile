FROM conanio/gcc5

LABEL maintainer="Luis Martinez de Bartolome <luism@jfrog.com>"

RUN sudo apt-get update \
    && sudo apt-get -qq install -y --no-install-recommends golang pkg-config \
    && sudo rm -rf /var/lib/apt/lists/* \
    && PYTHON_CONFIGURE_OPTS="--enable-shared" pyenv install 2.7.16 \
    && PYTHON_CONFIGURE_OPTS="--enable-shared" pyenv install 3.5.7 \
    && PYTHON_CONFIGURE_OPTS="--enable-shared" pyenv install 3.6.9 \
    && PYTHON_CONFIGURE_OPTS="--enable-shared" pyenv install 3.8.1 \
    && pyenv global 3.8.1 \
    && python -m pip install --upgrade pip "virtualenv<20.0.0" PyGithub conan meson
    # Install CMake versions

USER root

RUN wget https://github.com/Kitware/CMake/releases/download/v3.15.7/cmake-3.15.7-Linux-x86_64.tar.gz \
    && tar -xvzf cmake-3.15.7-Linux-x86_64.tar.gz && mv cmake-3.15.7-Linux-x86_64 /usr/share/cmake-3.15.7  \
    && wget https://github.com/Kitware/CMake/releases/download/v3.16.9/cmake-3.16.9-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.16.9-Linux-x86_64.tar.gz && mv cmake-3.16.9-Linux-x86_64 /usr/share/cmake-3.16.9 \
    && wget https://github.com/Kitware/CMake/releases/download/v3.17.5/cmake-3.17.5-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.17.5-Linux-x86_64.tar.gz && mv cmake-3.17.5-Linux-x86_64 /usr/share/cmake-3.17.5 \
    && wget https://github.com/Kitware/CMake/releases/download/v3.19.7/cmake-3.19.7-Linux-x86_64.tar.gz   \
    && tar -xvzf cmake-3.19.7-Linux-x86_64.tar.gz && mv cmake-3.19.7-Linux-x86_64 /usr/share/cmake-3.19.7

USER conan
WORKDIR /home/conan
RUN mkdir -p /home/conan/.conan
