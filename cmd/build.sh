#!/bin/bash

# Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
# WSO2 Inc. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.

function showUsageAndExit() {
    echo "Insufficient or invalid options provided"
    echo
    echo "Usage: ./build.sh -t [target-file] -v [build-version] -f"
    echo -en "  -t\t"
    echo "[REQUIRED] Target file to build."
    echo -en "  -v\t"
    echo "[REQUIRED] Build version. If not specified a default value will be used."
    echo -en "  -f\t"
    echo "[OPTIONAL] Cross compile for all the list of platforms. If not specified, the specified target" \
        "file will be compiled only for the autodetected native platform."
    echo
    echo "Ex: ./build.sh -t mi.go -v 1.0.0 -f : Builds Micro Integrator CLI" \
        "version 1.0.0 for all platforms."
    echo
    exit 1
}

function detectPlatformSpecificBuild() {
    if [ ! -e "$platform" ]; then
      platform=$(uname -s)
      if [[ "${platform}" == "Linux" ]]; then
          platforms="linux/386/linux/i586 linux/amd64/linux/x64"
      elif [[ "${platform}" == "Darwin" ]]; then
          platforms="darwin/amd64/macosx/x64"
      else
          platforms="windows/386/windows/i586 windows/amd64/windows/x64"
      fi
    fi
}

while getopts :t:v:f FLAG; do
  case $FLAG in
    t)
      target=$OPTARG
      ;;
    v)
      build_version=$OPTARG
      ;;
    f)
      full_build="true"
      ;;
    \?)
      showUsageAndExit
      ;;
  esac
done

if [ ! -e "$target" ]; then
  echo "Target file is needed. "
  showUsageAndExit
  exit 1
fi

if [ -z "$build_version" ]
then
  echo "Build version is needed. "
  showUsageAndExit
fi


rootPath=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
buildDir="build/target"
buildPath="$rootPath/${buildDir}"

echo "Cleaning build path ${buildDir}..."
rm -rf $buildPath

filename=$(basename ${target})
baseDir=$(dirname ${target})
if [ ".go" == ${filename:(-3)} ]
then
    filename=${filename%.go}
fi

#platforms="darwin/amd64 freebsd/386 freebsd/amd64 freebsd/arm linux/386 linux/amd64 linux/arm windows/386 windows/amd64"
#platforms="linux/amd64/linux/x64"
#platforms="darwin/amd64/macosx/x64"
if [ "${full_build}" == "true" ]; then
# the following line give an error in MacOS
#    echo "Building "$'\e[1m'"${filename^^}:${build_version}"$'\e[0m'" for all platforms..."
    # string format: {GOOS}/{GOARCH}/{ZIP_FILE_OS_NAME}/{ZIP_FILE_ARCH_NAME}
    platforms="darwin/amd64/darwin/amd64 darwin/arm64/darwin/arm64 linux/386/linux/i586 linux/arm64/linux/arm64 linux/amd64/linux/amd64 windows/386/windows/i586 windows/amd64/windows/x64"
else
    detectPlatformSpecificBuild
    echo "Building "$'\e[1m'"${filename^^}:${build_version}"$'\e[0m'" for detected "$'\e[1m'"${platform}"$'\e[0m'" platform..."
fi

if [ ! -z "${cgo_enabled}" ]
then
  echo "CGO is disabled manually hence the generated output will be a static binary."
  export CGO_ENABLED=0
fi

go_executable=$(which go)
if [[ -x "$go_executable" ]] ; then
    echo "Go found in \$PATH"
else
    echo "Go not found in \$PATH"
    exit 1
fi

# run the completion.go file to get the bash completion script
# To do the string replace first build the script so that we have a consistent name
go build -gcflags=-trimpath=$GOPATH -asmflags=-trimpath=$GOPATH tools/generate_bash_completion_script.go
./generate_bash_completion_script
sed -i -e "s=./generate_bash_completion_script=mi=g" ./shell-completions/mi_bash_completion.sh
rm generate_bash_completion_script

filename=$(basename mi)
baseDir=$(dirname mi)
target=mi.go
filename=${filename%.go}
for platform in ${platforms}
do
    split=(${platform//\// })
    goos=${split[0]}
    goarch=${split[1]}
    pos=${split[2]}
    parch=${split[3]}

    # ensure output file name
    output="mi"
    test "$output" || output="$(basename mi | sed 's/\.go//')"

    # add exe to windows output
    [[ "windows" == "$goos" ]] && output="$output.exe"

    echo -en "\t - $goos/$goarch..."

    zipfile="$output-$build_version-$pos-$parch"
    zipdir="${buildPath}/$filename"
    mkdir -p $zipdir

    cp -r "${baseDir}/README.html" $zipdir > /dev/null 2>&1
    cp -r "${baseDir}/LICENSE.txt" $zipdir > /dev/null 2>&1

    if [[ "windows" != "$goos" ]]; then
      cp -r "${baseDir}/shell-completions/mi_bash_completion.sh" $zipdir > /dev/null 2>&1
    fi

    # set destination path for binary
    destination="$zipdir/$output"

    #echo "GOOS=$goos GOARCH=$goarch go build -x -o $destination $target"
    GOOS=$goos GOARCH=$goarch go build \
     -gcflags=-trimpath=$GOPATH -asmflags=-trimpath=$GOPATH \
     -ldflags "-X github.com/wso2/product-mi-tooling/cmd/cmd.Version=$build_version -X 'github.com/wso2/product-mi-tooling/cmd/cmd.BuildDate=$(date -u '+%Y-%m-%d %H:%M:%S UTC')'" -o $destination $target

    pwd=`pwd`
    cd $buildPath
    if [[ "windows" == "$goos" ]]; then
	zip -r "$zipfile.zip" $filename > /dev/null 2>&1
    else
    	tar czf "$zipfile.tar.gz" $filename > /dev/null 2>&1
    fi
    rm -rf $filename
    cd $pwd
    echo -en $'\e[1m\u2714\e[0m'
    echo
done

echo "Build complete!"
