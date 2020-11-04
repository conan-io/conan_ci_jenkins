#!/bin/sh

if [ -z "$1" ]; then
    echo "usage: $0 <version>" 1>&2
    exit 1
fi

echo "Set CMake version $1" 1>&2
update-alternatives --set cmake "/usr/share/cmake-$1/bin/cmake"
