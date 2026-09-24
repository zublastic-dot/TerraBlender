package terrablender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.RegionType;
import terrablender.api.SurfaceRuleManager;
import terrablender.worldgen.IExtendedParameterList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import terrablender.util.LevelUtils;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PassiveCompatibilityTest {
    @BeforeAll static void bootstrapRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }
    static class Rule implements SurfaceRules.RuleSource {
        int applications;
        final SurfaceRules.SurfaceRule result = (x, y, z) -> null;
        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
            applications++; return result;
        }
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() { return null; }
    }
    @Test void initializationNeverReadsOrChangesAnyDimension() {
        // Null inputs fail immediately if these paths access a server, registry,
        // generator, dimension tag, or settings. The rule applies to dynamic dimensions too.
        LevelUtils.initializeOnServerStart(null);
        assertFalse(LevelUtils.shouldApplyToBiomeSource(null));
        assertFalse(LevelUtils.shouldApplyToChunkGenerator(null));
        assertNull(LevelUtils.getRegionTypeForDimension(null));
        for (long seed : new long[]{0, -1, Long.MIN_VALUE, Long.MAX_VALUE}) {
            LevelUtils.initializeBiomes(null, null, null, null, seed);
        }
    }
    @Test void extendedLookupDelegatesWithoutCreatingRegionalTrees() {
        var nativeParameters = new Climate.ParameterList<String>(List.of(
            Pair.of(Climate.parameters(-1, -1, -1, -1, -1, -1, 0), "cold/dry"),
            Pair.of(Climate.parameters(1, 1, 1, 1, 1, 1, 0), "warm/wet")));
        var parameters = (IExtendedParameterList<String>) (Object) nativeParameters;
        for (RegionType type : RegionType.values()) {
            parameters.initializeForTerraBlender(null, type, 1234L);
            parameters.recreateUniqueness();
            assertFalse(parameters.isInitialized());
            assertEquals(0, parameters.getTreeCount());
            for (int coordinate : new int[]{Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}) {
                assertEquals(0, parameters.getUniqueness(coordinate, coordinate, coordinate));
                assertNull(parameters.getTree(coordinate));
                assertNull(parameters.getRegion(coordinate));
                for (float climate : new float[]{-1, 0, 1}) {
                    var target = Climate.target(climate, climate, climate, climate, climate, climate);
                    assertEquals(nativeParameters.findValue(target), parameters.findValuePositional(target, coordinate, coordinate, coordinate));
                }
            }
        }
    }
    @Test void registrationIsAvailableButCannotReplaceFallbackRules() {
        Rule fallback = new Rule(), registered = new Rule();
        for (var category : SurfaceRuleManager.RuleCategory.values()) {
            SurfaceRuleManager.addSurfaceRules(category, "test_provider", registered);
            SurfaceRuleManager.setDefaultSurfaceRules(category, registered);
            for (var stage : SurfaceRuleManager.RuleStage.values())
                SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(category, stage, 100, registered);
            assertSame(fallback, SurfaceRuleManager.getNamespacedRules(category, fallback));
            var snapshot = SurfaceRuleManager.getRegisteredSurfaceRules(category);
            assertSame(registered, snapshot.get("test_provider"));
            assertThrows(UnsupportedOperationException.class, () -> snapshot.clear());
            SurfaceRuleManager.removeSurfaceRules(category, "test_provider");
            assertSame(registered, snapshot.get("test_provider"));
        }
        assertEquals(0, registered.applications);
        assertEquals(0, fallback.applications);
    }
    @Test void legacyMergedRuleCannotDispatchEvenThroughAnOptimizer() throws Exception {
        Rule base = new Rule(), forbidden = new Rule();
        var source = new NamespacedSurfaceRuleSource(base, Map.of("minecraft", forbidden, "test_provider", forbidden));
        assertSame(base, source.base());
        assertTrue(source.sources().isEmpty());
        var context = new SurfaceRules.Context(null, null, null, null, null, null, null);
        if (Boolean.getBoolean("expectModernFixSurface")) {
            var extension = Class.forName("org.embeddedt.modernfix.world.gen.ExtendedSurfaceContext");
            assertTrue(extension.isInstance(context), "ModernFix's surface optimization must be active for this fixture");
            var possible = (ThreadLocal) extension.getField("COMPUTED_POSSIBLE_BIOMES").get(null);
            possible.set(java.util.Set.of(net.minecraft.world.level.biome.Biomes.PLAINS));
            extension.getMethod("mfix$applyPossibleBiomes").invoke(context);
        }
        assertSame(base.result, source.apply(context));
        assertEquals(1, base.applications);
        assertEquals(0, forbidden.applications);
    }
}
