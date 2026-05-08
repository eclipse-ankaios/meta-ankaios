# CVE-2026-31431: crypto: algif_aead - Revert to operating out-of-place
# Bump vulnerable RPi kernels to patched versions.
# Using anonymous python so a single %.bbappend works across versions.
python () {
    lv = d.getVar('LINUX_VERSION') or ''

    # 6.12.x: fixed in v6.12.85 (stable kernel commit 8b88d99341f1)
    if lv.startswith('6.12.'):
        parts = lv.split('.')
        if len(parts) >= 3 and int(parts[2]) < 85:
            d.setVar('LINUX_VERSION', '6.12.85')
            d.setVar('SRCREV_machine', 'effcbc88e3ab970a2d2aafdfe7c9333766f7139a')
}
