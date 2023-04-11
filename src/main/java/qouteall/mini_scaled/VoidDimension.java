package qouteall.mini_scaled;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.*;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.McHelper;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.api.DimensionAPI;
import qouteall.q_misc_util.ducks.IEGeneratorOptions;

import java.util.ArrayList;
import java.util.Optional;

public class VoidDimension {
    public static final ResourceKey<Level> dimensionId = ResourceKey.create(
        Registry.DIMENSION_REGISTRY,
        new ResourceLocation("mini_scaled:void")
    );
    
    static void initializeVoidDimension(
        WorldGenSettings generatorOptions, RegistryAccess registryManager
    ) {
        Registry<LevelStem> registry = generatorOptions.dimensions();
        
        Holder<DimensionType> dimType = registryManager.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).getHolder(
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("mini_scaled:void_dim_type"))
        ).orElseThrow(() -> new RuntimeException("Missing dimension type mini_scaled:void_dim_type"));
        
        ResourceLocation dimId = new ResourceLocation("mini_scaled:void");
        
        DimensionAPI.addDimension(
            registry, dimId, dimType,
            createVoidGenerator(registryManager)
        );
    }
    
    private static ChunkGenerator createVoidGenerator(RegistryAccess rm) {
        Registry<Biome> biomeRegistry = rm.registryOrThrow(Registry.BIOME_REGISTRY);

        FlatLevelGeneratorSettings flatChunkGeneratorConfig =
            new FlatLevelGeneratorSettings(
                Optional.of(HolderSet.direct()), // disable structure generation
                biomeRegistry
            );
        flatChunkGeneratorConfig.getLayersInfo().add(new FlatLayerInfo(1, Blocks.AIR));
        flatChunkGeneratorConfig.updateLayers();

        Registry<StructureSet> structureSetRegistry = new DefaultedRegistry<>(
                "structure_sets",
                Registry.STRUCTURE_SET_REGISTRY,
                Lifecycle.stable(),
                null
        );

        return new FlatLevelSource(
            structureSetRegistry,
            flatChunkGeneratorConfig
        );
    }
    
    public static ServerLevel getVoidWorld() {
        return McHelper.getServerWorld(dimensionId);
    }
    
    @Environment(EnvType.CLIENT)
    public static class VoidSkyProperties extends DimensionSpecialEffects {
        public VoidSkyProperties() {
            super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
        }
        
        public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
            return color.multiply((double) (sunHeight * 0.94F + 0.06F), (double) (sunHeight * 0.94F + 0.06F), (double) (sunHeight * 0.91F + 0.09F));
        }
        
        public boolean isFoggyAt(int camX, int camY) {
            return false;
        }
    }
}
