# Yocto Metadata layer for Eclipse Ankaios

This repo contains a Yocto Metadata layer for Eclipse Ankaios.

It based on Yocto 5.2 as Ankaios v0.6.0 uses Rust edition 2021 and `Cargo.lock` version 4 and thus at least Rust toolchains 1.78 is required.

## Preconditions

To build a Yocto image you need

* At least 90 GB of free disk space
* At least 20-32 GB of RAM

## Build

The dev container keeps all required tools.
Within the container start the build.
It takes a couple of hours depending on the host machine.

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

## FAQ

**Question**: The build suddenly exits with "[process exited with code 1 (0x00000001)]".

**Answer**: Problems might be out of memory or disk space. Check the remaining disk space with `df -h`. Check the memory with `free -h`. On WSL2 you can increase the memory by editing `%USERPROFILE%\.wslconfig` and adding:

```
[wsl2]
memory=25GB
```
