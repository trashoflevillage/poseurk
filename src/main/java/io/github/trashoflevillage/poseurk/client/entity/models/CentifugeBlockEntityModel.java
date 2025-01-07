package io.github.trashoflevillage.poseurk.client.entity.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class CentifugeBlockEntityModel extends EntityModel<Entity> {
    private final ModelPart main;
    private final ModelPart base;
    private final ModelPart stand;
    private final ModelPart holders;
    public CentifugeBlockEntityModel(ModelPart root) {
        this.main = root.getChild("main");
        this.base = this.main.getChild("base");
        this.stand = this.main.getChild("stand");
        this.holders = this.main.getChild("holders");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 16.0F, 0.0F));

        ModelPartData base = main.addChild("base", ModelPartBuilder.create().uv(0, 16).cuboid(-15.0F, -2.0F, 5.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 16).cuboid(-7.0F, -2.0F, 1.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 16).cuboid(-7.0F, -2.0F, 9.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 8.0F, -8.0F));

        ModelPartData stand = main.addChild("stand", ModelPartBuilder.create().uv(20, 0).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData holders = main.addChild("holders", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-8.0F, -8.0F, 0.0F, 7.0F, 14.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = holders.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, -14.0F, 0.0F, 7.0F, 14.0F, 0.0F, new Dilation(0.0F))
                .uv(0, -7).cuboid(0.0F, -14.0F, -8.0F, 0.0F, 14.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 6.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        main.render(matrices, vertices, light, overlay, color);
    }
}