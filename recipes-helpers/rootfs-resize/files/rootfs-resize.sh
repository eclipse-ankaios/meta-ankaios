#!/bin/sh
set -eu

MARKER="/var/lib/rootfs-resize.done"

mkdir -p /var/lib
if [ -e "$MARKER" ]; then
    exit 0
fi

ROOT_DEV="$(awk '$2=="/" { print $1; exit }' /proc/mounts || true)"

if [ "$ROOT_DEV" = "/dev/root" ] || [ -z "$ROOT_DEV" ]; then
    for tok in $(cat /proc/cmdline); do
        case "$tok" in
            root=/dev/*)
                ROOT_DEV="${tok#root=}"
                ;;
            root=PARTUUID=*)
                partuuid="${tok#root=PARTUUID=}"
                ROOT_DEV="$(readlink -f "/dev/disk/by-partuuid/$partuuid" || true)"
                ;;
        esac
    done
fi

if [ -z "$ROOT_DEV" ] || [ ! -b "$ROOT_DEV" ]; then
    echo "rootfs-resize: unable to resolve root block device" >&2
    exit 1
fi

base="$(basename "$ROOT_DEV")"
case "$base" in
    *p[0-9]*)
        PART_NUM="${base##*p}"
        DISK_DEV="/dev/${base%p${PART_NUM}}"
        ;;
    *[0-9])
        PART_NUM="${base##*[!0-9]}"
        DISK_DEV="/dev/${base%${PART_NUM}}"
        ;;
    *)
        echo "rootfs-resize: unsupported root device format: $ROOT_DEV" >&2
        exit 1
        ;;
esac

if [ ! -b "$DISK_DEV" ] || [ -z "$PART_NUM" ]; then
    echo "rootfs-resize: invalid disk/partition derived from $ROOT_DEV" >&2
    exit 1
fi

parted -s "$DISK_DEV" "resizepart $PART_NUM 100%"
partprobe "$DISK_DEV" || true

sleep 2
resize2fs "$ROOT_DEV"

touch "$MARKER"
