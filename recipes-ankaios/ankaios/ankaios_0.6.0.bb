# Recipe for Eclipse Ankaios v0.6.0 with vendored dependencies

require ankaios-common.inc

SUMMARY = "Eclipse Ankaios: Lightweight container runtime for embedded Linux (vendored sources)"
DESCRIPTION = "Eclipse Ankaios is a lightweight container runtime for embedded Linux systems. This recipe uses the official vendored source archive."

SRC_URI = "git://github.com/eclipse-ankaios/ankaios.git;protocol=https;branch=main"
SRCREV = "58b26c026cebf54207a6dae7e52df29648065dd7"

SRC_URI += "file://state.yaml"
SRC_URI += "file://ank-server.conf"
SRC_URI += "file://ank-agent.conf"

require ${BPN}-crates.inc

S = "${WORKDIR}/git"

# Ankaios is written in Rust, using vendored dependencies
inherit cargo cargo-update-recipe-crates systemd

# Build dependencies
DEPENDS += "protobuf-native"

# Install systemd service files if systemd is enabled
SYSTEMD_SERVICE:${PN} = "ank-server.service ank-agent.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    # Install binaries
    install -d ${D}${bindir}
    install -m 755 ${B}/target/${CARGO_TARGET_SUBDIR}/ank-server ${D}${bindir}/
    install -m 755 ${B}/target/${CARGO_TARGET_SUBDIR}/ank-agent ${D}${bindir}/
    install -m 755 ${B}/target/${CARGO_TARGET_SUBDIR}/ank ${D}${bindir}/

    # Install configuration directory
    install -d ${D}${sysconfdir}/ankaios
    install -m 644 ${UNPACKDIR}/state.yaml ${D}${sysconfdir}/ankaios/
    install -m 644 ${UNPACKDIR}/ank-server.conf ${D}${sysconfdir}/ankaios/
    install -m 644 ${UNPACKDIR}/ank-agent.conf ${D}${sysconfdir}/ankaios/

    install -d ${D}${systemd_system_unitdir}
        
    # Create ank-server service file
    cat > ${D}${systemd_system_unitdir}/ank-server.service << EOF
[Unit]
Description=Ankaios server

[Service]
Environment="RUST_LOG=info"
ExecStart=${bindir}/ank-server

[Install]
WantedBy=default.target
EOF

    # Create ank-agent service file
    cat > ${D}${systemd_system_unitdir}/ank-agent.service << EOF
[Unit]
Description=Ankaios agent

[Service]
Environment="RUST_LOG=info"
ExecStart=${bindir}/ank-agent

[Install]
WantedBy=default.target
EOF

}
   