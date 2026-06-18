# Provides storage.conf and subuid/subgid for rootless container support.
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://subuid file://subgid"

do_install:append() {
	install -d ${D}${sysconfdir}
	install -m 0644 ${UNPACKDIR}/subuid ${D}${sysconfdir}/subuid
	install -m 0644 ${UNPACKDIR}/subgid ${D}${sysconfdir}/subgid
}
