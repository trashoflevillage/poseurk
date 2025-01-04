package io.github.trashoflevillage.poseurk.items.custom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BloodVialItem extends Item {
    public BloodVialItem(Settings settings) {
        super(settings);
    }

    public static boolean hasBlood(ItemStack stack) {
        return getEntityType(stack) != null;
    }

    public static EntityType<?> getEntityType(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt();
        if (nbt.contains("storedEntityType")) {
            Optional<EntityType<?>> type = EntityType.get(nbt.getString("storedEntityType"));
            return type.orElse(null);
        }
        else return null;
    }

    public static void setEntityType(ItemStack itemStack, EntityType entityType) {
        NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt();
        nbt.putString("storedEntityType", Registries.ENTITY_TYPE.getId(entityType).toString());
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static void removeEntityType(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt();
        nbt.remove("storedEntityType");
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static void setPlayerUUID(ItemStack itemStack, UUID playerUUID) {
        NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt();
        nbt.putUuid("storedPlayerUUID", playerUUID);
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static UUID getPlayerUUID(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt();
        if (nbt.contains("storedPlayerUUID"))
            return nbt.getUuid("storedPlayerUUID");
        else return null;
    }

    public static void removePlayerUUID(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).getNbt();
        nbt.remove("storedPlayerUUID");
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Text text;
        if (hasBlood(stack)) {
            if (getEntityType(stack) != EntityType.PLAYER) {
                text = getEntityType(stack).getName().getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
            } else {
                UUID uuid = getPlayerUUID(stack);
                if (uuid != null) {
                    ServerPlayerEntity playerEntity = MinecraftClient.getInstance().getServer().getPlayerManager().getPlayer(uuid);
                    if (playerEntity != null) {
                        text = playerEntity.getName()
                                .getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                    } else {
                        text = getEntityType(stack).getName().getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                    }
                } else {
                    text = getEntityType(stack).getName().getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                }
            }
        } else {
            text = Text.translatable("item.poseurk.syringe.empty_description").getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
        }
        tooltip.add(text);
    }

    public static void emptyContents(ItemStack stack) {
        removePlayerUUID(stack);
        removeEntityType(stack);
    }
}
