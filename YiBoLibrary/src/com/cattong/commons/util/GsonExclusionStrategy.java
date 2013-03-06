package com.cattong.commons.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {		
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes attributes) {
		return false;
	}

}
