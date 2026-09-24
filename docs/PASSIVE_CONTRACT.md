# Passive runtime contract

The supported target is Minecraft 1.21.1 with NeoForge. This fork exists because
a separate world-authoring system owns the shared biome layout and surfaces.
Grouping by provider is deliberately absent; environmental suitability belongs in
that separately generated data. This change does not implement that authoring system.

## Runtime boundaries

- `LevelUtils.initializeOnServerStart` is a no-op and is not registered on the
  NeoForge server-start event bus. Both `shouldApply` methods return false and
  dimension-to-region lookup returns null, regardless of dimension tags.
- `initializeBiomes` always exits before accessing its arguments. The unreachable
  upstream body retains invocation targets and local-variable types used by
  BiomeSpy and other external mixins. Its private unconditional gate has no config,
  dimension-specific exemption, or public reactivation API.
- Extension interfaces remain on vanilla classes. Parameter lists stay
  uninitialized, contain zero TerraBlender trees, and delegate positional requests
  to the normal `findValue`. End initialization, surface-category assignment and
  deferred-biome appends are inert. Vanilla clone-compatible APIs remain available.
- Automatic biome-query, noise-generator clone, surface getter, validation,
  world lifecycle and client warning mixins are not activated. The remaining
  registry hook registers the legacy merged-rule codec only.
- `SurfaceRuleManager` retains registration records for offline authoring.
  `getNamespacedRules` always returns its supplied fallback by identity.
  `getRegisteredSurfaceRules` returns an immutable snapshot for authoring tools;
  registration does not bind those rules to a running dimension.
- Legacy merged rules discard their runtime namespace map even when constructed
  directly or decoded. This matters because optimizers can read `sources()` and
  the corresponding field, bypassing `apply`. The base rule is retained.
  A private, unreachable `Set.forEach` site keeps ModernFix's mixin target valid.
  The nested legacy rule also delegates to its base without reading the biome.

No code here can stop an arbitrary third-party coremod from replacing these
methods. Compatibility claims must therefore identify the exact versions tested.
Retaining APIs is not a promise that every binary or reflective integration works.

## Known integration contracts examined

- Biolith 3.0.14 checks `isInitialized()` before trying TerraBlender's regional
  lookup; false causes its TerraBlender-specific path to decline.
- BiomeSpy 1.3.3 injects after parameter-list initialization and captures a region
  type and parameter list. The unreachable upstream anchor preserves that target.
- Featurify 2.0.14 declares optional invocation targets (`require=0`); Elysium API
  2.0.1's additional initialization consults the now-null dimension region type.
- ModernFix 5.27.24 can intercept merged-rule application and directly inspect
  namespace sources. The empty map and retained invocation anchor are both needed.
- ByePregen 1.1.2.5 reconstructs a merged source from its accessors; reconstruction
  still carries an empty namespace map and retains the base rule.
- Lithostitched 1.8.0's TerraBlender compatibility wraps vanilla biome-fill calls;
  those vanilla targets are not removed by this fork.

These code observations are not, by themselves, proof of a complete modpack startup
or correct generated world. Exact local fixture results accompany the delivery report.

## Acceptance and deployment

Automated tests cover inert initialization calls, transformed parameter-list
lookup, registered-rule isolation, direct merged-rule behavior, active mixin
inventory, startup listener absence and actual fixture discovery. Source/JAR
identities must be recorded with test results.

Before installing on a real pack: preserve the previous library, review every
supported dimension's effective biome source and surfaces, validate replacement
resources against the exact mod set, and check representative generated chunks.
Test a newly created/dynamic dimension and the required portal interactions too.
Do not treat a JUnit pass or a startup log as proof of correct terrain, acceptable
CPS, or full Immersive Portals compatibility. Existing chunks are not rewritten.

[Exact local fixture results and known beta limitation](VALIDATION-2026-09-24.md).
