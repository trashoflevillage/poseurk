package io.github.trashoflevillage.poseurk.util;

import io.github.trashoflevillage.poseurk.Poseurk;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public class ModTags {
    public static class Blocks {
        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(Poseurk.MOD_ID, name));
        }
    }

    public static class Items {
        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Poseurk.MOD_ID, name));
        }
    }

    public static class Structures {
        private static TagKey<Structure> createTag(String name) {
            return TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(Poseurk.MOD_ID, name));
        }
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> HAS_NO_BLOOD =
                createTag("has_no_blood");

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Poseurk.MOD_ID, name));
        }
    }
}
