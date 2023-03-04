SUMMARY = "The Image to run the hometerminal with debugging tools"
AUTHOR = "Tom Geelen"

LICENSE = "CLOSED"

require home-center-common.inc

IMAGE_FEATURES:qemu-homely-x86-64 += "\
    debug-tweaks \
"

IMAGE_FEATURES:hometerminal-cm4 += "\
    debug-tweaks \
"

IMAGE_INSTALL:hometerminal-cm4 += "\
    evtest \
    i2c-tools \
    weston-examples \
    strace \
    wayland-utils \
"