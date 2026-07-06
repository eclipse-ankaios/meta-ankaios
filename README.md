# Yocto Metadata layer for Eclipse Ankaios

This repo contains a [Yocto metadata layer](recipes-ankaios/ankaios) for [Eclipse Ankaios](https://github.com/eclipse-ankaios/ankaios).

The bin recipe installs from the binaries released on GitHub and supports all Yocto releases starting from 4.0 "Kirkstone". It was explicitly tested with 6.0 "Wrynose", 5.3 "Whinlatter", and 4.0 "Kirkstone".

The git recipe builds from source and requires at least 5.3 "Whinlatter" because of the required Rust version (>= 1.90).

Additionally the repo contains [`kas`](https://github.com/siemens/kas) and `bitbake-setup` example configurations to build images that include Ankaios.

## Preconditions

To build a Yocto image you need

* At least 90 GB of free disk space
* At least 20-32 GB of RAM

## Build

All builds in this repository are configured to use a shared cache location for all build variants:

* downloads: `/workspace/bitbake-shared-cache/downloads`
* sstate: `/workspace/bitbake-shared-cache/sstate-cache`

This allows `kas` and `bitbake-setup` builds to reuse downloaded sources and sstate artifacts across different build directories.

If you add additional builds, please ensure to use the cache in order to avoid storage-full problems.

### Building with kas

The dev container already has all required tools installed so the build can be easily started in it.

The repo contains six kas build configs, split by Yocto release:

* [kas-full-cmd-systemd-whinlatter.yml](kas-full-cmd-systemd-whinlatter.yml) - building a core-image-full-cmdline with systemd on Yocto 5.3 "Whinlatter"
* [kas-minimal-sysvinit-whinlatter.yml](kas-minimal-sysvinit-whinlatter.yml) - building a core-image-minimal with sysvinit on Yocto 5.3 "Whinlatter"
* [kas-full-cmd-systemd-wrynose.yml](kas-full-cmd-systemd-wrynose.yml) - building a core-image-full-cmdline with systemd on Yocto 6.0 "Wrynose"
* [kas-minimal-sysvinit-wrynose.yml](kas-minimal-sysvinit-wrynose.yml) - building a core-image-minimal with sysvinit on Yocto 6.0 "Wrynose"
* [kas-full-cmd-systemd-kirkstone.yml](kas-full-cmd-systemd-kirkstone.yml) - building a core-image-full-cmdline with systemd on Yocto 4.0 "Kirkstone"
* [kas-minimal-sysvinit-kirkstone.yml](kas-minimal-sysvinit-kirkstone.yml) - building a core-image-minimal with sysvinit on Yocto 4.0 "Kirkstone"

All images run Ankaios and start containers with Podman.

For details on building and running them see the next six sections.

The following examples shows how to build a minimal Yocto 6.0 "Wrynose" image with sysvinit as init manager:

```shell
kas build kas-minimal-sysvinit-wrynose.yml
```

Afterwards you can start a kas shell and run the image with qemu and login with user `root` (no password):

```shell
# First start the shell
kas shell kas-minimal-sysvinit-wrynose.yml
# And in the shell run qemu
runqemu snapshot nographic slirp qemuparams="-m 1024"
```

To build an image with the other 5 options, just use the desired config file.

### Building with bitbake-setup

This section provides information on building an image with bitbake setup.


> [!TIP]
> There are also just commands that handle the most common bitbake-setup configs. Run `just --list` to see them.

As an alternative to `kas`, this repository also includes two release-specific `bitbake-setup` profiles:

* [bitbake-setup-ankaios-whinlatter.conf.json](bitbake-setup-ankaios-whinlatter.conf.json)
* [bitbake-setup-ankaios-wrynose.conf.json](bitbake-setup-ankaios-wrynose.conf.json)

Kirkstone is not supported by `bitbake-setup` because that tool was introduced after Yocto 4.0. The `bitbake-setup` executable is provided by the bitbake repo which is not included in the current devcontainer. To get the repo in order to prepare the environment, just run once the following script:

`./scripts/bitbake-setup-repo.sh`

Afterwards (or if bitbake-setup was already available) continue with the setup init:

`bitbake-setup init --non-interactive ./bitbake-setup-ankaios-whinlatter.conf.json <runtime-oriented config> <machine target>`

For Wrynose, use `./bitbake-setup-ankaios-wrynose.conf.json` instead.

> [!NOTE]
> The `just` targets use Wrynose by default and can be switched to Whinlatter via:
>
> `just RELEASE=whinlatter <target>`

The supported runtime-oriented configurations are:

* `ankaios-sysvinit`
* `ankaios-systemd`

and the supported machine targets:

* `machine/qemux86-64`
* `machine/raspberrypi4-64`
* `machine/raspberrypi5`

For Raspberry Pi machine targets (`raspberrypi4-64` and `raspberrypi5`), the image includes
`rootfs-resize`, which expands the root partition and filesystem on first boot
to use the full SD card capacity.

After the setup is complete, source the environment with:

`. bitbake-builds/<runtime-oriented config>-<release>-<machine target>/build/init-build-env`

Note that after sourcing the config, the environment is set for exactly this runtime-oriented config, release, and machine target. Use another shell or source another environment to change this.

To build the image, trigger the `bitbake` command with the preferred target:

`bitbake <image-target>`

where the image target can be one of the following (other targets would probably work too, but these are tested):

* `core-image-minimal`
* `core-image-full-cmdline`

After the build is complete, run QEMU from the same shell with:

```shell
runqemu snapshot nographic slirp
```

The following two examples show how to build a minimal image with SysVinit and a full-cmdline image with Systemd, but you can also mix and match with different configs. For example, you can try to build a full-cmdline image with SysVinit for RaspberryPi4.

#### Bitbake-setup for a minimal image with SysVinit

Run `./scripts/bitbake-setup-init.sh` if bitbake is not available yet.

For a minimal image with a SysV init configuration for `qemux86-64`:

```shell
bitbake-setup init --non-interactive ./bitbake-setup-ankaios-whinlatter.conf.json ankaios-sysvinit machine/qemux86-64
. bitbake-builds/ankaios-sysvinit-whinlatter-qemux86-64/build/init-build-env
bitbake core-image-minimal
```

#### Bitbake-setup for a full-cmdline image with systemd

Systemd configuration example:

```shell
bitbake-setup init --non-interactive ./bitbake-setup-ankaios-whinlatter.conf.json ankaios-systemd machine/qemux86-64
. bitbake-builds/ankaios-systemd-whinlatter-qemux86-64/build/init-build-env
bitbake core-image-full-cmdline
```

## Linting recipes

The dev container includes `just` and `oelint-adv`.

To lint all `.bb` recipe files below `recipes-ankaios`, run:

```shell
just lint
```

The command walks all subdirectories of `recipes-ankaios`, prints each parsed file, and then prints only lint warnings and errors for that file.

## FAQ

**Question**: The build suddenly exits with "[process exited with code 1 (0x00000001)]".

**Answer**: Problems might be out of memory or disk space. Check the remaining disk space with `df -h`. Check the memory with `free -h`. On WSL2 you can increase the memory by editing `%USERPROFILE%\.wslconfig` and adding:

```toml
[wsl2]
memory=25GB
```

**Question**: I get fetch errors during the bitbake build.

**Answer**: Unfortunately this sometimes happens during the download of the required packages. If it happens, just rerun the build again until it succeeds.

**Question**: I use WSL and ran out of space during the builds

**Answer**: Even if you delete some files on the Linux system, the space will not automaticlly be reclaimed as free. To do this follow these steps:

* First trim the disk on the Linux guest:

```sudo fstrim /.```

* Then shutdown WSL from a windows shell:

```wsl.exe --shutdown```

* Run diskpart from a windows shell:

```diskpart```

* In DISKPART select the virtual disk by customizing the following to your machine:

```select vdisk file="C:\Users\<user name>\AppData\Local\Packages\<guest name>\LocalState\ext4.vhdx"```

* And trigger the compacting:

```compact vdisk```
