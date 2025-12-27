#!/usr/bin/env bash
./gradlew quarkusBuild \
  -Dquarkus.package.jar.enabled=true \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.builder=podman \
  -Dquarkus.container-image.image=localhost/wipu:1.0

podman save -o wipu-quarkus.tar localhost/wipu:1.0

scp wipu-quarkus.tar fw@hilda.123k.org:/home/fw/wipu/images
ssh fw@hilda.123k.org podman load -i /home/fw/wipu/images/wipu-quarkus.tar
  #cd /opt/images
  #podman load -i wipu-quarkus.tar
  #podman run --rm -p 8080:8080 \
  #  -e WIPU_BASIC_USER=... \
  #  -e WIPU_BASIC_PASSWORD=... \
  #  localhost/wipu:1.0
