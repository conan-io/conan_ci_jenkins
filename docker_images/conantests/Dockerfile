FROM conanio/gcc5

LABEL maintainer="Luis Martinez de Bartolome <luism@jfrog.com>"

RUN sudo apt-get update \
    && sudo apt-get -qq install -y --no-install-recommends golang pkg-config \
    && PYTHON_CONFIGURE_OPTS="--enable-shared" pyenv install 3.5.7 \
    && PYTHON_CONFIGURE_OPTS="--enable-shared" pyenv install 3.6.12 \
    && PYTHON_CONFIGURE_OPTS="--enable-shared" pyenv install 3.8.6 \
    && PYTHON_CONFIGURE_OPTS="--enable-shared" pyenv install 3.9.2 \
    && pyenv global 3.8.6 \
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

# Install Ninja

RUN wget https://github.com/ninja-build/ninja/archive/v1.10.2.tar.gz \
    && tar -xvzf v1.10.2.tar.gz && cd ninja-1.10.2 && ./configure.py --bootstrap \
    && cmake -Bbuild-cmake -H. && cmake --build build-cmake && cp ./build-cmake/ninja /usr/bin/

# Install Bazel

RUN apt install -y apt-transport-https curl gnupg \
    && curl -fsSL https://bazel.build/bazel-release.pub.gpg | gpg --dearmor > bazel.gpg \
    && mv bazel.gpg /etc/apt/trusted.gpg.d/ \
    && echo "deb [arch=amd64] https://storage.googleapis.com/bazel-apt stable jdk1.8" | sudo tee /etc/apt/sources.list.d/bazel.list \
    && apt update && apt install -y bazel \
    # Clean the image
    && rm -rf /var/lib/apt/lists && rm -rf /root/*.tar.gz

USER conan
WORKDIR /home/conan
RUN mkdir -p /home/conan/.conan
