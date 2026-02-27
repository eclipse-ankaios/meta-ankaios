SUMMARY = "SysV init scripts for Eclipse Ankaios"
DESCRIPTION = "Provides SysV init scripts for ank-server and ank-agent (Eclipse Ankaios)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit update-rc.d

SRC_URI = "\
    file://ank-server \
    file://ank-agent \
"

S = "${UNPACKDIR}"

PACKAGES =+ "${PN}-server ${PN}-agent"

FILES:${PN}-server = "${sysconfdir}/init.d/ank-server"
FILES:${PN}-agent = "${sysconfdir}/init.d/ank-agent"

# Make ${PN} a meta package that only pulls in the two init-script packages.
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN} = "${PN}-server ${PN}-agent"
RDEPENDS:${PN}-server = "ankaios"
RDEPENDS:${PN}-agent = "ankaios"

INITSCRIPT_PACKAGES = "${PN}-server ${PN}-agent"

INITSCRIPT_NAME:${PN}-server = "ank-server"
INITSCRIPT_PARAMS:${PN}-server = "start 70 2 3 4 5 . stop 30 0 1 6 ."

INITSCRIPT_NAME:${PN}-agent = "ank-agent"
INITSCRIPT_PARAMS:${PN}-agent = "start 71 2 3 4 5 . stop 29 0 1 6 ."

do_install() {
    install -d ${D}${sysconfdir}/init.d

    install -m 0755 ${UNPACKDIR}/ank-server ${D}${sysconfdir}/init.d/ank-server
    install -m 0755 ${UNPACKDIR}/ank-agent ${D}${sysconfdir}/init.d/ank-agent
}
