# Provides subuid/subgid for rootless container support.

pkg_postinst:${PN}() {
	rootfs="$D"
	if [ -z "$rootfs" ]; then
		rootfs="/"
	fi

	for map in subuid subgid; do
		map_file="$rootfs${sysconfdir}/$map"
		touch "$map_file"
		grep -q '^containers:100000:65536$' "$map_file" || echo 'containers:100000:65536' >> "$map_file"
	done
}
