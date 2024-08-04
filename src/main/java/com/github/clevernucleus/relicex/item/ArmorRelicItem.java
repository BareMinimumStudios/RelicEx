package com.github.clevernucleus.relicex.item;

import java.util.List;

import com.bibireden.data_attributes.api.item.ItemHelper;
import com.github.clevernucleus.relicex.RelicEx;
import com.github.clevernucleus.relicex.impl.EntityAttributeCollection;
import com.github.clevernucleus.relicex.impl.Rareness;
import com.github.clevernucleus.relicex.impl.RelicType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ArmorRelicItem extends ArmorItem implements ItemHelper {
	public ArmorRelicItem(RelicType type) {
		super(ArmorMaterials.CHAIN, type.getType(), (new FabricItemSettings()).maxCount(1));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> content.add(this));
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		NbtCompound tag = stack.getNbt();
		
		if(tag == null || !tag.contains(EntityAttributeCollection.KEY_RARENESS, NbtElement.STRING_TYPE)) return;
		Rareness rareness = Rareness.fromKey(tag.getString(EntityAttributeCollection.KEY_RARENESS));
		tooltip.add(rareness.formatted());
	}
	
	@Override
	public void onStackCreated(ItemStack itemStack, int count) {
		NbtCompound tag = itemStack.getOrCreateNbt();
		EntityAttributeCollection collection = new EntityAttributeCollection();
		collection.writeToNbt(tag);
	}
	
	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		NbtCompound tag = stack.getOrCreateNbt();
		Multimap<EntityAttribute, EntityAttributeModifier> modifiers = ArrayListMultimap.create();
		EntityAttributeCollection.readFromNbt(tag, this.getSlotType().getName(), modifiers, ArrayListMultimap.create());
		
		return slot == this.getSlotType() ? modifiers : super.getAttributeModifiers(stack, slot);
	}
	
	@Override
	public int getEnchantability() {
		return ArmorMaterials.GOLD.getEnchantability();
	}
	
	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.isOf(RelicEx.RELIC_SHARD);
	}
	
	@Override
	public Integer getProtection(ItemStack itemStack) {
		return (int)EntityAttributeCollection.getValueIfArmor(itemStack.getOrCreateNbt(), EntityAttributes.GENERIC_ARMOR, 0.0F);
	}
	
	@Override
	public Float getToughness(ItemStack itemStack) {
		return EntityAttributeCollection.getValueIfArmor(itemStack.getOrCreateNbt(), EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0.0F);
	}
	
	@Override
	public SoundEvent getEquipSound(ItemStack itemStack) {
		NbtCompound tag = itemStack.getNbt();
		
		if(tag != null && tag.contains(EntityAttributeCollection.KEY_RARENESS, NbtElement.STRING_TYPE)) {
			Rareness rareness = Rareness.fromKey(tag.getString(EntityAttributeCollection.KEY_RARENESS));
			return rareness.equipSound();
		}
		
		return Rareness.COMMON.equipSound();
	}
}
