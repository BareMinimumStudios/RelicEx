package com.github.clevernucleus.relicex.mixin;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.data_attributes.api.util.RandDistribution;
import com.github.clevernucleus.relicex.RelicEx;
import com.github.clevernucleus.relicex.config.RelicExConfig;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "dropLoot", at = @At("TAIL"))
	private void relicex$dropLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
		RelicExConfig config = RelicEx.config();
		
		if(!(this instanceof Monster) || config.mobDropBlacklist.contains(EntityType.getId(this.getType()).toString())) return;

		Random random = new Random();
		
		if(config.dropsOnlyFromPlayerKills && !causedByPlayer) return;
		double chance = 0.01 * config.mobsDropLootChance;
		double roll = (source.getAttacker() instanceof LivingEntity) ? DataAttributesAPI.getValue(EntityAttributes.GENERIC_LUCK, (LivingEntity) source.getAttacker())
			.map(value -> chance * (1.0 + value))
			.orElse(chance) : chance;
		
		if(!(random.nextFloat() < roll)) return;

		RandDistribution<Item> distributor = new RandDistribution<>(Items.AIR);
		distributor.add(RelicEx.RELICS.get(random.nextInt(RelicEx.RELICS.size())), 0.01F * (float)config.mobDropIsRelicChance);
		distributor.add(RelicEx.LESSER_ORB_OF_REGRET, 0.01F * (float)config.mobDropIsLesserOrbChance);
		distributor.add(RelicEx.GREATER_ORB_OF_REGRET, 0.01F * (float)config.mobDropIsGreaterOrbChance);
		distributor.add(RelicEx.TOME, 0.01F * (float)config.mobDropIsTomeChance);
		distributor.add(RelicEx.POTIONS.get(random.nextInt(RelicEx.POTIONS.size())), 0.01F * (float)config.mobDropIsPotionChance);
		
		Item item = distributor.getDistributedRandom();
		this.dropStack(new ItemStack(item, 1));
	}
}
