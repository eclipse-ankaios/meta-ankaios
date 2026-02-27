require ankaios-common.inc

SUMMARY = "Eclipse Ankaios: Lightweight container runtime for embedded Linux (prebuilt binaries)"
DESCRIPTION = "Eclipse Ankaios is a lightweight container runtime for embedded Linux systems. This recipe installs the official prebuilt release binaries."

inherit systemd

ANKAIOS_GITHUB_REPO = "eclipse-ankaios/ankaios"
ANKAIOS_RELEASE_TAG = "v${PV}"

SRC_URI = "\
    file://state.yaml \
    file://ank-server.conf \
    file://ank-agent.conf \
    https://raw.githubusercontent.com/${ANKAIOS_GITHUB_REPO}/${ANKAIOS_RELEASE_TAG}/LICENSE;name=license;downloadfilename=LICENSE \
"

SRC_URI:append:qemux86-64 = " https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-amd64.tar.gz;name=bin-amd64"
SRC_URI:append:raspberrypi4-64 = " https://github.com/${ANKAIOS_GITHUB_REPO}/releases/download/${ANKAIOS_RELEASE_TAG}/ankaios-linux-arm64.tar.gz;name=bin-arm64"

SRC_URI[bin-amd64.sha256sum] = "26c3d122baf0cc952bfe37c7861f3f911ea2b8407771be29258beba5b3bc458e"
SRC_URI[bin-arm64.sha256sum] = "19a3185c2e3c29f65f79a9b857745cd168db2f9030541b8408e2c8af3837a428"
SRC_URI[license.sha256sum] = "cfc7749b96f63bd31c3c42b5c471bf756814053e847c10f3eb003417bc523d30"

# The binaries are only unpacked
S = "${UNPACKDIR}"

SYSTEMD_SERVICE:${PN} = "ank-server.service ank-agent.service"
SYSTEMD_AUTO_ENABLE = "enable"

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

    install -d ${D}${systemd_system_unitdir}

    cat > ${D}${systemd_system_unitdir}/ank-server.service << EOF
[Unit]
Description=Ankaios server

[Service]
Environment=\"RUST_LOG=info\"
ExecStart=${bindir}/ank-server

[Install]
WantedBy=default.target
EOF

    cat > ${D}${systemd_system_unitdir}/ank-agent.service << EOF
[Unit]
Description=Ankaios agent

[Service]
Environment=\"RUST_LOG=info\"
ExecStart=${bindir}/ank-agent

[Install]
WantedBy=default.target
EOF
}

FILES:${PN} += "${systemd_system_unitdir} ${sysconfdir}/ankaios"
