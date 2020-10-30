# Docker images

## ci-unittests

This image contains only Python, **no system tools are available (there is no git, cmake or compiler)**.
You can list the python available versions with pyenv:

```
$> pyenv versions

* system (set by /root/.pyenv/version)
  2.7.17
  3.5.9
  3.7.6
  3.8.1

```

And select one before launching the tests:

```
pyenv global 3.8.1
```py

## ci-functional

This image contains the same Python versions as `ci-unittests`, but it also contains **system tools**
like Git, CMake, GCC or Clang. The installed versions are available using environment variables and
they can also be selected as the preferred alternative when starting the container:

```
docker run -it travis-ci-ci-functional:latest gcc 7 clang 9 cmake 3.16.4
```
