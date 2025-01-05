package io.github.trashoflevillage.poseurk.items;

import com.mojang.serialization.Codec;
import io.github.trashoflevillage.poseurk.Poseurk;
import io.github.trashoflevillage.poseurk.util.PoseurkUtil;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ModComponents {
    public static final ComponentType<String> STORED_ENTITY_TYPE = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of(Poseurk.MOD_ID, "stored_entity_type"),
        ComponentType.<String>builder().codec(Codec.STRING).build()
    );
    public static final ComponentType<UUID> STORED_PLAYER_UUID = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Poseurk.MOD_ID, "stored_player_uuid"),
            ComponentType.<UUID>builder().codec(Codec.STRING.xmap(
                    PoseurkUtil::getUUIDFromStringSafely, UUID::toString)
            ).build()
    );

    public static void registerComponents() {

    }
}
