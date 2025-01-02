package io.github.trashoflevillage.poseurk.client;

import io.github.trashoflevillage.poseurk.items.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public class PoseurkClient implements ClientModInitializer {
    private static final Block[] blocksWithTransparency = new Block[] {

    };

    @Override
    public void onInitializeClient() {
        for (Block i : blocksWithTransparency)
            BlockRenderLayerMap.INSTANCE.putBlock(i, RenderLayer.getCutout());

        registerItemColor(ModItems.SYRINGE, -0x00FFFF);
    }

    private void registerItemColor(Item item, int defaultColor) {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? -1 : DyedColorComponent.getColor(stack, defaultColor), item);
    }
}
