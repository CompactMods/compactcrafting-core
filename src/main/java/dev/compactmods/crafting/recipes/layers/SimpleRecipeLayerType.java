package dev.compactmods.crafting.recipes.layers;

import com.mojang.serialization.Codec;
import dev.compactmods.crafting.api.recipe.layers.IRecipeLayer;
import dev.compactmods.crafting.api.recipe.layers.RecipeLayerType;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SimpleRecipeLayerType<T extends IRecipeLayer>
        extends ForgeRegistryEntry<RecipeLayerType<?>>
        implements RecipeLayerType<T> {

    private final Codec<T> s;
    public SimpleRecipeLayerType(Codec<T> s) {
        this.s = s;
    }

    @Override
    public Codec<T> getCodec() {
        return s;
    }
}