package io.github.trashoflevillage.poseurk;

import io.github.trashoflevillage.poseurk.blocks.ModBlocks;
import io.github.trashoflevillage.poseurk.blocks.entities.ModBlockEntities;
import io.github.trashoflevillage.poseurk.items.ModComponents;
import io.github.trashoflevillage.poseurk.items.ModItemGroups;
import io.github.trashoflevillage.poseurk.items.ModItems;
import io.github.trashoflevillage.poseurk.recipes.ModSpecialRecipes;
import io.github.trashoflevillage.poseurk.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Poseurk implements ModInitializer {
	public static final String MOD_ID = "poseurk";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModScreenHandlers.registerScreenHandlers();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
		ModSpecialRecipes.registerSpecialRecipes();
		ModComponents.registerComponents();
	}
}