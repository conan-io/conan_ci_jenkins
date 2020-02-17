#!/bin/sh

if [ -z "$1" ]; then
    echo "usage: $0 <version>" 1>&2
    exit 1
fi

echo "Set CMake version $1" 1>&2
update-alternatives --set cmake "/root/conan_packages/.conan/data/cmake/$1/_/_/package/44fcf6b9a7fb86b2586303e3db40189d3b511830/bin/cmake"
