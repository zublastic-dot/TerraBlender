# Local validation — 24 September 2026

Environment: Windows, JDK 21.0.6, Minecraft 1.21.1, NeoForge 21.1.248,
ModDevGradle 2.0.143. Test worker heap: 512 MiB; Gradle: 1 GiB / two workers.
No Minecraft client or full dedicated server was started; no existing world or
instance was changed. These are transformed JUnit/mod-loading checks, not portal,
terrain, dimension lifecycle or CPS acceptance.

- Core transformed contract and packaging checks passed.
- Seven checks passed with all ten expected integration mod IDs actually loaded
  (nine root JARs plus bundled Apollib 1.2.0).
- The dedicated surface probe passed seven checks with ModernFix stability BETA,
  `mixin.perf.optimize_surface_rules=true`, and
  `mixin.perf.worldgen_allocation=false`. It asserts the ModernFix surface interface
  really exists and supplies a possible-biome set, exercising its fast path rather
  than just loading the mod.
- Unrestricted ModernFix BETA plus ByePregen failed ordinary `findValue` with a
  null climate index, before the passive extension call. It reproduced with both
  one-entry and two-entry climate lists. Inspection found ModernFix deferring index
  creation and ByePregen reading that index directly. This interaction was not
  repaired by this fork, and no full-stack beta compatibility claim is made.
  Disabling only ModernFix's allocation feature was a test isolation control,
  not a deployed settings change or a validated customer repair.
- The fixture also logged an existing invalid BiomeSpy resource path and routine
  mod/resource warnings; passing tests are not a claim of a warning-free log.

## Exact integration inputs

| Root JAR | SHA256 |
| --- | --- |
| `biolith-neoforge-3.0.14.jar` | `ded9169efb903198046cf44cf91a290ec836ed83a28e29179087eb860e0e6ce9` |
| `BiomesOPlenty-neoforge-1.21.1-21.1.0.14.jar` | `5f3c8d752c2e0464eca8c9527d8ffe498129fd37a2fe86a266b64c98371763b1` |
| `biomespy-neoforge-1.21.1-1.3.3.jar` | `2207ccff37f0631ebfaf692cf5aed9304835298c34df31c39b8558fd8c568acb` |
| `byepregen-1.21.1-1.1.2.5.jar` | `fbf5c03a67dfc0be5c0fc4303622a7bc119ab51d0d033d39aee352d208a71c65` |
| `ElysiumAPI-1.21.1-2.0.1.jar` | `5deb92540de983319693e8022ad9389052f31f60c81440bfca29bcc8000a6759` |
| `featurify-neoforge-2.0.14+mc1.21.1.jar` | `753ce7def330846b46ed49e2985e888314e9d56befcbcbe3339644cbe70a97b9` |
| `GlitchCore-neoforge-1.21.1-2.1.0.2.jar` | `59d2a3fb3d6877e43018fd6a2b199d526074a864276a5f49091409a1cdc62fae` |
| `lithostitched-1.8.0-neoforge-21.1.jar` | `204699b4e3aa3176e2da5b145c6c66887363821f2bd671ba77bd94750036e169` |
| `modernfix-neoforge-5.27.24+mc1.21.1.jar` | `e6e9446890f0feb3aab3f6e73ae18cb17575c370f232179fef7baa30e61538fe` |

## Deployment status

Candidate library only. Full current-pack startup, newly created and dynamic
dimensions, replacement placement/surface coverage, saved-chunk verification,
Immersive Portals/Sable/DH interaction and performance remain unaccepted.
The tests do not establish the cause of any reported grass-covered biome.

The source fixture directory is optional and excluded from distribution. Keep
these precise versions and the explicit ModernFix controls when reproducing the
surface probe; do not describe the unrestricted beta combination as passing.
