package io.github.trashoflevillage.poseurk.blocks;

import io.github.trashoflevillage.poseurk.Poseurk;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.function.ToIntFunction;

public class ModBlocks {
    private static Block registerBlock(String name, Block block) {
        return registerBlock(name, block, true);
    }

    private static Block registerBlock(String name, Block block, boolean hasBlockItem) {
        if (hasBlockItem) registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK,
                Identifier.of(Poseurk.MOD_ID, name),
                block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(Poseurk.MOD_ID, name),
                new BlockItem(block,
                        new Item.Settings()));
    }

    private static Block registerBlock(String name, Block block, Item.Settings itemSettings) {
        registerBlockItem(name, block, itemSettings);
        return Registry.register(Registries.BLOCK,
                Identifier.of(Poseurk.MOD_ID, name),
                block);
    }

    private static Item registerBlockItem(String name, Block block, Item.Settings settings) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(Poseurk.MOD_ID, name),
                new BlockItem(block,
                        settings));
    }

    public static void registerModBlocks() {
        Poseurk.LOGGER.info("Registering blocks for " + Poseurk.MOD_ID);
    }

    private static void addWaxingRecipe(Block from, Block to) {
        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(from, to);
    }

    public static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return createLightLevelFromLitBlockState(litLevel, 0);
    }

    public static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel, int unlitLevel) {
        return (state) -> {
            return (Boolean)state.get(Properties.LIT) ? litLevel : unlitLevel;
        };
    }
}
