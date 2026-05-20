SUMMARY = "Eclipse Ankaios: Lightweight container orchestrator for embedded Linux"
DESCRIPTION = "Eclipse Ankaios is a lightweight container runtime for embedded Linux systems. This recipe installs the official prebuilt release binaries."
HOMEPAGE = "https://eclipse-ankaios.github.io/ankaios/latest/"
BUGTRACKER = "https://github.com/eclipse-ankaios/ankaios/issues"

SECTION = "base"

CVE_PRODUCT = "eclipse:ankaios"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit systemd update-rc.d

ANKAIOS_GITHUB_REPO = "eclipse-ankaios/ankaios"
ANKAIOS_RELEASE_TAG = "v${PV}"

SRC_URI = "\
    https://raw.githubusercontent.com/${ANKAIOS_GITHUB_REPO}/${ANKAIOS_RELEASE_TAG}/LICENSE;name=license;downloadfilename=LICENSE \
    file://state.yaml \
    file://ank.conf \
    file://ank-server.conf \
    file://ank-agent.conf \
    file://ank-server.service \
    file://ank-agent.service \
    file://ank-server;subdir=init-scripts \
    file://ank-agent;subdir=init-scripts \
"

SRC_URI:append:qemux86-64 = " https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-amd64.tar.gz;name=bin-amd64"
SRC_URI:append = " ${@' https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-arm64.tar.gz;name=bin-arm64' if d.getVar('MACHINE') in ('raspberrypi4-64', 'raspberrypi5') else ''}"

SRC_URI[bin-amd64.sha256sum] = "72ed5e90652465ea31b53f04f93b0b5b1bdaeb9e4fe8e1a8a1a77994adad2eca"
SRC_URI[bin-arm64.sha256sum] = "ac8a01f4a49123731aba00c9ce9c6ccf3322a3ef1c085cce7cc0bcd54accd1ec"
SRC_URI[license.sha256sum] = "cfc7749b96f63bd31c3c42b5c471bf756814053e847c10f3eb003417bc523d30"

# The binaries are only unpacked into UNPACKDIR
S = "${UNPACKDIR}"

# Package split:
# - ank-server-bin: server binary + server config (+ systemd unit)
# - ank-agent-bin: agent binary + agent config (+ systemd unit)
# - ank-bin: CLI binary ("ank")
# - ankaios-bin (${PN}): meta package pulling server+agent+cli
PACKAGE_BEFORE_PN = "ank-agent-bin ank-bin ank-server-bin"

ALLOW_EMPTY:${PN} = "1"

FILES:ank-bin += "${bindir}/ank \
    ${sysconfdir}/ankaios/ank.conf \
"

FILES:ank-agent-bin += "\
    ${bindir}/ank-agent \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/ank-agent.service', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/ank-agent', '', d)} \
    ${sysconfdir}/ankaios/ank-agent.conf \
"

FILES:ank-server-bin += "\
    ${bindir}/ank-server \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/ank-server.service', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/ank-server', '', d)} \
    ${sysconfdir}/ankaios/state.yaml \
    ${sysconfdir}/ankaios/ank-server.conf \
"

RDEPENDS:${PN} = "ank-agent-bin ank-bin ank-server-bin"

CONFFILES:ank-bin = "${sysconfdir}/ankaios/ank.conf"
CONFFILES:ank-server-bin = "${sysconfdir}/ankaios/state.yaml ${sysconfdir}/ankaios/ank-server.conf"
CONFFILES:ank-agent-bin = "${sysconfdir}/ankaios/ank-agent.conf"

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'ank-server-bin ank-agent-bin', '', d)}"
SYSTEMD_SERVICE:ank-server-bin = "ank-server.service"
SYSTEMD_SERVICE:ank-agent-bin = "ank-agent.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'ank-server-bin ank-agent-bin', '', d)}"
INITSCRIPT_NAME:ank-server-bin = "ank-server"
INITSCRIPT_PARAMS:ank-server-bin = "start 70 2 3 4 5 . stop 30 0 1 6 ."
INITSCRIPT_NAME:ank-agent-bin = "ank-agent"
INITSCRIPT_PARAMS:ank-agent-bin = "start 71 2 3 4 5 . stop 29 0 1 6 ."

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ank-server ${D}${bindir}/
    install -m 0755 ank-agent ${D}${bindir}/
    install -m 0755 ank ${D}${bindir}/

    install -d ${D}${sysconfdir}/ankaios
    install -m 0644 state.yaml ${D}${sysconfdir}/ankaios/
    install -m 0644 ank.conf ${D}${sysconfdir}/ankaios/
    install -m 0644 ank-server.conf ${D}${sysconfdir}/ankaios/
    install -m 0644 ank-agent.conf ${D}${sysconfdir}/ankaios/

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/ank-server.service ${D}${systemd_system_unitdir}/
        install -m 0644 ${UNPACKDIR}/ank-agent.service ${D}${systemd_system_unitdir}/
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${UNPACKDIR}/init-scripts/ank-server ${D}${sysconfdir}/init.d/ank-server
        install -m 0755 ${UNPACKDIR}/init-scripts/ank-agent ${D}${sysconfdir}/init.d/ank-agent
    fi
}
