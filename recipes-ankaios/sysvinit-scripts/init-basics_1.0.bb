SUMMARY = "Init some basic services"
DESCRIPTION = "Init some basic services like cgroups and dhcp to get the machine in a usable state"
HOMEPAGE = "https://eclipse-ankaios.github.io/ankaios/latest/"
LICENSE = "Apache-2.0"
# nooelint: oelint.var.licenseremotefile
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit update-rc.d

SRC_URI = "file://init-cgroup-dhcp \
           file://udhcpc-dns-only"

S = "${UNPACKDIR}"

FILES:${PN} += "\
    ${datadir}/udhcpc/dns-only \
    ${sbindir}/rootfs-resize \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/rootfs-resize', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/rootfs-resize.service', '', d)} \
"

INITSCRIPT_NAME = "init-cgroup-dhcp"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 60 0 1 6 ."

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 init-cgroup-dhcp ${D}${sysconfdir}/init.d/init-cgroup-dhcp

    install -d ${D}${datadir}/udhcpc
    install -m 0755 ${UNPACKDIR}/udhcpc-dns-only ${D}${datadir}/udhcpc/dns-only
}
