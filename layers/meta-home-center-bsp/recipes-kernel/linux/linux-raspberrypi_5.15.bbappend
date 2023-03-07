FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://0001-added-reterminal-devicetree-overlay.patch  \
    file://rauc.cfg \
"

CMDLINE:remove = "root=/dev/mmcblk0p2"