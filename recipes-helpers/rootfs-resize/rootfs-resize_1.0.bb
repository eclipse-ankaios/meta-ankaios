SUMMARY = "First-boot rootfs expansion"
DESCRIPTION = "Expands the root partition and filesystem to use the full SD card on first boot."
HOMEPAGE = "https://eclipse-ankaios.github.io/ankaios/latest/"
LICENSE = "Apache-2.0"
# nooelint: oelint.var.licenseremotefile
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd update-rc.d

SRC_URI = "\
    file://rootfs-resize.sh \
    file://rootfs-resize.init \
    file://rootfs-resize.service \
"

S = "${UNPACKDIR}"

FILES:${PN} += "\
    ${sbindir}/rootfs-resize \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/rootfs-resize', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/rootfs-resize.service', '', d)} \
"

RDEPENDS:${PN} += "e2fsprogs-resize2fs parted"

SYSTEMD_SERVICE:${PN} = "rootfs-resize.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

INITSCRIPT_NAME = "rootfs-resize"
INITSCRIPT_PARAMS = "start 03 2 3 4 5 . stop 97 0 1 6 ."

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/rootfs-resize.sh ${D}${sbindir}/rootfs-resize

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${UNPACKDIR}/rootfs-resize.init ${D}${sysconfdir}/init.d/rootfs-resize
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/rootfs-resize.service ${D}${systemd_unitdir}/system/
    fi
}
