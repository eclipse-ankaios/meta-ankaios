# Yocto Metadata layer for Eclipse Ankaios

This repo contains a Yocto Metadata layer for Eclipse Ankaios.

It based on Yocto 5.2 as Ankaios v0.6.0 uses Rust edition 2021 and `Cargo.lock` version 4 and thus at least Rust toolchains 1.78 is required.

## Preconditions

To build a Yocto image you need

* At least 90 GB of free disk space
* At least 20-32 GB of RAM

## Build

The dev container already has all required tools installed so the build can be easily stared in it.

The repo contains two kas build configs: 
* [kas-poky-ankaios.yaml] - building a core-image-full-cmdline with systemd as init manager and automatic ankaios start via systemd units
* [kas-poky-ankaios-minimal-sysvinit.yaml] - building a core-image-minimal with sysvinit

Both images can run Ankaios and start containers with Podman.

For details on building and running them see the next two sections.

### Building a full-cmdline image with systemd

Currently this build does not used prebuild cached artefacts and the build
takes a couple of hours depending on the host machine.

```shell
kas build kas-poky-ankaios.yml
```

Afterwards you can start a kas shell and run the image with qemu and login with user `root` (no password):

```shell
# First start the shell
kas shell kas-poky-ankaios.yml
# And in the shell run qemu
runqemu nographic slirp
```

### Building a minimal image with sysvinit as init manager

This config builds a smaller image and uses prebuild artefacts so the build is significantly faster.

For more info on the cached artefacts see the following options in the kas file: `BB_HASHSERVE_UPSTREAM`, `SSTATE_MIRRORS`, `BB_HASHSERVE` and `BB_SIGNATURE_HANDLER`.

```shell
kas build kas-poky-ankaios-minimal-sysvinit.yml
```

Afterwards you can start a kas shell and run the image with qemu and login with user `root` (no password):

```shell
# First start the shell
kas shell kas-poky-ankaios-minimal-sysvinit.yml
# And in the shell run qemu
runqemu snapshot nographic slirp
```

## FAQ

**Question**: The build suddenly exits with "[process exited with code 1 (0x00000001)]".

**Answer**: Problems might be out of memory or disk space. Check the remaining disk space with `df -h`. Check the memory with `free -h`. On WSL2 you can increase the memory by editing `%USERPROFILE%\.wslconfig` and adding:

```
[wsl2]
memory=25GB
```
