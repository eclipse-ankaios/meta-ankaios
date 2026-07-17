# Recipe conventions

## `UNPACKDIR` vs `WORKDIR` across Yocto releases

Where `file://` sources land after unpacking changed over releases. Verified
against OpenEmbedded-Core `meta/conf/bitbake.conf` on each branch:

| Release            | `UNPACKDIR`                          | `S`                    | Use in recipe |
| ------------------ | ------------------------------------ | ---------------------- | ------------- |
| kirkstone (4.0)    | *not defined*                        | `${WORKDIR}/${BP}`     | `${WORKDIR}`  |
| scarthgap (5.0)    | *not defined*                        | `${WORKDIR}/${BP}`     | `${WORKDIR}`  |
| styhead (5.1)      | `??= "${WORKDIR}/sources-unpack"`    | `${WORKDIR}/${BP}`     | `${UNPACKDIR}`|
| whinlatter (5.3)   | `??= "${WORKDIR}/sources"`           | `${UNPACKDIR}/${BP}`   | `${UNPACKDIR}`|
| wrynose (6.0)      | `??= "${WORKDIR}/sources"`           | `${UNPACKDIR}/${BP}`   | `${UNPACKDIR}`|

Key points:

- On **kirkstone / scarthgap** there is **no `UNPACKDIR`** at all — sources
  unpack directly into `WORKDIR`. Recipes on these branches must reference
  `${WORKDIR}`; using `${UNPACKDIR}` there expands to empty and breaks.
- The split begins at **styhead (5.1)**, where `UNPACKDIR` was introduced as a
  *subdirectory* of `WORKDIR` (not equal to it) — not at walnascar (5.2).
- The `ankaios-bin` recipe follows this: `${WORKDIR}` on the
  kirkstone/scarthgap branches, `${UNPACKDIR}` on whinlatter/wrynose/main.
