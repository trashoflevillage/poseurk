package io.github.trashoflevillage.poseurk.items.custom;

import io.github.trashoflevillage.poseurk.items.ModComponents;
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

public class DNAVialItem extends Item {
    public DNAVialItem(Settings settings) {
        super(settings);
    }

    public static boolean hasBlood(ItemStack stack) {
        return getEntityType(stack).isPresent();
    }

    public static Optional<EntityType<?>> getEntityType(ItemStack itemStack) {
        String id = itemStack.get(ModComponents.STORED_ENTITY_TYPE);
        if (id != null) {
            Optional<EntityType<?>> type = EntityType.get(id);
            return type;
        }
        return Optional.empty();
    }

    public static ItemStack setEntityType(ItemStack itemStack, EntityType<?> entityType) {
        itemStack.set(ModComponents.STORED_ENTITY_TYPE, Registries.ENTITY_TYPE.getId(entityType).toString());
        return itemStack;
    }

    public static ItemStack removeEntityType(ItemStack itemStack) {
        itemStack.remove(ModComponents.STORED_ENTITY_TYPE);
        return itemStack;
    }

    public static ItemStack setPlayerUUID(ItemStack itemStack, UUID playerUUID) {
        itemStack.set(ModComponents.STORED_PLAYER_UUID, playerUUID);
        return itemStack;
    }

    public static UUID getPlayerUUID(ItemStack itemStack) {
        return itemStack.get(ModComponents.STORED_PLAYER_UUID);
    }

    public static ItemStack removePlayerUUID(ItemStack itemStack) {
        itemStack.remove(ModComponents.STORED_PLAYER_UUID);
        return itemStack;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Text text;
        if (hasBlood(stack)) {
            if (getEntityType(stack).get() != EntityType.PLAYER) {
                text = getEntityType(stack).get().getName().getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
            } else {
                UUID uuid = getPlayerUUID(stack);
                if (uuid != null) {
                    ServerPlayerEntity playerEntity = MinecraftClient.getInstance().getServer().getPlayerManager().getPlayer(uuid);
                    if (playerEntity != null) {
                        text = playerEntity.getName()
                                .getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                    } else {
                        text = getEntityType(stack).get().getName().getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                    }
                } else {
                    text = getEntityType(stack).get().getName().getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                }
            }
        } else {
            text = Text.translatable("item.poseurk.syringe.empty_description").getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
        }
        tooltip.add(text);
    }

    public static ItemStack emptyContents(ItemStack stack) {
        removePlayerUUID(stack);
        removeEntityType(stack);
        return stack;
    }

    @Override
    public ItemStack getDefaultStack() {
        return emptyContents(super.getDefaultStack());
    }
}
