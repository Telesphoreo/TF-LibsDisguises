package me.totalfreedom.libsdisguise;

public class TF_DisguiseAPI {
    public static void disableDisguises()
    {
        DisallowedDisguises.disabled = true;
    }

    public static void enableDisguises()
    {
        DisallowedDisguises.disabled = false;
    }
}
