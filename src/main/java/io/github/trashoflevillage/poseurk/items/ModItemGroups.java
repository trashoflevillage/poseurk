package io.github.trashoflevillage.poseurk.items;

import io.github.trashoflevillage.poseurk.Poseurk;
import io.github.trashoflevillage.poseurk.items.custom.BloodVialItem;
import io.github.trashoflevillage.poseurk.items.custom.SyringeItem;
import io.github.trashoflevillage.poseurk.util.ModTags;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup POSEURK_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(Poseurk.MOD_ID, "poseurk"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.poseurk"))
                    .icon(ModItems.EMPTY_VIAL::getDefaultStack)
                    .entries(((displayContext, entries) -> {
                        entries.add(ModItems.SYRINGE.getDefaultStack());
                        entries.add(ModItems.EMPTY_VIAL.getDefaultStack());
                    }))
                    .build()
    );

    public static final ItemGroup BLOOD_AND_DNA_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(Poseurk.MOD_ID, "blood_and_dna"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.blood_and_dna"))
                    .icon(() -> {
                        return SyringeItem.setEntityType(ModItems.SYRINGE.getDefaultStack(), EntityType.PLAYER);
                    })
                    .entries(((displayContext, entries) -> {
                        registerAllVials(entries);
                    }))
                    .build()
    );

    public static void registerItemGroups() {

    }

    private static void registerAllVials(ItemGroup.Entries entries) {
        Registries.ENTITY_TYPE.forEach((entityType -> {
            if ((entityType.getSpawnGroup() != SpawnGroup.MISC || entityType == EntityType.PLAYER || entityType == EntityType.VILLAGER) && !entityType.isIn(ModTags.EntityTypes.HAS_NO_BLOOD)) {
                ItemStack bloodVial = ModItems.BLOOD_VIAL.getDefaultStack();
                BloodVialItem.setEntityType(bloodVial, entityType);
                entries.add(bloodVial);
                ItemStack dnaVial = ModItems.DNA_VIAL.getDefaultStack();
                BloodVialItem.setEntityType(dnaVial, entityType);
                entries.add(dnaVial);
            }
        }));
    }

    private static void addItemsToItemGroup(RegistryKey<ItemGroup> group, ItemConvertible... items) {
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
            for (ItemConvertible i : items)
                content.add(i);
        });
    }
}
