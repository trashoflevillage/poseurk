package io.github.trashoflevillage.poseurk.blocks.entities.custom;

import io.github.trashoflevillage.poseurk.blocks.entities.ModBlockEntities;
import io.github.trashoflevillage.poseurk.items.ModItems;
import io.github.trashoflevillage.poseurk.items.custom.BloodVialItem;
import io.github.trashoflevillage.poseurk.items.custom.DNAVialItem;
import io.github.trashoflevillage.poseurk.screen.CentrifugeScreen;
import io.github.trashoflevillage.poseurk.screen.CentrifugeScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CentrifugeBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final int SLOTS = 4;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 600;

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CENTRIFUGE_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CentrifugeBlockEntity.this.progress;
                    case 1 -> CentrifugeBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CentrifugeBlockEntity.this.progress = value;
                    case 1 -> CentrifugeBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Object getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return new CentrifugeData(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.poseurk.centrifuge");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CentrifugeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("centrifuge.progress", progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("centrifuge.progress");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }

        if (getStack(CentrifugeScreenHandler.WIND_CHARGE_INPUT_SLOT).isOf(Items.WIND_CHARGE)) {
            if (this.hasRecipes()) {
                this.increaseCraftProgress();
                markDirty(world, pos, state);

                if (hasCraftingFinished()) {
                    this.craftItem();
                    this.resetProgress();
                    world.playSound(null, pos, SoundEvents.ENTITY_BREEZE_SHOOT, SoundCategory.BLOCKS, 0.8f, 1.2f);
                } else {
                    if (world.getTime() % 60 == 0)
                        world.playSound(null, pos, SoundEvents.ENTITY_BREEZE_IDLE_GROUND, SoundCategory.BLOCKS, 0.8f, 1.2f);
                }
            } else {
                this.resetProgress();
                markDirty(world, pos, state);
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        ItemStack newFuelStack = getStack(CentrifugeScreenHandler.WIND_CHARGE_INPUT_SLOT);
        newFuelStack.setCount(getStack(CentrifugeScreenHandler.WIND_CHARGE_INPUT_SLOT).getCount() - 1);
        setStack(CentrifugeScreenHandler.WIND_CHARGE_INPUT_SLOT, newFuelStack);
        for (int i = CentrifugeScreenHandler.FIRST_BLOOD_INPUT_SLOT; i < SLOTS; i++) {
            if (getStack(i).isOf(ModItems.BLOOD_VIAL)) {
                ItemStack bloodStack = getStack(i);
                setStack(i,
                        DNAVialItem.setPlayerUUID(
                                DNAVialItem.setEntityType(new ItemStack(ModItems.DNA_VIAL), BloodVialItem.getEntityType(bloodStack).get()),
                                BloodVialItem.getPlayerUUID(bloodStack)));
            }
        }
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipes() {
        for (int i = 0; i < SLOTS; i++) {
            if (getStack(i).isOf(ModItems.BLOOD_VIAL)) return true;
        }
        return false;
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        if (slot >= CentrifugeScreenHandler.FIRST_BLOOD_INPUT_SLOT)
            return stack.isOf(ModItems.DNA_VIAL);
        return false;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        if (side == Direction.UP && slot == CentrifugeScreenHandler.WIND_CHARGE_INPUT_SLOT) return getStack(slot).getCount() < getMaxCountPerStack();
        if (side != Direction.UP && slot >= CentrifugeScreenHandler.FIRST_BLOOD_INPUT_SLOT) return getStack(slot).getCount() < 1;
        return false;
    }
}
