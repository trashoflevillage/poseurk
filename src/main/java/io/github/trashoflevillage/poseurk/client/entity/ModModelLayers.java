package io.github.trashoflevillage.poseurk.client.entity;

import io.github.trashoflevillage.poseurk.Poseurk;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer CENTRIFUGE = registerMain("centrifuge");

    private static EntityModelLayer registerMain(String id) {
        return new EntityModelLayer(Identifier.of(Poseurk.MOD_ID, id), "main");
    }
}
