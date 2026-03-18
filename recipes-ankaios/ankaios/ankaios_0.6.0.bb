# Recipe for Eclipse Ankaios v0.6.0 with vendored dependencies

require ankaios-common.inc

SUMMARY = "Eclipse Ankaios: Lightweight container runtime for embedded Linux (vendored sources)"
DESCRIPTION = "Eclipse Ankaios is a lightweight container runtime for embedded Linux systems. This recipe uses the official vendored source archive."

SRC_URI = "git://github.com/eclipse-ankaios/ankaios.git;protocol=https;branch=main"
# v0.6.0 tag commit
SRCREV = "58b26c026cebf54207a6dae7e52df29648065dd7"

SRC_URI += "file://state.yaml"
SRC_URI += "file://ank-server.conf"
SRC_URI += "file://ank-agent.conf"
SRC_URI += "file://ank-server.service"
SRC_URI += "file://ank-agent.service"
SRC_URI += "file://ank-server"
SRC_URI += "file://ank-agent"

require ${BPN}-crates-${PV}.inc

# Ankaios is written in Rust, using vendored dependencies
inherit cargo cargo-update-recipe-crates systemd update-rc.d

# Build dependencies
DEPENDS += "protobuf-native"

# Package split:
# - ank-server: server binary + server config + service/init script
# - ank-agent: agent binary + agent config + service/init script
# - ank: CLI binary ("ank")
# - ankaios (${PN}): meta package pulling server+agent+cli
PACKAGE_BEFORE_PN = "ank-server ank-agent ank"

FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "ank-server ank-agent ank"

FILES:ank-server = " \
    ${bindir}/ank-server \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/ank-server.service', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/ank-server', '', d)} \
    ${sysconfdir}/ankaios/state.yaml \
    ${sysconfdir}/ankaios/ank-server.conf \
"
FILES:ank-agent = " \
    ${bindir}/ank-agent \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/ank-agent.service', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/ank-agent', '', d)} \
    ${sysconfdir}/ankaios/ank-agent.conf \
"
FILES:ank = "${bindir}/ank"

RPROVIDES:ank-server += "virtual/ank-server"
RPROVIDES:ank-agent += "virtual/ank-agent"

CONFFILES:ank-server = "${sysconfdir}/ankaios/state.yaml ${sysconfdir}/ankaios/ank-server.conf"
CONFFILES:ank-agent = "${sysconfdir}/ankaios/ank-agent.conf"

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'ank-server ank-agent', '', d)}"
SYSTEMD_SERVICE:ank-server = "ank-server.service"
SYSTEMD_SERVICE:ank-agent = "ank-agent.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'ank-server ank-agent', '', d)}"
INITSCRIPT_NAME:ank-server = "ank-server"
INITSCRIPT_PARAMS:ank-server = "start 70 2 3 4 5 . stop 30 0 1 6 ."
INITSCRIPT_NAME:ank-agent = "ank-agent"
INITSCRIPT_PARAMS:ank-agent = "start 71 2 3 4 5 . stop 29 0 1 6 ."

do_install() {
    # Install binaries
    install -d ${D}${bindir}
    install -m 0755 ${B}/target/${CARGO_TARGET_SUBDIR}/ank-server ${D}${bindir}/
    install -m 0755 ${B}/target/${CARGO_TARGET_SUBDIR}/ank-agent ${D}${bindir}/
    install -m 0755 ${B}/target/${CARGO_TARGET_SUBDIR}/ank ${D}${bindir}/

    # Install configuration directory
    install -d ${D}${sysconfdir}/ankaios
    install -m 0644 ${UNPACKDIR}/state.yaml ${D}${sysconfdir}/ankaios/
    install -m 0644 ${UNPACKDIR}/ank-server.conf ${D}${sysconfdir}/ankaios/
    install -m 0644 ${UNPACKDIR}/ank-agent.conf ${D}${sysconfdir}/ankaios/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/ank-server.service ${D}${systemd_system_unitdir}/
        install -m 0644 ${UNPACKDIR}/ank-agent.service ${D}${systemd_system_unitdir}/
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${UNPACKDIR}/ank-server ${D}${sysconfdir}/init.d/ank-server
        install -m 0755 ${UNPACKDIR}/ank-agent ${D}${sysconfdir}/init.d/ank-agent
    fi

}
   