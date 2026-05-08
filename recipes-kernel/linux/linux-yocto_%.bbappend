FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

SRC_URI += "${@'file://podman-nft.scc' if d.getVar('DISTRO_CODENAME') == 'kirkstone' else ''}"

# CVE-2026-31431: crypto: algif_aead - Revert to operating out-of-place
# Bump vulnerable linux-yocto kernels to patched versions.
# Using anonymous python so a single %.bbappend works across all releases.
python () {
    lv = d.getVar('LINUX_VERSION') or ''

    # 5.15.x: fixed in v5.15.204 (stable kernel commit 893d22e0135f)
    if lv.startswith('5.15.'):
        parts = lv.split('.')
        if len(parts) >= 3 and int(parts[2]) < 204:
            d.setVar('LINUX_VERSION', '5.15.204')
            srcrev = '4974ca65fd6212d5a2a8022addd5bf495b2bb1dc'
            d.setVar('SRCREV_machine', srcrev)
            for m in ('qemux86-64', 'qemux86', 'qemuriscv64', 'qemuriscv32'):
                d.setVar('SRCREV_machine:' + m, srcrev)
            d.setVar('SRCREV_meta', '5e3ac50d9735b128bb7163cc951c8f62a3030d85')

    # 6.12.x: fixed in v6.12.85 (stable kernel commit 8b88d99341f1)
    elif lv.startswith('6.12.'):
        parts = lv.split('.')
        if len(parts) >= 3 and int(parts[2]) < 85:
            d.setVar('LINUX_VERSION', '6.12.85')
            srcrev = '17adabf0db8939b5abca59207efb33362624b033'
            d.setVar('SRCREV_machine', srcrev)
            for m in ('qemux86-64', 'qemux86', 'qemuarm64', 'qemuloongarch64', 'qemuppc', 'qemuriscv64', 'qemuriscv32'):
                d.setVar('SRCREV_machine:' + m, srcrev)
            d.setVar('SRCREV_meta', '89cdc6b11d8516512a1e7b584bbe19900a55059b')
}
