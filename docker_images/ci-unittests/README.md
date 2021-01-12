# ci-unittests

This image contains only Python, **no system tools are available (there is no git, cmake or
compiler)**. Its intentended use is running unit and integration tests in Conan CI. You can list the
python available versions with pyenv:

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
```
