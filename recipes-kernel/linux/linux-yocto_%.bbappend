FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

SRC_URI += "${@'file://podman-nft.scc' if d.getVar('DISTRO_CODENAME') == 'kirkstone' else ''}"
