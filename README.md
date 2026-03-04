# Yocto Metadata layer for Eclipse Ankaios

This repo contains a Yocto Metadata layer for Eclipse Ankaios.

It based on Yocto 5.3 "Whinlatter" and Ankaios v1.

## Preconditions

To build a Yocto image you need

* At least 90 GB of free disk space
* At least 20-32 GB of RAM

## Build

The dev container already has all required tools installed so the build can be easily stared in it.

The repo contains two kas build configs:

* [kas-full-cmd-systemd.yaml] - building a core-image-full-cmdline with systemd as init manager and automatic ankaios start via systemd units
* [kas-minimal-sysvinit.yaml] - building a core-image-minimal with sysvinit

Both images can run Ankaios and start containers with Podman.

For details on building and running them see the next two sections.

### Building a full-cmdline image with systemd

Currently this build does not used prebuild cached artefacts and the build
takes a couple of hours depending on the host machine.

```shell
kas build kas-full-cmd-systemd.yml
```

Afterwards you can start a kas shell and run the image with qemu and login with user `root` (no password):

```shell
# First start the shell
kas shell kas-full-cmd-systemd.yml
# And in the shell run qemu
runqemu snapshot nographic slirp
```

### Building a minimal image with sysvinit as init manager

This config builds a smaller image and uses prebuild artefacts so the build is significantly faster.

For more info on the cached artefacts see the following options in the kas file: `BB_HASHSERVE_UPSTREAM`, `SSTATE_MIRRORS`, `BB_HASHSERVE` and `BB_SIGNATURE_HANDLER`.

```shell
kas build kas-minimal-sysvinit.yml
```

Afterwards you can start a kas shell and run the image with qemu and login with user `root` (no password):

```shell
# First start the shell
kas shell kas-minimal-sysvinit.yml
# And in the shell run qemu
runqemu snapshot nographic slirp
```

This image does not provide tmux because of missing locales and tools in minimal image.
You can start ank server and agent in the background and pipe the logs to appropriate files.

### Building with bitbake-setup

As an alternative to `kas`, this repository now includes a `bitbake-setup` profile. You can initiate the setup with:

`layers/bitbake/bin/bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json <runtime-oriented config> <machine target>`

The suported runtime-oriented configurations are:

* `ankaios-sysvinit`
* `ankaios-systemd`

and the supported machine targets:

* `qemux86-64`
* `raspberrypi4-64`
* `raspberrypi5`

For Raspberry Pi machine targets (`raspberrypi4-64` and `raspberrypi5`), the image includes
`ankaios-rpi-rootfs-resize`, which expands the root partition and filesystem on first boot
to use the full SD card capacity.

After the setup is complete, source the environment with:

`. bitbake-builds/<runtime-oriented config>-<machine targets>/build/init-build-env`

And trigger the `bitbake` build with the desitred image target:

`bitbake <image-target>`

The following image targets are tested, but others would probabaly work too:

* `core-image-minimal`
* `core-image-full-cmdline`

After the build is complete, run QEMU from the same shell with:

```shell
runqemu snapshot nographic slirp
```

#### Example builds

For a minimal image with a SysV init configuration for `qemux86-64`:

```shell
layers/bitbake/bin/bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-sysvinit machine/qemux86-64
. bitbake-builds/ankaios-sysvinit-qemux86-64/build/init-build-env
bitbake core-image-minimal
```

Systemd configuration example:

```shell
layers/bitbake/bin/bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-systemd machine/qemux86-64
. bitbake-builds/ankaios-systemd-qemux86-64/build/init-build-env
bitbake core-image-full-cmdline
```

## FAQ

**Question**: The build suddenly exits with "[process exited with code 1 (0x00000001)]".

**Answer**: Problems might be out of memory or disk space. Check the remaining disk space with `df -h`. Check the memory with `free -h`. On WSL2 you can increase the memory by editing `%USERPROFILE%\.wslconfig` and adding:

```toml
[wsl2]
memory=25GB
```
