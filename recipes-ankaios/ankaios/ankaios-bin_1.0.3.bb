SUMMARY = "Eclipse Ankaios: Lightweight container orchestrator for embedded Linux"
DESCRIPTION = "Eclipse Ankaios is a lightweight container/workload orchestrator for embedded Linux systems. This recipe installs the official prebuilt release binaries."
HOMEPAGE = "https://eclipse-ankaios.github.io/ankaios/latest/"
BUGTRACKER = "https://github.com/eclipse-ankaios/ankaios/issues"

SECTION = "virtualization/tools"

CVE_PRODUCT = "eclipse:ankaios"

# Ankaios itself is Apache-2.0; the prebuilt binaries statically link the same
# >700 vendored Rust crates as the from-source build, so the effective license is
# their union (derived from upstream deny.toml, release-1.0).
LICENSE = "Apache-2.0 & MIT & BSD-3-Clause & ISC & Unicode-3.0 & Zlib & MIT-0 & OpenSSL"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

# Container runtime stack is not supported on MIPS
COMPATIBLE_HOST = "^(?!(qemu)?mips).*"

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
    file://ank-server.default \
    file://ank-agent.default \
"

SRC_URI:append:qemux86-64 = " https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-amd64.tar.gz;name=bin-amd64"
SRC_URI:append = " ${@' https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-arm64.tar.gz;name=bin-arm64' if d.getVar('MACHINE') in ('raspberrypi4-64', 'raspberrypi5') else ''}"

SRC_URI[bin-amd64.sha256sum] = "ff3cdd88566f25ec3bc3362b2152bf498ce977f987baf82252c10d03d6261fc6"
SRC_URI[bin-arm64.sha256sum] = "62cce84744884f7d4c76f94fed7f952f5390bd9e2d96b2c446bbfd2ab4f07a64"
SRC_URI[license.sha256sum] = "cfc7749b96f63bd31c3c42b5c471bf756814053e847c10f3eb003417bc523d30"

# The binaries are only unpacked into WORKDIR
S = "${WORKDIR}"

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
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/default/ank-agent', '', d)} \
    ${sysconfdir}/ankaios/ank-agent.conf \
"

FILES:ank-server-bin += "\
    ${bindir}/ank-server \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/ank-server.service', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/ank-server', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/default/ank-server', '', d)} \
    ${sysconfdir}/ankaios/state.yaml \
    ${sysconfdir}/ankaios/ank-server.conf \
"

RDEPENDS:${PN} = "ank-agent-bin ank-bin ank-server-bin"

# The agent shells out to a container runtime to launch workloads
# Ankaios does not require the following to start, but running workloads requires Podman and/or containerd + nerdctl.
RRECOMMENDS:ank-agent-bin += "podman \
                              containerd \
                              nerdctl \
                              "

CONFFILES:ank-bin = "${sysconfdir}/ankaios/ank.conf"
CONFFILES:ank-server-bin = "${sysconfdir}/ankaios/state.yaml ${sysconfdir}/ankaios/ank-server.conf ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/default/ank-server', '', d)}"
CONFFILES:ank-agent-bin = "${sysconfdir}/ankaios/ank-agent.conf ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/default/ank-agent', '', d)}"

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'ank-server-bin ank-agent-bin', '', d)}"
SYSTEMD_SERVICE:ank-server-bin = "ank-server.service"
SYSTEMD_SERVICE:ank-agent-bin = "ank-agent.service"
SYSTEMD_AUTO_ENABLE:ank-server-bin = "enable"
SYSTEMD_AUTO_ENABLE:ank-agent-bin = "enable"

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
        install -m 0644 ${WORKDIR}/ank-server.service ${D}${systemd_system_unitdir}/
        install -m 0644 ${WORKDIR}/ank-agent.service ${D}${systemd_system_unitdir}/
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/init-scripts/ank-server ${D}${sysconfdir}/init.d/ank-server
        install -m 0755 ${WORKDIR}/init-scripts/ank-agent ${D}${sysconfdir}/init.d/ank-agent

        install -d ${D}${sysconfdir}/default
        install -m 0644 ${WORKDIR}/ank-server.default ${D}${sysconfdir}/default/ank-server
        install -m 0644 ${WORKDIR}/ank-agent.default ${D}${sysconfdir}/default/ank-agent
    fi
}
