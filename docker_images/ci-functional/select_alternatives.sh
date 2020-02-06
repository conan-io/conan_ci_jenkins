#!/bin/bash

echo "usage: $0 gcc $1 $2 clang $3 $4" 1>&2

if [[ $1 != gcc || $3 != clang ]]; then
    echo "usage: $0 gcc <version> clang <version>" 1>&2
    exit 1
fi

/root/select_gcc.sh $2
/root/select_clang.sh $4

/bin/bash
