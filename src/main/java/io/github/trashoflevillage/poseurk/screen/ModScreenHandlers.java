package io.github.trashoflevillage.poseurk.screen;

import io.github.trashoflevillage.poseurk.Poseurk;
import io.github.trashoflevillage.poseurk.blocks.entities.custom.CentrifugeData;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Poseurk.MOD_ID, "centrifuge"),
                    new ExtendedScreenHandlerType<>(CentrifugeScreenHandler::new, CentrifugeData.PACKET_CODEC));

    public static void registerScreenHandlers() {

    }
}
