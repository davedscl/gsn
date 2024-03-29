#!/bin/sh
fpm -s tar -t deb --after-install "./post-install.sh" --deb-init "./gsn" -n "gsn" -v 1.1.7  --url http://gsn.epfl.ch --description "GSN is a stream processor middleware" -m "gsn@epfl.ch"  -d 'openjdk-7-jdk' -d 'maven' --vendor "LSIR-EPFL" --license "GPLv3+" ../gsn-1.1.7-pack.tar.gz
fpm -s tar -t rpm --after-install "./post-install.sh" --rpm-init "./gsn" -n "gsn" -v 1.1.7  --url http://gsn.epfl.ch --description "GSN is a stream processor middleware" -m "gsn@epfl.ch"  -d 'openjdk-7-jdk' -d 'maven' --vendor "LSIR-EPFL" --license "GPLv3+" ../gsn-1.1.7-pack.tar.gz
