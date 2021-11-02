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
RUN python3 --version
WORKDIR /root
