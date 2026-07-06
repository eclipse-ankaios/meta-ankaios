# Ensure rootless container subid mappings are present in the final image.

ANKAIOS_CONTAINER_SUBID_ENTRY ?= "containers:100000:65536"

ankaios_write_container_subid_maps () {
    for map in subuid subgid; do
        map_file="${IMAGE_ROOTFS}${sysconfdir}/$map"
        install -d "${IMAGE_ROOTFS}${sysconfdir}"
        touch "$map_file"
        grep -qxF "${ANKAIOS_CONTAINER_SUBID_ENTRY}" "$map_file" || echo "${ANKAIOS_CONTAINER_SUBID_ENTRY}" >> "$map_file"
    done
}

ROOTFS_POSTPROCESS_COMMAND:append = " ankaios_write_container_subid_maps;"
