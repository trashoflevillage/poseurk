package io.github.trashoflevillage.poseurk.blocks.blockentities.custom;

import io.github.trashoflevillage.poseurk.blocks.blockentities.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class CentrifugeBlockEntity extends BlockEntity {
    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CENTRIFUGE_BLOCK_ENTITY, pos, state);
    }
}
