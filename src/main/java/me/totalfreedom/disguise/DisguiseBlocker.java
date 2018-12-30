package me.totalfreedom.disguise;

import java.util.Arrays;
import java.util.List;

import me.libraryaddict.disguise.LibsDisguises;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import com.google.common.base.Function;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class DisguiseBlocker {
    private static Logger logger = LibsDisguises.getInstance().getLogger();
    private static Function<Player, Boolean> adminProvider;
    public static boolean enabled = true;
    private static final List<DisguiseType> forbiddenDisguises = Arrays.asList(
            DisguiseType.ITEM_FRAME,
            DisguiseType.ENDER_DRAGON,
            DisguiseType.PLAYER,
            DisguiseType.GIANT,
            DisguiseType.GHAST,
            DisguiseType.MAGMA_CUBE,
            DisguiseType.SLIME,
            DisguiseType.DROPPED_ITEM,
            DisguiseType.ENDER_CRYSTAL,
            DisguiseType.AREA_EFFECT_CLOUD,
            DisguiseType.WITHER,
            DisguiseType.PHANTOM
    );

    public static Plugin getTFM()
    {
        final Plugin tfm = Bukkit.getPluginManager().getPlugin("TotalFreedomMod");
        if (tfm == null)
        {
            logger.warning("Could not resolve plugin: TotalFreedomMod");
        }

        return tfm;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private static boolean isAdmin(Player player)
    {

        if (adminProvider == null)
        {
            final Plugin tfm = getTFM();
            if (tfm == null)
            {
                return false;
            }

            Object provider = null;
            for (RegisteredServiceProvider<?> serv : Bukkit.getServicesManager().getRegistrations(tfm))
            {
                if (Function.class.isAssignableFrom(serv.getService()))
                {
                    provider = serv.getProvider();
                }
            }

            if (provider == null)
            {
                logger.warning("Could not obtain admin service provider!");
                return false;
            }

            adminProvider = (Function<Player, Boolean>)provider;
        }

        return adminProvider.apply(player);
    }

    public static boolean isAllowed(Disguise disguise, Player player) {
        return isAllowed(disguise.getType(), player);
    }

    public static boolean isAllowed(DisguiseType type, Player player) {
        if (forbiddenDisguises.contains(type) && !isAdmin(player)) {
            return false;
        }
        return true;
    }
}
