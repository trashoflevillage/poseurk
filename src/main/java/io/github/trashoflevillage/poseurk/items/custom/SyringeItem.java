package io.github.trashoflevillage.poseurk.items.custom;

import com.google.common.base.Predicates;
import io.github.trashoflevillage.poseurk.items.ModComponents;
import io.github.trashoflevillage.poseurk.util.ModTags;
import io.github.trashoflevillage.poseurk.util.PoseurkUtil;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SyringeItem extends Item {
    public SyringeItem(Settings settings) {
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
        if (playerUUID == null) removePlayerUUID(itemStack);
        else itemStack.set(ModComponents.STORED_PLAYER_UUID, playerUUID);
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
                    String potentialUsername = PoseurkUtil.getUsernameFromUUID(uuid);
                    if (potentialUsername != null) {
                        text = Text.of(potentialUsername).getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                    } else {
                        ServerPlayerEntity playerEntity = MinecraftClient.getInstance().getServer().getPlayerManager().getPlayer(uuid);
                        if (playerEntity != null) {
                            text = playerEntity.getName()
                                    .getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                        } else {
                            text = getEntityType(stack).get().getName().getWithStyle(Style.EMPTY.withColor(Colors.LIGHT_GRAY)).getFirst();
                        }
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
                    if (entity instanceof MobEntity mobEntity && !entity.getType().isIn(ModTags.EntityTypes.HAS_NO_BLOOD)) {
                        entity.damage(new DamageSource(world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(DamageTypes.GENERIC), user, user), DAMAGE_AMOUNT);
                        removePlayerUUID(stack);
                        setEntityType(stack, entity.getType());
                        mobEntity.addStatusEffect(WEAKNESS_EFFECT, user);
                    } else {
                        if (entity instanceof PlayerEntity playerEntity && !entity.getType().isIn(ModTags.EntityTypes.HAS_NO_BLOOD)) {
                            entity.damage(new DamageSource(world.getRegistryManager()
                                    .get(RegistryKeys.DAMAGE_TYPE)
                                    .entryOf(DamageTypes.GENERIC), user, user), DAMAGE_AMOUNT);
                            removePlayerUUID(stack);
                            setEntityType(stack, entity.getType());
                            setPlayerUUID(stack, playerEntity.getUuid());
                            playerEntity.addStatusEffect(WEAKNESS_EFFECT, user);
                        } else ((PlayerEntity) user).sendMessage(Text.translatable("item.poseurk.syringe.entity_has_no_blood").withColor(Colors.LIGHT_RED), true);
                    }
                } else {
                    if (!user.getType().isIn(ModTags.EntityTypes.HAS_NO_BLOOD)) {
                        user.damage(new DamageSource(world.getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(DamageTypes.GENERIC), user, user), DAMAGE_AMOUNT);
                        setPlayerUUID(stack, user.getUuid());
                        setEntityType(stack, EntityType.PLAYER);
                        user.addStatusEffect(WEAKNESS_EFFECT, user);
                    } else ((PlayerEntity) user).sendMessage(Text.translatable("item.poseurk.syringe.entity_has_no_blood").withColor(Colors.LIGHT_RED), true);
                }
                user.playSound(SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 1.0f, 1.5f);
            }
        }
    }

    @Override
    public ItemStack getDefaultStack() {
        return emptyContents(super.getDefaultStack());
    }

    public static ItemStack emptyContents(ItemStack stack) {
        removePlayerUUID(stack);
        removeEntityType(stack);
        return stack;
    }
}
