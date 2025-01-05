package io.github.trashoflevillage.poseurk.client;

import io.github.trashoflevillage.poseurk.items.ModItems;
import io.github.trashoflevillage.poseurk.items.custom.SyringeItem;
import io.github.trashoflevillage.poseurk.util.PoseurkUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

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
                        -(0xFFFFFF - (PoseurkUtil.mixColors(0xa30c0c, PoseurkUtil.getDNAColorOfEntityType(SyringeItem.getEntityType(stack).get(), stack)))), ModItems.SYRINGE);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
                tintIndex == 0 ? -1 :
                    !SyringeItem.hasBlood(stack) ? 0xFFFFFF :
                        -(0xFFFFFF - (PoseurkUtil.mixColors(0xa30c0c, PoseurkUtil.getDNAColorOfEntityType(SyringeItem.getEntityType(stack).get(), stack)))), ModItems.BLOOD_VIAL);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
                tintIndex == 0 ? -1 :
                        !SyringeItem.hasBlood(stack) ? 0xFFFFFF :
                                -(0xFFFFFF - PoseurkUtil.getDNAColorOfEntityType(SyringeItem.getEntityType(stack).get(), stack)), ModItems.DNA_VIAL);
    }
}
