package io.github.trashoflevillage.poseurk.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

public class PoseurkUtil {
    private static final HashMap<EntityType<?>, Integer> entityDNAColorCache = new HashMap<>();

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
        if (!entityDNAColorCache.containsKey(type)) entityDNAColorCache.put(type, generateDNAColorOfEntityType(type));
        return entityDNAColorCache.get(type);
    }

    // ChatGPT helped IMMENSELY with this system...
    // I had no idea where to begin, and I'm not going to take credit for something written largely by AI.
    public static int generateDNAColorOfEntityType(EntityType<?> type) {
        if (type != null && type != EntityType.PLAYER) {
            try {
                Optional<Identifier> texture = getEntityTexture(type);
                if (texture.isPresent()) {
                    Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(texture.get()).get();
                    try (InputStream stream = resource.getInputStream()) {
                        BufferedImage image = ImageIO.read(stream);
                        return getAverageColor(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0x999999;
    }

    private static Optional<Identifier> getEntityTexture(EntityType<?> type) {
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();

        var world = client.world;
        if (world == null) {
            return Optional.empty();
        }

        var entity = type.create(world);
        if (entity == null) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        EntityRenderer<? super Entity> renderer = (EntityRenderer<? super Entity>) dispatcher.getRenderer(entity);

        if (renderer != null) {
            return Optional.of(renderer.getTexture(entity));
        }

        return Optional.empty();
    }

    private static int getAverageColor(BufferedImage image) {
        long redSum = 0, greenSum = 0, blueSum = 0;
        int pixelCount = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;
                if (alpha > 0) {
                    redSum += (pixel >> 16) & 0xFF;
                    greenSum += (pixel >> 8) & 0xFF;
                    blueSum += pixel & 0xFF;
                    pixelCount++;
                }
            }
        }

        if (pixelCount > 0) {
            int avgRed = (int) (redSum / pixelCount);
            int avgGreen = (int) (greenSum / pixelCount);
            int avgBlue = (int) (blueSum / pixelCount);
            return (avgRed << 16) | (avgGreen << 8) | avgBlue;
        }

        return 0x999999;
    }
}
