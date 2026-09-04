# UnitedFactions maintained-fork policy

This public repository preserves the GitHub fork relationship with
[GroupeZ-dev/zKoth](https://github.com/GroupeZ-dev/zKoth) so generic fixes can
be contributed upstream.

## Canonical release line

- main is the protected UnitedFactions production source branch.
- The initial Paper 26.2 line is based on upstream develop commit
  eafcfdf6eca8593110817531cf561258880c3e02.
- UnitedFactions builds use an explicit -uf.N version suffix until an official
  upstream release supersedes them.
- Each release must be built from main by Java 25 with
  ./gradlew clean build --no-daemon.
- The deployable artifact is the shaded JAR in target/; JARs in build/libs/
  are not release artifacts.
- Release notes record the source commit, artifact SHA-256, build environment,
  test results, and configuration impact.

## Change policy

- Use one focused branch and pull request per logical change.
- Preserve upstream commands, permissions, placeholders, hooks, configuration
  keys, event types, capture modes, scoreboards, holograms, and reward paths.
- Do not add UnitedFactions gameplay or production configuration to this
  repository.
- Prepare generic upstream contributions from a clean branch based on the
  current upstream target branch.

## Public-repository boundaries

Never commit production configuration, arena coordinates, rewards, logs,
databases, player data, secrets, licensed dependencies, or signing material.
Those assets are managed separately from this public source repository.
