package com.github.clevernucleus.relicex.item;

import java.util.List;
import java.util.UUID;

import com.github.clevernucleus.dataattributes_dc.api.item.ItemHelper;
import com.github.clevernucleus.relicex.impl.EntityAttributeCollection;
import com.github.clevernucleus.relicex.impl.Rareness;
import com.github.clevernucleus.relicex.impl.RelicType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class RelicItem extends TrinketItem implements ItemHelper {
	private final RelicType type;
	
	public RelicItem(final RelicType type) {
		super((new FabricItemSettings()).maxCount(1));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> content.add(this));
		this.type = type;
	}
	
	public void amendTooltip(final List<Text> tooltip) {
		this.type.tooltip().accept(tooltip);
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
	public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
		SlotType slotType = slot.inventory().getSlotType();
		String key = slotType.getGroup() + "/" + slotType.getName();
		NbtCompound tag = stack.getOrCreateNbt();
		var modifiers = super.getModifiers(stack, slot, entity, uuid);
		EntityAttributeCollection.readFromNbt(tag, key, modifiers, ArrayListMultimap.create());
		
		return modifiers;
	}
	
	@Override
	public SoundEvent getEquipSound(ItemStack itemStack) {
		return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC; // todo: unsure about implementation here.
	}
}
