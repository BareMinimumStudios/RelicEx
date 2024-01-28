package com.github.clevernucleus.relicex.mixin;

import net.minecraft.screen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.relicex.impl.EntityAttributeCollection;
import com.github.clevernucleus.relicex.item.ArmorRelicItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

@Mixin(AnvilScreenHandler.class)
abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
	private AnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) { super(type, syncId, playerInventory, context); }
	
	@Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
	private void relicex_canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
		Item item = this.input.getStack(0).getItem();
		Item item2 = this.input.getStack(1).getItem();
		
		if(item instanceof ArmorItem && !(item instanceof ArmorRelicItem) && item2 instanceof ArmorRelicItem) {
			cir.setReturnValue(true);
		}
	}
	
	@Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
	private void relicex_updateResult(CallbackInfo ci) {
		ItemStack itemStack = this.input.getStack(0);
		ItemStack itemStack2 = this.input.getStack(1);
		Item item = itemStack.getItem();
		Item item2 = itemStack2.getItem();
		
		if(item instanceof ArmorItem && !(item instanceof ArmorRelicItem) && item2 instanceof ArmorRelicItem) {
			if(((ArmorItem)item).getSlotType() == ((ArmorItem)item2).getSlotType() && !(itemStack.hasNbt() && itemStack.getNbt().contains(EntityAttributeCollection.KEY_ATTRIBUTES))) {
				ItemStack itemStack3 = itemStack.copy();
				NbtCompound tag = itemStack2.getNbt();
				NbtCompound tag2 = itemStack3.getOrCreateNbt();
        		NbtList list = tag.getList(EntityAttributeCollection.KEY_ATTRIBUTES, NbtElement.COMPOUND_TYPE);
        		String rareness = tag.getString(EntityAttributeCollection.KEY_RARENESS);
        		tag2.put(EntityAttributeCollection.KEY_ATTRIBUTES, list);
        		tag2.putString(EntityAttributeCollection.KEY_RARENESS, rareness);
        		this.output.setStack(0, itemStack3);
        		ci.cancel();
			}
		}
	}
}
