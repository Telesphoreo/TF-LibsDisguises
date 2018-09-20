package me.libraryaddict.disguise.utilities.parser.params;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.libraryaddict.disguise.disguisetypes.AnimalColor;
import me.libraryaddict.disguise.disguisetypes.RabbitType;
import me.libraryaddict.disguise.utilities.parser.params.types.ParamInfoEnum;
import me.libraryaddict.disguise.utilities.parser.params.types.base.*;
import me.libraryaddict.disguise.utilities.parser.params.types.custom.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by libraryaddict on 7/09/2018.
 */
public class ParamInfoTypes {
    /**
     * Constructor values are listed here for continuity
     */
    public List<ParamInfo> getParamInfos() {
        List<ParamInfo> paramInfos = new ArrayList<>();

        // Register enum types
        paramInfos.add(new ParamInfoEnum(AnimalColor.class, "Animal Color",
                "View all the colors you can use for an animal color"));
        paramInfos
                .add(new ParamInfoEnum(Art.class, "Art", "View all the paintings you can use for a painting disguise"));
        paramInfos.add(new ParamInfoEnum(Horse.Color.class, "Horse Color",
                "View all the colors you can use for a horses color"));

        paramInfos.add(new ParamInfoEnum(Ocelot.Type.class, "Ocelot Type",
                "View all the ocelot types you can use for ocelots"));
        paramInfos.add(new ParamInfoEnum(Villager.Profession.class, "Villager Profession",
                "View all the professions you can set on a Zombie and Normal Villager"));
        paramInfos.add(new ParamInfoEnum(BlockFace.class, "Direction", "Direction (North, East, South, West, Up, Down)",
                "View the directions usable on player setSleeping and shulker direction",
                Arrays.copyOf(BlockFace.values(), 6)));
        paramInfos
                .add(new ParamInfoEnum(RabbitType.class, "Rabbit Type", "View the kinds of rabbits you can turn into"));
        paramInfos
                .add(new ParamInfoEnum(TreeSpecies.class, "Tree Species", "View the different types of tree species"));

        paramInfos.add(new ParamInfoEnum(MainHand.class, "Main Hand", "Set the main hand for an entity"));
        paramInfos.add(new ParamInfoEnum(Llama.Color.class, "Llama Color",
                "View all the colors you can use for a llama color"));
        paramInfos.add(new ParamInfoEnum(Parrot.Variant.class, "Parrot Variant",
                "View the different colors a parrot can be"));
        paramInfos.add(new ParamInfoEnum(Particle.class, "Particle", "The different particles of Minecraft"));
        paramInfos.add(new ParamInfoEnum(TropicalFish.Pattern.class, "Pattern", "Patterns of a tropical fish"));
        paramInfos.add(new ParamInfoEnum(DyeColor.class, "DyeColor", "Dye colors of many different colors"));
        paramInfos.add(new ParamInfoEnum(Horse.Style.class, "Horse Style",
                "Horse style which is the patterns on the horse"));

        // Register custom types
        paramInfos.add(new ParamInfoEulerAngle(EulerAngle.class, "Euler Angle", "Euler Angle (X,Y,Z)",
                "Set the X,Y,Z directions on an armorstand"));
        paramInfos.add(new ParamInfoColor(Color.class, "Color", "Colors that can also be defined through RGB",
                getColors()));
        paramInfos.add(new ParamInfoEnum(Material.class, "Material", "A material used for blocks and items",
                getMaterials()));
        paramInfos.add(new ParamInfoItemStack(ItemStack.class, "ItemStack", "ItemStack (Material:Amount?:Glow?)",
                "An ItemStack compromised of Material:Amount:Glow, only requires Material", getMaterials()));
        paramInfos.add(new ParamInfoItemStackArray(ItemStack[].class, "ItemStack[]",
                "Four ItemStacks (Material:Amount?:Glow?,Material:Amount?:Glow?..)",
                "Four ItemStacks separated by a comma", getMaterials()));
        paramInfos.add(new ParamInfoEnum(PotionEffectType.class, "Potion Effect",
                "View all the potion effects you can add", getPotions()));

        paramInfos.add(new ParamInfoBlockPosition(BlockPosition.class, "Block Position", "Block Position (num,num,num)",
                "Three numbers separated by a ,"));
        paramInfos.add(new ParamInfoGameProfile(WrappedGameProfile.class, "GameProfile",
                "Get the gameprofile here https://sessionserver.mojang" +
                        ".com/session/minecraft/profile/PLAYER_UUID_GOES_HERE?unsigned=false"));

        // Register base types
        Map<String, Object> booleanMap = new HashMap<>();
        booleanMap.put("true", true);
        booleanMap.put("false", false);

        paramInfos.add(new ParamInfoBoolean("Boolean", "True/False", "True or False", booleanMap));
        paramInfos.add(new ParamInfoString(String.class, "Text", "A line of text"));
        paramInfos.add(new ParamInfoInteger("Number", "A whole number without decimals"));
        paramInfos.add(new ParamInfoFloat("Number.0", "A number which can have decimal places"));
        paramInfos.add(new ParamInfoDouble("Number.0", "A number which can have decimal places"));

        return paramInfos;
    }

    private Map<String, Object> getColors() {
        try {
            Map<String, Object> map = new HashMap<>();
            Class cl = Class.forName("org.bukkit.Color");

            for (Field field : cl.getFields()) {
                if (field.getType() != cl) {
                    continue;
                }

                map.put(field.getName(), field.get(null));
            }

            return map;
        }
        catch (ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Material[] getMaterials() {
        List<Material> list = new ArrayList<>();

        for (Material material : Material.values()) {
            try {
                Field field = Material.class.getField(material.name());

                // Ignore all legacies materials
                if (field.isAnnotationPresent(Deprecated.class)) {
                    continue;
                }

                list.add(material);
            }
            catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        return list.toArray(new Material[0]);
    }

    private Map<String, Object> getPotions() {
        Map<String, Object> map = new HashMap<>();

        for (PotionEffectType effectType : PotionEffectType.values()) {
            if (effectType == null)
                continue;

            map.put(toReadable(effectType.getName()), effectType);
        }

        return map;
    }

    private String toReadable(String string) {
        String[] split = string.split("_");

        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].substring(0, 1) + split[i].substring(1).toLowerCase();
        }

        return StringUtils.join(split, "_");
    }
}
