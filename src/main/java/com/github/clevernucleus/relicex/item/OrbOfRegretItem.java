package com.github.clevernucleus.relicex.item;

import java.util.List;

import com.github.clevernucleus.dataattributes_dc.api.DataAttributesAPI;
import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.api.PlayerData;
import com.github.clevernucleus.relicex.RelicEx;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class OrbOfRegretItem extends Item {
	private final boolean greater;
	
	public OrbOfRegretItem(final boolean greater) {
		super((new FabricItemSettings()).maxCount(1));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> content.add(this));
		this.greater = greater;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("tooltip.relicex." + (this.greater ? "greater" : "lesser") + "_orb_of_regret").formatted(Formatting.GRAY));
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return this.greater ? Rarity.EPIC : Rarity.RARE;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return DataAttributesAPI.ifPresent(user, ExAPI.LEVEL, super.use(world, user, hand), value -> {
			ItemStack itemStack = user.getStackInHand(hand);
			PlayerData playerData = ExAPI.PLAYER_DATA.get(user);
			int refundPoints = 0;
			
			for(var refundCondition : ExAPI.getRefundConditions()) {
				refundPoints += refundCondition.apply(playerData, user);
			}
			
			if(!(value > 0.0D) || !(refundPoints > 0)) return super.use(world, user, hand);
			if(world.isClient) {
				user.playSound(RelicEx.LEVEL_REFUND_SOUND, SoundCategory.NEUTRAL, ExAPI.getConfig().skillUpVolume(), 1.0F);
			} else {
				playerData.addRefundPoints(this.greater ? refundPoints : 1);
				
				if(!user.isCreative()) {
					itemStack.decrement(1);
				}
			}
			
			return TypedActionResult.success(itemStack, world.isClient);
		});
	}
}
