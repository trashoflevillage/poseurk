package io.github.trashoflevillage.poseurk.items.custom;

import io.github.trashoflevillage.poseurk.items.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SyringeItem extends Item {
    private EntityType entityType = null;
    private UUID playerUUID = null;

    public SyringeItem(Settings settings) {
        super(settings);
    }

    public boolean hasBlood() {
        return entityType != null;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
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
        if (hasBlood()) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            float f = getPullProgress(i);
            if (f == 1.0f) {
                if (true) { // if there is no targetted entity
                    user.damage(new DamageSource(world.getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .entryOf(DamageTypes.GENERIC), user, user), 4);
                    setPlayerUUID(user.getUuid());
                    setEntityType(EntityType.PLAYER);
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (hasBlood()) {
            Text text;
            if (getEntityType() != EntityType.PLAYER) {
                text = getEntityType().getName().getWithStyle(Style.EMPTY.withColor(getBloodColorOfEntityType(getEntityType()))).getFirst();
            } else {
                //text = getPlayerUUID().getWithStyle(Style.EMPTY.withColor(getBloodColorOfEntityType(getEntityType()))).getFirst();
                text = getEntityType().getName().getWithStyle(Style.EMPTY.withColor(getBloodColorOfEntityType(getEntityType()))).getFirst();
            }
            tooltip.add(text);
        }
    }

    public int getBloodColorOfEntityType(EntityType type) {
        switch(type) {
            default: return 0xFF0000;
        }
    }
}
