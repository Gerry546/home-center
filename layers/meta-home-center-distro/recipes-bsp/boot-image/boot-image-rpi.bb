SUMMARY = "Recipe to build the vfat boot directory"
DESCRIPTION = "Separate recipe to build vfat boot image to be included in RAUC artifacts list"
AUTHOR = "Tom Geelen"
LICENSE = "MIT"

inherit nopackages deploy

do_fetch[noexec] = "1"
do_patch[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
deltask do_populate_sysroot

do_deploy[depends] += "\
    dosfstools-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    rpi-bootfiles:do_deploy \
    ${@bb.utils.contains('RPI_USE_U_BOOT', '1', 'u-boot:do_deploy', '',d)} \
"

do_deploy () {
    FATSOURCEDIR="${WORKDIR}/${PN}/"
    mkdir -p ${FATSOURCEDIR}

    cp ${DEPLOY_DIR_IMAGE}/boot.scr ${FATSOURCEDIR}/boot.scr
    cp ${DEPLOY_DIR_IMAGE}/bootfiles/* ${FATSOURCEDIR}/
    cp ${DEPLOY_DIR_IMAGE}/Image ${FATSOURCEDIR}/Image
    

    MKDOSFS_EXTRAOPTS="-S 512"
    FATIMG="${WORKDIR}/${PN}.vfat"
    BLOCKS=65536

    mkdosfs -n "BOOT" ${MKDOSFS_EXTRAOPTS} -C ${FATIMG} \
                    ${BLOCKS}
    # Copy FATSOURCEDIR recursively into the image file directly
    mcopy -i ${FATIMG} -s ${FATSOURCEDIR}/* ::/
    chmod 644 ${FATIMG}

    mv ${FATIMG} ${DEPLOYDIR}/
}

do_deploy[cleandirs] += "${WORKDIR}/${PN}"

addtask deploy after do_install
