# Repository structure & branch layout

- **Multi-branch layout.** The release branches (`kirkstone`, `scarthgap`,
  `whinlatter`, `wrynose`) are *layer-only*: they carry just the `ankaios-bin`
  prebuilt-binary recipe under `recipes-ankaios/`. The `main`
  branch additionally carries `ankaios_git` (the build-from-source recipe and its
  vendored `ankaios-crates.inc`), which is being migrated to
  `meta-virtualization`.
- **Per-branch files.** Each branch has its own `README.md` and
  `.github/workflows/lint.yml`. The lint workflow's `push.branches` filter is
  scoped to that branch only (e.g. on the `scarthgap` branch it is
  `push: branches: [scarthgap]`). GitHub Actions runs the *pushed* branch's
  workflow on push, and the *PR base* branch's workflow on pull requests — so
  every branch needs its own workflow copy.
- **Lint CI.** Linting runs in the devcontainer image
  `ghcr.io/eclipse-ankaios/ankaios-yocto-devcontainer` and checks only the
  recipes under `recipes-ankaios/` with `oelint-adv`.

## Working across branches (gotcha)

- After committing changes on one branch, a `git checkout <next-branch>` can
  occasionally leave the just-committed `README.md` / `.github` files as
  **untracked** files still holding the previous branch's content. After each
  checkout, run `git clean -fd` and verify the file content — e.g. 
  `head -1 README.md` — before committing.
