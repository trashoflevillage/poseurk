package io.github.trashoflevillage.poseurk.client.entity.renderers;

import io.github.trashoflevillage.poseurk.Poseurk;
import io.github.trashoflevillage.poseurk.blocks.blockentities.custom.CentrifugeBlockEntity;
import io.github.trashoflevillage.poseurk.client.entity.ModModelLayers;
import io.github.trashoflevillage.poseurk.client.entity.models.CentifugeBlockEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CentrifugeBlockEntityRenderer implements BlockEntityRenderer<CentrifugeBlockEntity> {
    private static final CentifugeBlockEntityModel model = new CentifugeBlockEntityModel(CentifugeBlockEntityModel.getTexturedModelData().createModel());

    @Override
    public void render(CentrifugeBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5, 1.5, 0.5);

        matrices.scale(-1.0F, -1.0F, 1.0F);

        // Bind the texture and render the model
        Identifier texture = Identifier.of(Poseurk.MOD_ID, "textures/block/centrifuge.png");
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        model.render(matrices, vertexConsumer, light, overlay);

        matrices.pop();
    }
}
