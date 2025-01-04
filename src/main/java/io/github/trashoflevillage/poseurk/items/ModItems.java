package io.github.trashoflevillage.poseurk.items;

import io.github.trashoflevillage.poseurk.Poseurk;
import io.github.trashoflevillage.poseurk.items.custom.BloodVialItem;
import io.github.trashoflevillage.poseurk.items.custom.SyringeItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SYRINGE = registerItem("syringe", new SyringeItem(new Item.Settings().maxCount(1)));
    public static final Item EMPTY_VIAL = registerItem("empty_vial", new Item(new Item.Settings()));
    public static final Item BLOOD_VIAL = registerItem("blood_vial", new BloodVialItem(new Item.Settings()));
    public static final Item DNA_VIAL = registerItem("blood_vial", new DNAVialItem(new Item.Settings()));

    public static void registerModItems() {
        Poseurk.LOGGER.info("Registering items for " + Poseurk.MOD_ID + ".");
    }


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Poseurk.MOD_ID, name), item);
    }
}