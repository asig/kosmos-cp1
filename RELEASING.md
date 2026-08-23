# Releasing

Releases are created by GitHub Actions when a version tag beginning with `v`
is pushed. The release workflow builds the application for Linux, Windows,
and macOS, then attaches the generated packages to a GitHub Release.

## Prepare the release

1. Make sure the changes to be released are merged into the branch from
which the release tag will be created (typically `master`, this is a
single-dev hobby project :-) ).
2. Build and test locally when practical:

```bash
   cmake -S . -B build/Release -DCMAKE_BUILD_TYPE=Release
   cmake --build build/Release --parallel
   ctest --test-dir build/Release --output-on-failure
```

3. Choose the new release number and set it in `CMakeLists.txt` first. This
 is the source of truth for the release version. Use the numeric version
 without the leading `v`, for example:

```cmake
   project(
      kosmos-cp1
      VERSION 2.1.0
   )
```

   This version is used for application metadata and the generated Windows
   version file.

4. Update `CHANGELOG.md` using the same version number from `CMakeLists.txt`.
It may already contain a `## Next` section for unreleased changes. Rename
that heading to the version with a leading `v`, keeping the existing entries
as the release notes. For example, if `CMakeLists.txt` contains
`VERSION 2.1.0`, rename `## Next` to `## v2.1.0`:

```markdown
   ## v2.1.0

   ### Added

   - Describe the new feature.
```

5. Commit and push the version and changelog updates:

```bash
   git commit -am "Prepare release v2.1.0"
   git push
```

## Create a release

From the repository root, create an annotated tag using the same version from
`CMakeLists.txt`, add the leading `v`, and push it to GitHub:

```bash
git tag -a v2.1.0 -m "Release v2.1.0"
git push origin v2.1.0
```

The version number originates in `CMakeLists.txt` and must then be reflected
consistently in the changelog and Git tag:

- `CMakeLists.txt`: set `VERSION 2.1.0` without the leading `v`.
- `CHANGELOG.md`: rename `## Next` to `## v2.1.0`.
- Git: create and push the tag `v2.1.0`.

The tag must match the `v*` pattern. The release workflow derives its version
from the tag, then passes it to the platform build workflows. Pushing the tag
starts `.github/workflows/release.yml`.

## Prereleases

Use a suffix containing `-alpha`, `-beta`, or `-rc` for a GitHub prerelease:

```bash
git tag -a v2.1.0-rc1 -m "Release v2.1.0-rc1"
git push origin v2.1.0-rc1
```

The workflow marks these releases as prereleases automatically.

## Monitor the build

Open **GitHub -> Actions -> Release** and wait for all jobs to complete:

- Linux AppImage
- Windows installer
- macOS ARM64 DMG
- macOS x86_64 DMG

The workflow extracts the matching section from `CHANGELOG.md` and creates
the GitHub Release with the generated packages attached.

## Troubleshooting

- If the changelog is empty, check that its heading exactly matches
  `## v<version>`.
- If a release does not start, check that the tag was pushed and begins
  with `v`.
- If a build fails, fix the problem and push a new tag. Do not reuse an
  existing tag unless the old release and tag have been deliberately removed
  or moved.
