package io.github.trashoflevillage.poseurk.client;

import io.github.trashoflevillage.poseurk.items.ModItems;
import io.github.trashoflevillage.poseurk.items.custom.SyringeItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class PoseurkClient implements ClientModInitializer {
    private static final Block[] blocksWithTransparency = new Block[] {

    };

    @Override
    public void onInitializeClient() {
        for (Block i : blocksWithTransparency)
            BlockRenderLayerMap.INSTANCE.putBlock(i, RenderLayer.getCutout());

        ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
                tintIndex == 0 ? -1 :
                !SyringeItem.hasBlood(stack) ? 0xFFFFFF :
                -(0xFFFFFF - SyringeItem.getBloodColorOfEntityType(SyringeItem.getEntityType(stack))), ModItems.SYRINGE);
    }
}
