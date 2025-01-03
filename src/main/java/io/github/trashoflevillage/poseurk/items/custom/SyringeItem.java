package io.github.trashoflevillage.poseurk.items.custom;

import com.google.common.base.Predicates;
import com.mojang.authlib.GameProfile;
import io.github.trashoflevillage.poseurk.items.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SyringeItem extends Item {

    public SyringeItem(Settings settings) {
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

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (hasBlood(itemStack)) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int DAMAGE_AMOUNT = 4;
        StatusEffectInstance WEAKNESS_EFFECT = new StatusEffectInstance(StatusEffects.WEAKNESS, 600);

        if (user instanceof PlayerEntity) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            float f = getPullProgress(i);
            if (f == 1.0f) {
                Vec3d lookDir = user.getRotationVec(1);
                Vec3d reach = lookDir.multiply(32);
                HitResult result = ProjectileUtil.raycast(user,
                        user.getEyePos(), user.getEyePos().add(reach),
                        user.getBoundingBox().stretch(reach).expand(1, 1, 1),
                        Predicates.alwaysTrue(),
                        4);
                if (result instanceof EntityHitResult entityHitResult) {
                    Entity entity = entityHitResult.getEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        entity.damage(new DamageSource(world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(DamageTypes.GENERIC), user, user), DAMAGE_AMOUNT);
                        removePlayerUUID(stack);
                        setEntityType(stack, entity.getType());
                        livingEntity.addStatusEffect(WEAKNESS_EFFECT, user);
                    }
                } else {
                    user.damage(new DamageSource(world.getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .entryOf(DamageTypes.GENERIC), user, user), DAMAGE_AMOUNT);
                    setPlayerUUID(stack, user.getUuid());
                    setEntityType(stack, EntityType.PLAYER);
                    user.addStatusEffect(WEAKNESS_EFFECT, user);
                }
            }
        }
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

    public static int getBloodColorOfEntityType(EntityType<?> type) {
        if (type != null) {
            if (type == EntityType.PLAYER) return 0xFF0000;
            if (type == EntityType.ENDERMAN || type == EntityType.ENDERMITE || type == EntityType.SHULKER) return 0xaa4cf7;
            if (type.isIn(EntityTypeTags.ZOMBIES)) return 0x108d26;
            if (type == EntityType.CREEPER) return 0x58f272;
        }
        return 0xa30c0c;
    }

    public static void emptyContents(ItemStack stack) {
        removePlayerUUID(stack);
        removeEntityType(stack);
    }
}
