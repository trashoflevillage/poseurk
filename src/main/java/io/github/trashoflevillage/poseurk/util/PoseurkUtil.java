package io.github.trashoflevillage.poseurk.util;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.tag.EntityTypeTags;

public class PoseurkUtil {
    public static int mixColors(int colorA, int colorB) {
        float[] arrayA = getColorArray(colorA);
        float[] arrayB = getColorArray(colorB);
        float[] newArray = new float[] {
                (arrayA[0] + arrayB[0]) / 2,
                (arrayA[1] + arrayB[1]) / 2,
                (arrayA[2] + arrayB[2]) / 2
        };
        return getColorFromArray(newArray);
    }

    private static int getColorFromArray(float[] array) {
        int red = Math.round(array[0] * 255);
        int green = Math.round(array[1] * 255);
        int blue = Math.round(array[2] * 255);

        return (red << 16) | (green << 8) | blue;
    }

    private static float[] getColorArray(int n) {
        int j = (n & 0xFF0000) >> 16;
        int k = (n & 0xFF00) >> 8;
        int l = (n & 0xFF) >> 0;
        return new float[]{(float) j / 255.0f, (float) k / 255.0f, (float) l / 255.0f};
    }

    public static int getDNAColorOfEntityType(EntityType<?> type) {
        if (type != null) {
            if (type == EntityType.PLAYER) return 0xFF0000;
            if (type == EntityType.ENDERMAN || type == EntityType.ENDERMITE || type == EntityType.SHULKER) return 0xaa4cf7;
            if (type.isIn(EntityTypeTags.ZOMBIES)) return 0x108d26;
            if (type.isIn(EntityTypeTags.ILLAGER) || type == EntityType.RAVAGER) return 0x727272;
            if (type == EntityType.VILLAGER) return 0x705041;
            if (type == EntityType.CREEPER) return 0x58f272;
        }
        return 0xa30c0c;
    }
}
