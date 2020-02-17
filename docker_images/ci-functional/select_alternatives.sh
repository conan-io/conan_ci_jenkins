#!/bin/bash

# echo "usage: $0 gcc $1 $2 clang $3 $4" 1>&2

if [[ $1 != gcc || $3 != clang || $5 != cmake ]]; then
    echo "usage: $0 gcc <version> clang <version> cmake <version>" 1>&2
    exit 1
fi

/root/select_gcc.sh $2
/root/select_clang.sh $4
/root/select_cmake.sh $6

# Pass the rest of the arguments to /bin/bash
all_args=("$@")
rest_args=("${all_args[@]:6}")

echo "/bin/bash ${rest_args[@]}" 1>&2
/bin/bash "${rest_args[@]}"
