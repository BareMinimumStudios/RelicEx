package com.github.clevernucleus.relicex.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.clevernucleus.dataattributes_dc.api.attribute.IEntityAttribute;
import com.github.clevernucleus.relicex.RelicEx;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class RarityManager {
	private final Map<Identifier, WeightProperty> cachedWeightMap;
	
	public RarityManager() {
		this.cachedWeightMap = new HashMap<Identifier, WeightProperty>();
	}
	
	public void onPropertiesLoaded() {
		this.cachedWeightMap.clear();
		
		for(Identifier identifier : Registries.ATTRIBUTE.getIds()) {
			EntityAttribute attributeIn = Registries.ATTRIBUTE.get(identifier);
			
			if(attributeIn == null) continue;
			IEntityAttribute attribute = (IEntityAttribute)attributeIn;
			String weight = attribute.getProperty(RelicEx.WEIGHT_PROPERTY);
			
			if(weight.equals("")) continue;
			String[] strings = weight.split(":");
			
			if(strings.length != 8) continue;
			WeightProperty property = new WeightProperty(strings);
			this.cachedWeightMap.putIfAbsent(identifier, property);
		}
	}
	
	public Collection<Identifier> keys() {
		return this.cachedWeightMap.keySet();	
	}
	
	public WeightProperty weight(final Identifier identifier) {
		return this.cachedWeightMap.getOrDefault(identifier, (WeightProperty)null);
	}
}
