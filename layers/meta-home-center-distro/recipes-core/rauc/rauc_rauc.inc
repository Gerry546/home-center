FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# additional dependencies required to run RAUC on the target
RDEPENDS:${PN}:qemu-homely-x86-64 += "grub-editenv e2fsprogs-mke2fs"
