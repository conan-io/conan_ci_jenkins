FROM ubuntu:trusty
# Pyinstaller links dynamically with libc, backward compatible needed
LABEL maintainer="Conan.io <info@conan.io>"
RUN apt-get update
# Workaround the let's encrypt issue: https://superuser.com/questions/1679204/curl-on-ubuntu-14-all-lets-encrypt-certificates-are-expired-error-60
RUN rm /etc/ssl/certs/DST_Root_CA_X3.pem
RUN apt remove python python3 python3-pip python-pip -y
RUN apt-get install build-essential git libexpat1-dev libssl-dev zlib1g-dev \
  libncurses5-dev libbz2-dev liblzma-dev \
  libsqlite3-dev libffi-dev tcl-dev linux-headers-generic libgdbm-dev \
  libreadline-dev tk tk-dev wget openssl -y
RUN wget https://www.openssl.org/source/openssl-1.1.1l.tar.gz
RUN tar -xvzf openssl-1.1.1l.tar.gz
RUN cd openssl-1.1.1l && ./config no-unit-test --prefix=/openssl --openssldir=/openssl && make && make install_sw
RUN wget https://www.python.org/ftp/python/3.8.11/Python-3.8.11.tgz
RUN tar -xvzf Python-3.8.11.tgz
RUN cd Python-3.8.11 && LD_LIBRARY_PATH="/openssl/lib/:/openssl/lib64/" ./configure --with-openssl=/openssl --enable-shared && make && make install
ENV LD_LIBRARY_PATH="/openssl/lib/:/openssl/lib64/:/usr/local/lib/:${LD_LIBRARY_PATH}"
RUN ls /usr/local/lib
RUN update-alternatives --install /usr/local/bin/pip pip /usr/local/bin/pip3 1
RUN update-alternatives --install /usr/local/bin/python python /usr/local/bin/python3 1
RUN python --version
WORKDIR /root

COPY jenkins-slave /usr/local/bin/jenkins-slave
COPY entrypoint.sh /opt/entrypoint.sh

ARG AGENT_VERSION=3248.v65ecb_254c298

RUN apt-get -qq update \
    && apt-get -q install -y openjdk-17-jre-headless \
    && apt-get -q clean -y \
    && rm -rf /var/lib/apt/lists/* \
    && rm -f /var/cache/apt/*.bin \
    && curl --create-dirs -sSLo /usr/share/jenkins/slave.jar https://repo.jenkins-ci.org/public/org/jenkins-ci/main/remoting/${AGENT_VERSION}/remoting-${AGENT_VERSION}.jar \
    && chmod 755 /usr/share/jenkins \
    && chmod 644 /usr/share/jenkins/slave.jar \
    && chmod +x /opt/entrypoint.sh /usr/local/bin/jenkins-slave

ENTRYPOINT ["/opt/entrypoint.sh"]
