# meta-ankaios

A Yocto/OpenEmbedded metadata layer for [Eclipse Ankaios](https://github.com/eclipse-ankaios/ankaios).

Ankaios is a workload and container orchestrator for embedded and automotive use-cases.
This repository provides the `meta-ankaios` Yocto layer and contains only the
recipes for the Ankaios server, agent and ank CLI.

Everything needed to build a complete, runnable example image (image/distro/machine
configuration, `kas` and `bitbake-setup` files, and supporting helper recipes) lives
in the separate demo repository, [ankaios-yocto-demo](https://github.com/eclipse-ankaios/ankaios-yocto-demo).

## What this layer provides

* `recipes-ankaios/ankaios` – Ankaios server, agent and ank CLI
  * `ankaios-bin` – installs the pre-built binaries released on GitHub. Supports all
    Yocto releases starting from 4.0 "Kirkstone".
  * `ankaios_git` – builds from source. Requires at least 5.3 "Whinlatter" because of
    the required Rust version (>= 1.90).

> [!NOTE]
> The `ankaios_git` (build-from-source) recipe is in the process of being moved to
> `meta-virtualization`. It is kept here until that move is finalized.

## Compatibility

This layer follows the usual Yocto model of one branch per release. Pick the branch
that matches the Yocto/OpenEmbedded release you are building against:

| Branch        | Yocto release(s) (`LAYERSERIES_COMPAT`) |
| ------------- | --------------------------------------- |
| `main`        | `whinlatter wrynose`                    |
| `wrynose`     | `wrynose`                               |
| `whinlatter`  | `whinlatter`                            |
| `scarthgap`   | `scarthgap`                             |
| `kirkstone`   | `kirkstone`                             |

The `scarthgap` and `kirkstone` branches only provide the `ankaios-bin` recipe, since
`ankaios_git` (build-from-source) requires a newer Rust toolchain than those releases
ship.

## Dependencies

This layer depends on:

* `openembedded-core` (`meta`) – declared as `LAYERDEPENDS` in
  [conf/layer.conf](conf/layer.conf). Provides the `cargo`, `systemd` and
  `update-rc.d` classes the recipes inherit.
* `meta-openembedded` (`meta-oe`) – only for `ankaios_git`, which build-depends on
  `protobuf-native`. The `ankaios-bin` recipe needs only `openembedded-core`.

## Building an image

To build a ready-to-run Poky image that includes the Ankaios packages from this
layer, use the demo repository:

**[ankaios-yocto-demo](https://github.com/eclipse-ankaios/ankaios-yocto-demo)**

It contains the `kas` and `bitbake-setup` configurations, machine/distro settings and
build instructions for QEMU and Raspberry Pi targets, and pulls in `meta-ankaios` as a
layer.

## Using this layer in your own build

Clone the repository and add the layer to your build:

```shell
git clone https://github.com/eclipse-ankaios/meta-ankaios.git
bitbake-layers add-layer meta-ankaios
```

Alternatively, reference the repository directly from your `kas` or `bitbake-setup`
configuration (see [ankaios-yocto-demo](https://github.com/eclipse-ankaios/ankaios-yocto-demo)
for a working example).

Then install the components you need, e.g. in your image or `local.conf`.

To install the full set (server, agent and the `ank` CLI), use the meta-package:

```bitbake
IMAGE_INSTALL:append = " ankaios-bin"
```

The recipe is split into separate packages, so you can also install only the parts
you need:

* `ank-server-bin` – the Ankaios server (`ank-server`) with its config and service.
* `ank-agent-bin` – the Ankaios agent (`ank-agent`) with its config and service.
* `ank-bin` – the `ank` command-line interface with its configs.

For example, to build an image that only runs a server and an agent, run:

```bitbake
IMAGE_INSTALL:append = " ank-server-bin ank-agent-bin"
```

## Linting recipes

The recipes are linted with [`oelint-adv`](https://github.com/priv-kweihmann/oelint-adv)
in CI/CD on every push or pull request (see [.github/workflows/lint.yml](.github/workflows/lint.yml)).

## License

See [LICENSE](LICENSE).
