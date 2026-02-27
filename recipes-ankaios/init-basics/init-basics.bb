DESCRIPTION = "Mount cgroups and start dhcp"
LICENSE = "CLOSED"

inherit update-rc.d

SRC_URI = "file://init-cgroup-dhcp"

S = "${UNPACKDIR}"

INITSCRIPT_NAME = "init-cgroup-dhcp"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 60 0 1 6 ."

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 init-cgroup-dhcp ${D}${sysconfdir}/init.d/init-cgroup-dhcp
}
