# Change Log

### Unreleased

* Upgrade the `au.com.stocksoftware.idea.codestyle` artifact to version `1.17`.
* Upgrade the `org.realityforge.braincheck` artifact to version `1.31.0`.
* Upgrade the `org.realityforge.guiceyloops` artifact to version `0.106`.

### [v0.11](https://github.com/realityforge/gir/tree/v0.11) (2019-11-05) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.10...v0.11)

* Upgrade the `org.realityforge.javax.annotation` artifact to version `1.0.1`.
* Upgrade the `org.realityforge.guiceyloops` artifact to version `0.102`.
* Upgrade the `au.com.stocksoftware.idea.codestyle` artifact to version `1.14`.
* Upgrade the `org.realityforge.braincheck` artifact to version `1.25.0`.
* Add `Bazel` utility to start patching dependencies in Bazel based projects.

### [v0.10](https://github.com/realityforge/gir/tree/v0.10) (2019-06-04) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.09...v0.10)

* Lock down the charset used to `US_ASCII` when `FileUtil.write(Path,String)` to get consistent
  behaviour across systems.
* Add some additional `FileUtil.write(...)` variants.

### [v0.09](https://github.com/realityforge/gir/tree/v0.09) (2019-05-02) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.08...v0.09)

* Add `FileUtil.createLocalTempDir()` helper method.
* Upgrade the `org.realityforge.braincheck` artifact to version `1.16.0`.
* Remove `{@inheritDoc}` as it only explicitly indicates that the default behaviour at the expense of significant visual clutter.

### [v0.08](https://github.com/realityforge/gir/tree/v0.08) (2019-03-25) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.07...v0.08)

* Upgrade the `org.realityforge.braincheck` artifact to version `1.13.0`.
* Add `FileUtil.write(...)` methods to ease writing files relative to current directory.
* Add `FileUtil.createTempDir()` helper method.
* Add `FileUtil.inTempDir(Action)` helper method.
* Refactor tests to run in an isolated, per-test, temp directory.

### [v0.07](https://github.com/realityforge/gir/tree/v0.07) (2019-02-04) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.06...v0.07)

* Ensure `FileUtil.deleteDirIfExists(Path)` is public.

### [v0.06](https://github.com/realityforge/gir/tree/v0.06) (2019-02-04) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.05...v0.06)

* Add `FileUtil.deleteDirIfExists(Path)` helper method.
* Remove anodoc dependency.
* Remove deployment from TravisCI infrastructure as it is no longer feasible.

### [v0.05](https://github.com/realityforge/gir/tree/v0.05) (2018-04-08) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.04...v0.05)

* Fix bug in `Git.checkout(...)` that would attempt to checkout existing branches with `-b` argument.

### [v0.04](https://github.com/realityforge/gir/tree/v0.04) (2018-04-05) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.03...v0.04)

* Add `Patch.patchAndAddFile(...)` utility.

### [v0.03](https://github.com/realityforge/gir/tree/v0.03) (2018-03-23) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.02...v0.03)

* Extract a `Patch.patchAndCommitFile(...)` from `Buildr` utilities.
* Start to import `Maven` utilities from downstream projects.

### [v0.02](https://github.com/realityforge/gir/tree/v0.02) (2018-03-06) · [Full Changelog](https://github.com/realityforge/gir/compare/v0.01...v0.02)

* Color output from `StandardMessenger` using ANSI escape sequences if a console is present.
* Add `FileUtil.copyDirectory(Path,Path)` utility method to recursively copy directories.

### [v0.01](https://github.com/realityforge/gir/tree/v0.01) (2018-03-04) · [Full Changelog](https://github.com/realityforge/gir/compare/fe376759d55ced503574d2782a6f19d30b061f77...v0.01)

‎🎉	 Initial project released ‎🎉.
