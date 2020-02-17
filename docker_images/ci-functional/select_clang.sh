#!/bin/sh

if [ -z "$1" ]; then
    echo "usage: $0 <version>" 1>&2
    exit 1
fi

echo "Set Clang version $1"

if [ ! -f "/usr/bin/clang-$1" ] || [ ! -f "/usr/bin/clang++-$1" ]; then
    echo "no such version clang/clang++ installed" 1>&2
    exit 1
fi

update-alternatives --set clang "/usr/bin/clang-$1"
update-alternatives --set clang++ "/usr/bin/clang++-$1"
