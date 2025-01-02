package io.github.trashoflevillage.poseurk.items;

import io.github.trashoflevillage.poseurk.Poseurk;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItemGroups {
    public static final ItemGroup POSEURK_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(Poseurk.MOD_ID, "poseurk"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.poseurk"))
                    .icon(ModItems.SYRINGE::getDefaultStack)
                    .entries(((displayContext, entries) -> {
                        entries.add(ModItems.SYRINGE);
                    }))
                    .build()
    );

    public static void registerItemGroups() {

    }

    private static void addItemsToItemGroup(RegistryKey<ItemGroup> group, ItemConvertible... items) {
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
            for (ItemConvertible i : items)
                content.add(i);
        });
    }
}
