# meta-ankaios (scarthgap branch)

A Yocto/OpenEmbedded metadata layer for [Eclipse Ankaios](https://github.com/eclipse-ankaios/ankaios).

Ankaios is a workload and container orchestrator for embedded and automotive use-cases.
This branch of the `meta-ankaios` layer targets **Yocto 5.0 "Scarthgap" (LTS)** and
provides the recipe to install the released Ankaios server, agent and `ank` CLI binaries.

Everything needed to build a complete, runnable image (image/distro/machine
configuration and `kas`/`bitbake-setup` files) lives in the separate demo repository,
[ankaios-yocto-demo](https://github.com/eclipse-ankaios/ankaios-yocto-demo).

> [!NOTE]
> This is the release branch for Yocto 5.0 "Scarthgap". Other Yocto releases are
> maintained on their own branches (`kirkstone`, `scarthgap`, `whinlatter`, `wrynose`)
> and the latest development happens on `main`.

## What this layer provides

* `recipes-ankaios/ankaios/ankaios-bin` – installs the pre-built Ankaios binaries
  released on GitHub (server, agent and the `ank` CLI).

Building Ankaios from source is not available on this branch, because it requires a
newer Rust toolchain (>= 1.90) than Scarthgap ships. Use `main` (or the `whinlatter` /
`wrynose` branch) for the build-from-source recipe.

## Compatibility

This branch is compatible with Yocto 5.0 "Scarthgap"
(`LAYERSERIES_COMPAT_meta-ankaios = "scarthgap"`).

## Dependencies

This layer only depends on `openembedded-core` (`meta`), declared as `LAYERDEPENDS` in
[conf/layer.conf](conf/layer.conf).

## Building an image

To build a ready-to-run Poky image that includes the Ankaios packages from this
layer, use the demo repository:

**[ankaios-yocto-demo](https://github.com/eclipse-ankaios/ankaios-yocto-demo)**

It contains the `kas` and `bitbake-setup` configurations, machine/distro settings and
build instructions for QEMU and Raspberry Pi targets, and pulls in `meta-ankaios` as a
layer.

## Using this layer in your own build

Clone this branch and add the layer to your build:

```shell
git clone -b scarthgap https://github.com/eclipse-ankaios/meta-ankaios.git
bitbake-layers add-layer meta-ankaios
```

Alternatively, reference the repository directly from your `kas` or `bitbake-setup`
configuration (see [ankaios-yocto-demo](https://github.com/eclipse-ankaios/ankaios-yocto-demo)
for a working example).

To install the full set (server, agent and the `ank` CLI), use the meta-package:

```bitbake
IMAGE_INSTALL:append = " ankaios-bin"
```

The recipe is split into separate packages, so you can also install only the parts
you need:

* `ank-server-bin` – the Ankaios server (`ank-server`) with its config and service.
* `ank-agent-bin` – the Ankaios agent (`ank-agent`) with its config and service.
* `ank-bin` – the `ank` command-line interface with its config.

For example, to build an image that only runs a server and an agent:

```bitbake
IMAGE_INSTALL:append = " ank-server-bin ank-agent-bin"
```

## Linting recipes

The recipes are linted with [`oelint-adv`](https://github.com/priv-kweihmann/oelint-adv)
in CI/CD on every push or pull request (see [.github/workflows/lint.yml](.github/workflows/lint.yml)).

## License

See [LICENSE](LICENSE).
