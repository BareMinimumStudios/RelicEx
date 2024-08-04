package com.github.clevernucleus.relicex.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.github.clevernucleus.relicex.item.ArmorRelicItem;

import net.minecraft.item.Item;
import net.minecraft.recipe.RepairItemRecipe;

@Mixin(RepairItemRecipe.class)
abstract class RepairItemRecipeMixin {

	@WrapOperation(method = "matches(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/world/World;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isDamageable()Z"))
	private boolean relicex$matches(Item item, Operation<Boolean> original) {
		return !(item instanceof ArmorRelicItem) && original.call(item);
	}
}
