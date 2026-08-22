# AGENTS.md

Minecraft Forge mod: PacketProfiler (`dev.xkmc.packetprofiler`, modid `packetprofiler`). Single Gradle module; Forge 1.20.1 (47.1.3), Java 17 toolchain, ForgeGradle 5.1 + MixinGradle with official mappings.

## Commands

- Build: `./gradlew build` (no test suite, no lint/checkstyle — compiling is the verification step)
- Run dev client/server: `./gradlew runClient` / `./gradlew runServer` (working directory is `run/`, gitignored)
- Gradle daemon is disabled (`org.gradle.daemon=false`); every invocation pays JVM startup. Heap is set to `-Xmx3G` for the one-time Minecraft decompile/setup; first build is slow.
- Distributable jar is produced by `reobfJar` (auto-finalized after `jar`). Publishing writes to local `mcmodsrepo/`.

## Gotchas

- `libs/` holds untracked local jars (gitignored `*.jar`) consumed via `flatDir` as `zip.local:convivium` / `zip.local:caupona` runtimeOnly deps. Without them, tasks resolving the runtime classpath (e.g. `runClient`) fail even though `build` compiles fine.
- Every new mixin class must be added to `src/main/resources/packetprofiler.mixins.json` or it silently won't apply.
- Mod version lives in `gradle.properties` (`ll_version` is reused as the project version).
- `PacketProfiler.testPacket()` hardcodes `false`, disabling the automatic at-startup profiling path; profiling normally starts via in-game commands `/profileserver <ticks>`, `/profileloot <ticks>`, `/profiledatapack` (registered in `init/ServerCommands`).

## Structure

- `init/`: `PacketProfiler` is the `@Mod` entrypoint; client-only setup in `PPClient`.
- `mixin/`: hooks into `Connection`, `SimpleChannel`, and Forge internals to count packets; stats accumulate in `statmap/`.
- `profiler/`: recorders (`PacketRecorder`, `SidedRecorder`, `LootDebugger`) and report generation; reports are written by `ReportGenerator` at tick-time expiry.
