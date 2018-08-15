package me.totalfreedom.libsdisguise;

import com.google.common.base.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Logger;

public class SuperAdminProvider
{
    public static final boolean DEBUG = true;
    private static Logger logger;
    private static Function<Player, Boolean> superAdminProvider;

    public static void setLogger(Logger logger)
    {
        SuperAdminProvider.logger = logger;
    }

    public static Plugin getTFM()
    {
        final Plugin tfm = Bukkit.getPluginManager().getPlugin("TotalFreedomMod");
        if (tfm == null)
        {
            logger.warning("Could not resolve plugin: TotalFreedomMod");
        }

        return tfm;
    }

    @SuppressWarnings("unchecked")
    public static boolean isSuperAdmin(Player player)
    {

        if (superAdminProvider == null)
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
                warning("Could not obtain Super Admin service provider!");
                return false;
            }

            superAdminProvider = (Function<Player, Boolean>) provider;
        }

        return superAdminProvider.apply(player);
    }

    public static void debug(String debug)
    {
        if (DEBUG)
        {
            info(debug);
        }
    }

    public static void warning(String warning)
    {
        logger.warning(warning);
    }

    public static void info(String info)
    {
        logger.info(info);
    }
}