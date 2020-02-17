# Docker images

## ci-unittests

This image contains only Python, **no system tools are available (there is no git, cmake or compiler)**.
It provides several python versions that can be referenced using environment variables like PY27,
PY35, PY37,...

## ci-functional

This image contains the same Python versions as `ci-unittests`, and **system tools** like Git, CMake,
GCC or Clang. The installed versions are available using environment variables and they can also
be installed when starting the container:

```
docker run -it travis-ci-ci-functional:latest gcc 7 clang 9 cmake 3.16.4
```
