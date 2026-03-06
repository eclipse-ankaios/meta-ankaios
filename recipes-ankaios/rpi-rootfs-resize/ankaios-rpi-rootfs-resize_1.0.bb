SUMMARY = "First-boot rootfs expansion on Raspberry Pi"
DESCRIPTION = "Expands the root partition and filesystem to use the full SD card on first boot."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd update-rc.d

SRC_URI = "\
    file://ankaios-rpi-rootfs-resize.sh \
    file://ankaios-rpi-rootfs-resize.init \
    file://ankaios-rpi-rootfs-resize.service \
"

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "parted e2fsprogs-resize2fs"

SYSTEMD_SERVICE:${PN} = "ankaios-rpi-rootfs-resize.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

INITSCRIPT_NAME = "ankaios-rpi-rootfs-resize"
INITSCRIPT_PARAMS = "start 03 2 3 4 5 . stop 97 0 1 6 ."

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/ankaios-rpi-rootfs-resize.sh ${D}${sbindir}/ankaios-rpi-rootfs-resize

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/ankaios-rpi-rootfs-resize.init ${D}${sysconfdir}/init.d/ankaios-rpi-rootfs-resize

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/ankaios-rpi-rootfs-resize.service ${D}${systemd_unitdir}/system/
}

FILES:${PN} += "\
    ${sbindir}/ankaios-rpi-rootfs-resize \
    ${sysconfdir}/init.d/ankaios-rpi-rootfs-resize \
    ${systemd_unitdir}/system/ankaios-rpi-rootfs-resize.service \
"
