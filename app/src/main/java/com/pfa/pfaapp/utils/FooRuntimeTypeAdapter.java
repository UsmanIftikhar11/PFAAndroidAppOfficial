package com.pfa.pfaapp.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class FooRuntimeTypeAdapter implements JsonDeserializer<FooRuntime>, JsonSerializer<FooRuntime>
{
    public FooRuntime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        int runtime;
        try
        {
            runtime = json.getAsInt();
        }
        catch (NumberFormatException e)
        {
            runtime = 0;
        }
        return new FooRuntime(runtime);
    }

    public JsonElement serialize(FooRuntime src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.getValue());
    }
}
