package io.github.trashoflevillage.poseurk.blocks.entities;

import io.github.trashoflevillage.poseurk.Poseurk;
import io.github.trashoflevillage.poseurk.blocks.ModBlocks;
import io.github.trashoflevillage.poseurk.blocks.entities.custom.CentrifugeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<CentrifugeBlockEntity> CENTRIFUGE_BLOCK_ENTITY =
            register("centrifuge", CentrifugeBlockEntity::new, ModBlocks.CENTRIFUGE);

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        Identifier id = Identifier.of(Poseurk.MOD_ID, name);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.<T>create(entityFactory, blocks).build());
    }

    public static void registerBlockEntities() {

    }
}
