package io.github.trashoflevillage.poseurk.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import io.github.trashoflevillage.poseurk.items.ModComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class PoseurkUtil {
    private static final HashMap<EntityType<?>, Integer> entityDNAColorCache = new HashMap<>();
    private static final HashMap<UUID, Integer> playerDNAColorCache = new HashMap<>();
    private static final HashMap<UUID, String> playerUsernameCache = new HashMap<>();

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


    public static int getDNAColorOfEntityType(EntityType<?> type, ItemStack stack) {
        if (type != EntityType.PLAYER && !entityDNAColorCache.containsKey(type)) entityDNAColorCache.put(type, generateDNAColorOfEntityType(type));
        else if (type == EntityType.PLAYER) {
            UUID uuid = stack.get(ModComponents.STORED_PLAYER_UUID);
            if (!playerDNAColorCache.containsKey(uuid))
                playerDNAColorCache.put(uuid, generateDNAColorOfPlayer(uuid));
            return playerDNAColorCache.get(uuid);
        }
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

    public static int generateDNAColorOfPlayer(UUID playerUUID) {
        BufferedImage image = fetchPlayerSkinAsImage(playerUUID);
        if (image != null) {
            return getAverageColor(image);
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

    public static BufferedImage fetchPlayerSkinAsImage(UUID uuid) {
        if (uuid == null) return null;

        try {
            // Mojang API URL (UUID without dashes)
            String uuidString = uuid.toString().replace("-", "");
            URL profileUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);

            // Read and log the response
            try (InputStreamReader reader = new InputStreamReader(profileUrl.openStream())) {
                JsonObject profileJson = JsonParser.parseReader(reader).getAsJsonObject();

                if (!profileJson.has("properties")) {
                    return null;
                }

                JsonObject texturesProperty = profileJson.getAsJsonArray("properties").get(0).getAsJsonObject();
                String decodedTextures = new String(Base64.getDecoder().decode(texturesProperty.get("value").getAsString()));
                JsonObject texturesJson = JsonParser.parseString(decodedTextures).getAsJsonObject();

                if (!texturesJson.has("textures") || !texturesJson.getAsJsonObject("textures").has("SKIN")) {
                    return null;
                }

                String skinUrl = texturesJson.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                return ImageIO.read(new URL(skinUrl));
            }
        } catch (IOException | IllegalStateException e) {
            return null;
        }
    }

    public static String getUsernameFromUUID(UUID uuid) {
        if (!playerUsernameCache.containsKey(uuid)) playerUsernameCache.put(uuid, fetchUsernameFromUUID(uuid));
        return playerUsernameCache.get(uuid);
    }

    public static String fetchUsernameFromUUID(UUID uuid) {
        if (uuid == null) return null;

        try {
            String uuidString = uuid.toString().replace("-", "");

            URL profileUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);

            try (InputStreamReader reader = new InputStreamReader(profileUrl.openStream())) {
                JsonObject profileJson = JsonParser.parseReader(reader).getAsJsonObject();

                if (profileJson.has("name")) {
                    return profileJson.get("name").getAsString();
                } else {
                    return null;
                }
            }
        } catch (IOException | IllegalStateException e) {
            return null;
        }
    }
}
