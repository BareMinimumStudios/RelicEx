package com.github.clevernucleus.relicex.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.relicex.item.RelicItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(value = ItemStack.class, priority = 1001)
abstract class ItemStackMixin {

	@Shadow public abstract Item getItem();

	@Inject(method = "getTooltip", at = @At("RETURN"))
	private void relicex$getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci) {
		List<Text> tooltip = ci.getReturnValue();
		Item item = this.getItem();
		
		if(item instanceof RelicItem relicItem) {
            relicItem.amendTooltip(tooltip);
		}
	}
	
//	// We inject into Trinket's tooltip method because we want to keep attribute value formatting consistent with PlayerEx.
//	@Inject(method = "addAttributes", at = @At("HEAD"), cancellable = true)
//	private void relicex_addAttributes(List<Text> list, Multimap<EntityAttribute, EntityAttributeModifier> map, CallbackInfo ci) {
//		if(!map.isEmpty()) {
//			for(Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entries()) {
//				EntityAttribute attribute = entry.getKey();
//				EntityAttributeModifier modifier = entry.getValue();
//				String p = "";
//				double g = modifier.getValue();
//
//                if (modifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || modifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
//                    g *= 100.0D;
//                }
//
//                Text text = Text.translatable(attribute.getTranslationKey());
//
//				if(attribute instanceof SlotAttributes.SlotEntityAttribute) {
//					text = Text.translatable("trinkets.tooltip.attributes.slots", text);
//				}
//
//				if(g > 0.0D) {
//					list.add(Text.translatable("attribute.modifier.plus." + modifier.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(g) + p, text).formatted(Formatting.BLUE));
//				} else if(g < 0.0D) {
//					g *= -1.0D;
//					list.add(Text.translatable("attribute.modifier.take." + modifier.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(g) + p, text).formatted(Formatting.RED));
//				}
//			}
//		}
//
//		ci.cancel();
//	}
}
