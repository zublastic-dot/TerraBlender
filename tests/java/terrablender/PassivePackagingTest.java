package terrablender;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PassivePackagingTest {
    @Test void expectedModsAreActuallyLoaded() {
        var loaded = net.neoforged.fml.loading.LoadingModList.get().getMods().stream()
            .map(mod -> mod.getModId()).collect(java.util.stream.Collectors.toSet());
        System.out.println("Transformed test environment mods: " + new TreeSet<>(loaded));
        assertTrue(loaded.contains("terrablender"));
        for (var expected : System.getProperty("expectedCompatModIds", "").split(","))
            if (!expected.isBlank()) assertTrue(loaded.contains(expected), "Fixture was not loaded: " + expected);
    }
    @Test void noGenerationOrClientInterceptionsAreActivated() throws Exception {
        var config = JsonParser.parseString(new String(getClass().getResourceAsStream("/terrablender.mixins.json").readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
        assertEquals(0, config.getAsJsonArray("client").size());
        var names = new HashSet<String>();
        for (var entry : config.getAsJsonArray("mixins")) names.add(entry.getAsString());
        assertEquals(Set.of("MixinBiomeSource", "MixinBuiltInRegistries", "MixinMultiNoiseBiomeSource", "MixinNoiseGeneratorSettings", "MixinParameterList", "MixinTheEndBiomeSource", "MultiNoiseBiomeSourceAccess"), names);
        for (String name : names) {
            if (name.equals("MixinBuiltInRegistries")) continue; // codec registration only
            try (var stream = getClass().getResourceAsStream("/terrablender/mixin/"+name+".class")) {
                assertNotNull(stream);
                new ClassReader(stream).accept(new ClassVisitor(Opcodes.ASM9) {
                    public MethodVisitor visitMethod(int access, String method, String desc, String signature, String[] exceptions) {
                        return new MethodVisitor(Opcodes.ASM9) {
                            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                                assertFalse(descriptor.contains("/injection/"), name+"."+method+" intercepts generation");
                                return null;
                            }
                        };
                    }
                }, 0);
            }
        }
    }
    @Test void neoforgeEntrypointDoesNotRegisterWorldEvents() throws Exception {
        try (var stream = getClass().getResourceAsStream("/terrablender/core/TerraBlenderNeoForge.class")) {
            assertNotNull(stream);
            new ClassReader(stream).accept(new ClassVisitor(Opcodes.ASM9) {
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    return new MethodVisitor(Opcodes.ASM9) {
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            assertNotEquals("addListener", name, "entrypoint registers an automatic hook");
                            assertFalse(owner.contains("InitializationHandler"));
                        }
                    };
                }
            }, 0);
        }
    }
}
