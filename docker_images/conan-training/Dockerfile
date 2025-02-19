FROM ubuntu:jammy

LABEL maintainer="Conan.io <info@conan.io>"

ARG DEBIAN_FRONTEND=noninteractive

# Update apt and install basic dependencies including asciinema
RUN apt-get update && apt-get install -y \
    sudo \
    ca-certificates \
    build-essential \
    libssl-dev \
    zlib1g-dev \
    libbz2-dev \
    libreadline-dev \
    libsqlite3-dev \
    wget \
    curl \
    git \
    unzip \
    software-properties-common \
    asciinema && \
    rm -rf /var/lib/apt/lists/*

# Add PPAs for gcc-13 and Python 3.9, then install them
RUN add-apt-repository -y ppa:ubuntu-toolchain-r/test && \
    add-apt-repository -y ppa:deadsnakes/ppa && \
    apt-get update && \
    apt-get install -y gcc-13 g++-13 python3.9 python3.9-venv python3.9-dev && \
    rm -rf /var/lib/apt/lists/*

# Install CMake 3.27.1
RUN wget https://github.com/Kitware/CMake/releases/download/v3.27.1/cmake-3.27.1-linux-x86_64.tar.gz && \
    tar -xzf cmake-3.27.1-linux-x86_64.tar.gz && \
    mv cmake-3.27.1-linux-x86_64 /usr/local/cmake && \
    ln -s /usr/local/cmake/bin/cmake /usr/local/bin/cmake && \
    rm cmake-3.27.1-linux-x86_64.tar.gz

# Create user 'conan', set password, and grant sudo privileges
RUN useradd -ms /bin/bash conan && echo "conan:conan" | chpasswd && adduser conan sudo && \
    echo "conan ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# Create /home/training directory and assign ownership to 'conan'
RUN mkdir -p /home/training && chown conan:conan /home/training

# Switch to user 'conan'
USER conan
WORKDIR /home/conan

# Set Python 3.9 as the default python3 alternative
RUN sudo update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.9 1

# Install Rust toolchain non-interactively using rustup
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y

# Add Cargo's bin directory to PATH
ENV PATH="/home/conan/.cargo/bin:${PATH}"

# Install agg from the asciinema Git repository using Cargo
RUN cargo install --git https://github.com/asciinema/agg

CMD ["bash"]
