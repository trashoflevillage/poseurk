package io.github.trashoflevillage.poseurk.recipes;

import io.github.trashoflevillage.poseurk.items.ModItems;
import io.github.trashoflevillage.poseurk.items.custom.BloodVialItem;
import io.github.trashoflevillage.poseurk.items.custom.SyringeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class FillVialWithBloodRecipe extends SpecialCraftingRecipe {
    public FillVialWithBloodRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        ItemStack syringe = null;
        ItemStack vial = null;
        for (ItemStack i : input.getStacks()) {
            if (!i.isEmpty()) {
                if (i.isOf(ModItems.SYRINGE)) {
                    if (SyringeItem.hasBlood(i)) {
                        if (syringe == null) syringe = i;
                        else return false;
                    } else return false;
                } else if (i.isOf(ModItems.EMPTY_VIAL)) {
                    if (vial == null) vial = i;
                    else return false;
                } else return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack syringe = null;
        ItemStack vial = null;
        for (ItemStack i : input.getStacks()) {
            if (!i.isEmpty()) {
                if (i.isOf(ModItems.SYRINGE)) {
                    if (SyringeItem.hasBlood(i)) {
                        if (syringe == null) syringe = i;
                    }
                } else if (i.isOf(ModItems.EMPTY_VIAL)) {
                    if (vial == null) vial = i;
                }
            }
        }

        if (syringe != null && vial != null)
            return BloodVialItem.setEntityType(BloodVialItem.setPlayerUUID(ModItems.BLOOD_VIAL.getDefaultStack(), SyringeItem.getPlayerUUID(syringe)), SyringeItem.getEntityType(syringe).get());
        else return Items.AIR.getDefaultStack();
    }

    @Override
    public boolean fits(int width, int height) {
        return width > 1 || height > 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModSpecialRecipes.FILL_VIAL_WITH_BLOOD;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput input) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.getSize(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            Item item = input.getStackInSlot(i).getItem();
            if (item == ModItems.SYRINGE) {
                defaultedList.set(i, new ItemStack(ModItems.SYRINGE));
            } else if (item == ModItems.EMPTY_VIAL) {
                ItemStack itemStack = defaultedList.get(i);
                itemStack.setCount(defaultedList.get(i).getCount());
                defaultedList.set(i, itemStack);
            }
        }
        return defaultedList;
    }
}
