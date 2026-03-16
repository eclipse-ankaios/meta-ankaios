# Yocto Metadata layer for Eclipse Ankaios

This repo contains a Yocto metadata layer for Eclipse Ankaios.

It is based on Yocto 5.3 "Whinlatter" and Ankaios v1.

## Preconditions

To build a Yocto image you need

* At least 90 GB of free disk space
* At least 20-32 GB of RAM

## Build

All builds in this repository are configured to use a shared cache location for all build variants:

* downloads: `/workspace/bitbake-shared-cache/downloads`
* sstate: `/workspace/bitbake-shared-cache/sstate-cache`

This allows `kas` and `bitbake-setup` builds to reuse downloaded sources and sstate artifacts across different build directories.

If you add additional builds, please ensure to use the cache in order to avoid storage full problems.

### Building with kas

The dev container already has all required tools installed so the build can be easily started in it.

The repo contains two kas build configs:

* [kas-full-cmd-systemd.yml](kas-full-cmd-systemd.yml) - building a core-image-full-cmdline with systemd as init manager and automatic ankaios start via systemd units
* [kas-minimal-sysvinit.yml](kas-minimal-sysvinit.yml) - building a core-image-minimal with sysvinit

Both images can run Ankaios and start containers with Podman.

For details on building and running them see the next two sections.

#### Building a full-cmdline image with systemd

This configuration builds a full cmdline image with systemd as init manager. To start the build just run:

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

#### Building a minimal image with sysvinit as init manager

To build the minimal image with the SysVinit init manager run:

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

This image does not provide tmux because of missing locales and tools in the minimal image.
You can start the Ankaios server and agent in the background and pipe the logs to appropriate files.

### Building with bitbake-setup

As an alternative to `kas`, this repository also includes a `bitbake-setup` profile - [bitbake-setup-ankaios.conf.json](bitbake-setup-ankaios.conf.json). The `bitbake-setup` executable is provided by the bitbake repo which is not included in the current devcontainer. If you first run `kas`, `bitbake` will already be available in the expected location, but if you skip this and directly want to use `bitbake-setup`, you will have to prepare the environment first (currently just check out the bitbake repo). To do so, just run once the following script:

`./scripts/bitbake-setup-init.sh`

Afterwards (or if bitbake was already available) continue with the setup init:

`bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json <runtime-oriented config> <machine target>`

The supported runtime-oriented configurations are:

* `ankaios-sysvinit`
* `ankaios-systemd`

and the supported machine targets:

* `machine/qemux86-64`
* `machine/raspberrypi4-64`
* `machine/raspberrypi5`

For Raspberry Pi machine targets (`raspberrypi4-64` and `raspberrypi5`), the image includes
`ankaios-rpi-rootfs-resize`, which expands the root partition and filesystem on first boot
to use the full SD card capacity.

After the setup is complete, source the environment with:

`. bitbake-builds/<runtime-oriented config>-<machine target>/build/init-build-env`

Note that after sourcing the config, the environment is set for exactly this runtime-oriented config and machine target. Use another shell or source another environment to change this.

Select an  image target.
Following targets are tested, but others would probably work too:

* `core-image-minimal`
* `core-image-full-cmdline`

And trigger the `bitbake` build with the desired image target:

`bitbake <image-target>`
After the build is complete, run QEMU from the same shell with:

```shell
runqemu snapshot nographic slirp
```

The following two examples show how to build the images that correspond to the `kas` builds, but you can also mix and match with different configs. For example, you can try to build a full-cmdline image with SysVinit for RaspberryPi4.

#### Bitbake-setup for a minimal image with SysVinit

Run `./scripts/bitbake-setup-init.sh` if bitbake is not available yet.

For a minimal image with a SysV init configuration for `qemux86-64`:

```shell
bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-sysvinit machine/qemux86-64
. bitbake-builds/ankaios-sysvinit-qemux86-64/build/init-build-env
bitbake core-image-minimal
```

#### Bitbake-setup for a full-cmdline image with systemd

Systemd configuration example:

```shell
bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-systemd machine/qemux86-64
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

**Question**: I get fetch errors during the bitbake build.

**Answer**: Unfortunately this sometimes happens during the download of the required packages. If it happens, just rerun the build again until it succeeds.
