# TerraBlender — Zublastic Passive

Compatibility-only fork for Minecraft 1.21.1 / NeoForge. The mod ID remains
`terrablender` so dependent mods can register their data and call the retained APIs.
**TerraBlender does not automatically place biomes or apply surface rules in any dimension.**
There is no enable switch, region allowlist, or alternate End initialization path.

This intentionally changes upstream behavior. A separate, validated worldgen
package must provide the biome placement and surfaces that a pack needs. Installing
this fork alone can remove modded biome placement and produce fallback surfaces.
It does not repair already generated chunks, establish a cause for surface defects,
or prove compatibility with Immersive Portals/Sable/Distant Horizons.

## What remains and what is disabled

| Retained compatibility | Disabled generation behavior |
| --- | --- |
| Mod ID, public registration APIs and region/surface authoring records | Region selection, regional noise and regional search trees |
| Inert extension interfaces used by other mods | Automatic biome-source rewrites and deferred biome-list changes |
| Ordinary delegated biome lookup | TerraBlender interception of each biome query |
| Readable legacy `terrablender:merged` codec, using its base rule only | Namespace-based surface dispatch, including optimizer inspection |
| Public config/data types and upstream data-building utilities | Server dimension initialization, including the End |
| Upstream license and API provenance | Validation cancellation and experimental-world warning suppression |

Data registration can still cost work during mod loading. This is not a claim of
literally zero allocation or CPU cost, and it does not disable independent worldgen
hooks belonging to other mods.

## Build and verify

Use JDK 21. Only the NeoForge target is supported by this fork.

```sh
./gradlew :NeoForge:test :NeoForge:jar :NeoForge:sourcesJar --max-workers=2
```

The build pins NeoForge 21.1.248 and ModDevGradle 2.0.143. Output is under
`NeoForge/build/libs/`. Tests use NeoForge's transformed JUnit environment; a full
server, generated-world comparison and portal acceptance are separate checks.

For a bounded integration test, supply an isolated directory containing the exact
comparison mods and an explicit list of IDs that must actually load:

```sh
./gradlew :NeoForge:test -PcompatModsDir=/path/to/isolated/mods -PexpectedCompatModIds=biolith,modernfix
```

The fixture jars are not bundled in the product. Do not point testing tools at an
active instance or deploy this artifact automatically. See
[behavior and validation](docs/PASSIVE_CONTRACT.md).

## Origin and licensing

Based on [Glitchfiend/TerraBlender, 1.21.1](https://github.com/Glitchfiend/TerraBlender/tree/c90344362b5e813c43f38fed19ea82412c1fffc3),
commit `c90344362b5e813c43f38fed19ea82412c1fffc3` (4.1.0.8 lineage), by
Glitchfiend/Adubbz and upstream contributors. Zublastic's passive behavior and
NeoForge-only build changes are maintained in this fork. Unbuilt upstream
Fabric/Forge/example files remain as history/reference, not supported artifacts.

Licensed under [LGPLv3](LICENSE); keep notices and provide the corresponding
source with redistributed binaries. This fork is not an upstream TerraBlender
release. The inherited upstream publishing job is disabled in this repository;
verification does not publish to mod distribution services.
