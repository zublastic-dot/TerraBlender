# TerraBlender Passive operating contract

Read `zublastic-dot/zublastic-context/BOOTSTRAP.md` and follow the canonical ModAutomator CAS lease/preflight protocol before repository mutations. Work on leased `work/...` branches; preserve LGPL-3.0 and upstream attribution.

This fork retains the TerraBlender 4.1 API for NeoForge 1.21.1 but has no automatic biome, surface, dimension, validation or client-warning replacement behavior. Keep inert extension interfaces for dependent mods. Do not add per-chunk hooks, a dimension allowlist, or an optional switch that reactivates region placement. Registered author data can be exported; it must not become active worldgen implicitly. Required schema/codec registration is a compatibility responsibility.

Do not install this fork into a save without validating replacement biome and surface data for every affected dimension. A passing build/startup is not worldgen or Immersive Portals acceptance. Tests and artifact reports must state their exact modstack and limits.
