package me.totalfreedom.libsdisguise;

public class TF_DisguiseAPI
{
    public static boolean disabled = false;

    public static void disableDisguises()
    {
        disabled = true;
    }

    public static void enableDisguises()
    {
        disabled = false;
    }
}
