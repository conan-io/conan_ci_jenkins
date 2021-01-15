# ci-functional

This image contains the same Python versions as _ci-unittests_, but it also contains **system tools**
like _Git_, _CMake_, _GCC_ or _Clang_. 
The installed versions can  be selected as the preferred alternative
when starting the container:

```
docker run -it ci-functional:latest gcc 7 clang 9 cmake 3.16.4
```
