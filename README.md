# Yocto Metadata layer for Eclipse Ankaios

This repo contains a Yocto Metadata layer for Eclipse Ankaios.

It based on Yocto 5.2 as Ankaios v0.6.0 uses Rust edition 2021 and `Cargo.lock` version 4 and thus at least Rust toolchains 1.78 is required.


## Preconditions

To build a Yocto image you need a compatible Linux distribution like Ubuntu with

* At least 90 GB of free disk space
* At least 20-32 GB of RAM

To install the essential host packages:

``` shell
sudo apt-get install build-essential chrpath cpio debianutils diffstat file gawk gcc git iputils-ping libacl1 liblz4-tool locales python3 python3-git python3-jinja2 python3-pexpect python3-pip python3-subunit socat texinfo unzip wget xz-utils zstd
```

The easiest way to build the image is using the [kas](https://kas.readthedocs.io/en/latest/) tool. Install it with:

```shell
sudo pip3 install .
```

## Build

First create a project folder:

```shell
mkdir myproj
cd myproj
```

Then clone this repo:

```shell
git clone https://...
```

Then we can start the build. It takes a couple fo hours depending on the host machine.

```shell
kas build meta-ankaios/kas-poky-ankaios.yml
```

## FAQ

Question: The build suddenly exits with "[process exited with code 1 (0x00000001)]".
Answer: Problems might be out of memory or disk space. Check the remaining disk space with `df -h`. Check the memory with `free -h`. On WSL2 you can increase the memory by editing `%USERPROFILE%\.wslconfig` and adding:

```
[wsl2]
memory=25GB
```

