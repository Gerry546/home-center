#!/bin/bash

set -e

while getopts i: flag
do
    case "${flag}" in
        i) ipaddress=${OPTARG};
    esac
done

echo "Trying to connect on $ipaddress"
scp build/tmp/deploy/images/hometerminal-cm4/home-center-bundle-debug-aarch64-hometerminal-cm4.raucb root@${ipaddress}:/tmp
ssh root@${ipaddress} rauc install /tmp/home-center-bundle-debug-aarch64-hometerminal-cm4.raucb
ssh root@${ipaddress} reboot