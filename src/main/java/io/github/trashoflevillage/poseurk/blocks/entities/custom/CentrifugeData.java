package io.github.trashoflevillage.poseurk.blocks.entities.custom;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;

public record CentrifugeData(BlockPos pos) {
    public static final PacketCodec<RegistryByteBuf, CentrifugeData> PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, CentrifugeData::pos, CentrifugeData::new);
}
