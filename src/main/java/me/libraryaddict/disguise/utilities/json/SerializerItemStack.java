package me.libraryaddict.disguise.utilities.json;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by libraryaddict on 1/06/2017.
 */
public class SerializerItemStack implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.serialize());
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {
        return ItemStack.deserialize((Map<String, Object>) context.deserialize(json, HashMap.class));
    }
}
