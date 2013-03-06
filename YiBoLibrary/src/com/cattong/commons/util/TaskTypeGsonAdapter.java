package com.cattong.commons.util;

import java.lang.reflect.Type;

import com.cattong.entity.TaskType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TaskTypeGsonAdapter implements JsonDeserializer<TaskType>,
		JsonSerializer<TaskType> {

	@Override
	public TaskType deserialize(JsonElement json, final Type type,
			JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonNull()) {
            return null;
        }
        if (!json.isJsonPrimitive()) {
            throw new JsonParseException("it' not json primitive");
        }
        
        final JsonPrimitive primitive = (JsonPrimitive)json;
        int taskTypeNo = primitive.getAsInt();
        
		return TaskType.getTaskType(taskTypeNo);
	}

	@Override
	public JsonElement serialize(TaskType src, Type typeOfSrc,
			JsonSerializationContext context) {
		if (src == null) {
            return new JsonNull();
        }
		
		return new JsonPrimitive(src.getTaskTypeNo());
	}

}
