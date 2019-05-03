#!/bin/sh
set -ex

DIR=$(cd $(dirname $0); pwd)
cd $DIR

# Get the current jar file
JAR=$(basename $(ls -1 $DIR/target/*.app))
VERSION=$(echo $JAR | sed 's/virt-hostd-//' | sed 's/.app//')

echo "Building Virt HostD V$VERSION ($JAR) CI: ${CI_COMMIT_SHORT_SHA:-local}"

# Build the hostd server image
cp $DIR/target/$JAR $DIR/src/main/docker/hostd/$JAR
cd $DIR/src/main/docker/hostd
docker build --build-arg version=$VERSION -t registry.gitlab.com/virt/virt/hostd:latest -t registry.gitlab.com/virt/virt/hostd:$VERSION -t registry.gitlab.com/virt/virt/hostd:${CI_COMMIT_SHORT_SHA:-local} .
docker push registry.gitlab.com/virt/virt/hostd:latest
docker push registry.gitlab.com/virt/virt/hostd:$VERSION
[ -z "$CI_COMMIT_SHORT_SHA" ] || docker push registry.gitlab.com/virt/virt/hostd:$CI_COMMIT_SHORT_SHA
cd $DIR
