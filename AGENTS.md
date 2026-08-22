# AGENTS.md

Minecraft Forge mod: PacketProfiler (`dev.xkmc.packetprofiler`, modid `packetprofiler`). Single Gradle module; Forge 1.20.1 (47.1.3), Java 17 toolchain, ForgeGradle 5.1 + MixinGradle with official mappings.

## Commands

- Build: `./gradlew build` (no test suite, no lint/checkstyle — compiling is the verification step)
- Run dev client/server: `./gradlew runClient` / `./gradlew runServer` (working directory is `run/`, gitignored)
- Gradle daemon is disabled (`org.gradle.daemon=false`); every invocation pays JVM startup. Heap is set to `-Xmx3G` for the one-time Minecraft decompile/setup; first build is slow.
- Distributable jar is produced by `reobfJar` (auto-finalized after `jar`). Publishing writes to local `mcmodsrepo/`.

## Gotchas

- Every new mixin class must be added to `src/main/resources/packetprofiler.mixins.json` or it silently won't apply.
- NeoForge 1.21.1 source jars for reference (match `neo_version=21.1.197` in `gradle.properties`):
  - NeoForge-only sources jar: `/Users/arthur/.gradle/caches/modules-2/files-2.1/net.neoforged/neoforge/21.1.197/2cf0f97aeae06308110f72191eecd54f00ff76be/neoforge-21.1.197-sources.jar`
  - Merged decompiled Minecraft+NeoForge sources zip (what ModDevGradle attaches as IDE sources; use this to check vanilla MC classes too): `/Users/arthur/.gradle/caches/neoformruntime/intermediate_results/sourcesWithNeoForge_54246dd41c7976bb41b99cf423160341952a58e7_output.zip`
  - These paths are hash-keyed Gradle cache entries and can disappear after cache cleanup or a `neo_version` bump; re-locate via `~/.gradle/caches/modules-2/files-2.1/net.neoforged/neoforge/<ver>/` and the `sourcesWithNeoForge_*` entries under `~/.gradle/caches/neoformruntime/intermediate_results/`.

## Structure
