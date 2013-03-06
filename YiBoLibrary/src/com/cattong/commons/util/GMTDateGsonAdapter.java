package com.cattong.commons.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GMTDateGsonAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private SimpleDateFormat dateFormat;

    public GMTDateGsonAdapter() {
    	this.dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
        this.dateFormat.setTimeZone(GMT);
	}

	@Override
	public Date deserialize(JsonElement json, final Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonNull()) {
            return null;
        }
        if (!json.isJsonPrimitive()) {
            throw new JsonParseException("it' not json primitive");
        }
        final JsonPrimitive primitive = (JsonPrimitive) json;
        if (!primitive.isString()) {
            throw new JsonParseException("Expected string for date type");
        }
        try {
        	synchronized (dateFormat) {
                return dateFormat.parse(primitive.getAsString());
			}
        } catch (ParseException e) {
            throw new JsonParseException("Not a date string");
        }
	}

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc,
			JsonSerializationContext context) {
		if (src == null) {
            return new JsonNull();
        }
		synchronized (dateFormat) {
			String dateString = dateFormat.format(src);
			return new JsonPrimitive(dateString);
		}
	}
}
