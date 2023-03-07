FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "\
    file://boot-nfs-tftp.cmd.in \
    file://boot-default.cmd.in \
"

inherit logging

do_compile() {
    if [ "${ENABLE_TFTP_NFS}" = "1" ]; then
        bbnote "Using TFTP/NFS boot.scr script"
        sed -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        "${WORKDIR}/boot-nfs-tftp.cmd.in" > "${WORKDIR}/boot.cmd"
    else
        bbnote "Using Default boot.scr script"
        sed -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        "${WORKDIR}/boot-default.cmd.in" > "${WORKDIR}/boot.cmd"
    fi

    mkimage -A ${UBOOT_ARCH} -T script -C none -n "Boot script" -d "${WORKDIR}/boot.cmd" boot.scr
}
