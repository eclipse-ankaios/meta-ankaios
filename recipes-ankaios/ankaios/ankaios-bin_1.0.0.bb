require ankaios-common.inc

SUMMARY = "Eclipse Ankaios: Lightweight container runtime for embedded Linux (prebuilt binaries)"
DESCRIPTION = "Eclipse Ankaios is a lightweight container runtime for embedded Linux systems. This recipe installs the official prebuilt release binaries."

inherit systemd update-rc.d

ANKAIOS_GITHUB_REPO = "eclipse-ankaios/ankaios"
ANKAIOS_RELEASE_TAG = "v${PV}"

SRC_URI = "\
    file://state.yaml \
    file://ank-server.conf \
    file://ank-agent.conf \
    file://ank-server.service \
    file://ank-agent.service \
    file://ank-server \
    file://ank-agent \
    https://raw.githubusercontent.com/${ANKAIOS_GITHUB_REPO}/${ANKAIOS_RELEASE_TAG}/LICENSE;name=license;downloadfilename=LICENSE \
"

SRC_URI:append:qemux86-64 = " https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-amd64.tar.gz;name=bin-amd64"
SRC_URI:append:raspberrypi4-64 = " https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-arm64.tar.gz;name=bin-arm64"
SRC_URI:append:raspberrypi5 = " https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-arm64.tar.gz;name=bin-arm64"

SRC_URI[bin-amd64.sha256sum] = "26c3d122baf0cc952bfe37c7861f3f911ea2b8407771be29258beba5b3bc458e"
SRC_URI[bin-arm64.sha256sum] = "19a3185c2e3c29f65f79a9b857745cd168db2f9030541b8408e2c8af3837a428"
SRC_URI[license.sha256sum] = "cfc7749b96f63bd31c3c42b5c471bf756814053e847c10f3eb003417bc523d30"

# The binaries are only unpacked
S = "${UNPACKDIR}"

# Package split:
# - ank-server-bin: server binary + server config (+ systemd unit)
# - ank-agent-bin: agent binary + agent config (+ systemd unit)
# - ank-bin: CLI binary ("ank")
# - ankaios-bin (${PN}): meta package pulling server+agent+cli
PACKAGE_BEFORE_PN = "ank-server-bin ank-agent-bin ank-bin"

FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = "ank-server-bin ank-agent-bin ank-bin"

FILES:ank-server-bin = " \
    ${bindir}/ank-server \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/ank-server.service', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/ank-server', '', d)} \
    ${sysconfdir}/ankaios/state.yaml \
    ${sysconfdir}/ankaios/ank-server.conf \
"
FILES:ank-agent-bin = " \
    ${bindir}/ank-agent \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/ank-agent.service', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/ank-agent', '', d)} \
    ${sysconfdir}/ankaios/ank-agent.conf \
"
FILES:ank-bin = "${bindir}/ank"

RPROVIDES:ank-server-bin += "virtual/ank-server"
RPROVIDES:ank-agent-bin += "virtual/ank-agent"

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
    install -m 0644 ank-server.conf ${D}${sysconfdir}/ankaios/
    install -m 0644 ank-agent.conf ${D}${sysconfdir}/ankaios/

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
