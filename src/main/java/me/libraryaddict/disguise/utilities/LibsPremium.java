package me.libraryaddict.disguise.utilities;

import me.libraryaddict.disguise.utilities.reflection.ReflectionManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by libraryaddict on 2/06/2017.
 */
public class LibsPremium {
    private static class PluginInformation {
        private String userID;
        private boolean premium;
        private String version;
        private String buildNumber;
        private String buildDate;

        public PluginInformation(String userID, boolean premium, String version, String buildNumber, String buildDate) {
            this.userID = userID;
            this.premium = premium;
            this.version = version;
            this.buildNumber = buildNumber;
            this.buildDate = buildDate;
        }

        public String getUserID() {
            return userID;
        }

        public boolean isPremium() {
            return premium;
        }

        public String getVersion() {
            return version;
        }

        public String getBuildNumber() {
            return buildNumber;
        }

        public String getBuildDate() {
            return buildDate;
        }

        public Date getParsedBuildDate() {
            try {
                return new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(getBuildDate());
            }
            catch (ParseException e) {
                return null;
            }
        }
    }

    private static Boolean thisPluginIsPaidFor;

    /**
     * @return Account ID if downloaded through SpigotMC
     */
    public static String getUserID() {
        return "%%__USER__%%";
    }

    /**
     * @return Resource ID if downloaded through SpigotMC
     */
    public static String getResourceID() {
        return "%%__RESOURCE__%%";
    }

    /**
     * @return Download ID if downloaded through SpigotMC
     */
    public static String getDownloadID() {
        return "%%__NONCE__%%";
    }

    /**
     * @param userID
     * @return true if userID does not contain __USER__
     */
    private static Boolean isPremium(String userID) {
        return !userID.contains("__USER__");
    }

    public static Boolean isPremium() {
        return thisPluginIsPaidFor == null ? !getUserID().contains("__USER__") : thisPluginIsPaidFor;
    }

    private static PluginInformation getInformation(File file) throws Exception {
        try (URLClassLoader cl = new URLClassLoader(new URL[]{file.toURI().toURL()})) {
            Class c = cl.loadClass(LibsPremium.class.getName());

            boolean oldJarFile = true;

            try {
                // Error thrown if method doesn't exist
                c.getMethod("getUserID");
                // Method exists, is not older file
                oldJarFile = false;
            }
            catch (Exception ignored) {
            }

            // Fetch the plugin.yml from the jar file
            YamlConfiguration config = ReflectionManager.getPluginYaml(cl);
            // No checks for null config as the correct error will be thrown on access

            Boolean premium;
            String userId = null;

            if (oldJarFile) {
                premium = (Boolean) c.getMethod("isPremium").invoke(null);
            } else {
                userId = (String) c.getMethod("getUserID").invoke(null);
                premium = isPremium(userId);
            }

            String pluginBuildDate = "??/??/????";

            // If plugin.yml contains a build-date
            if (config.contains("build-date")) {
                pluginBuildDate = config.getString("build-date");
            }

            String pluginBuildNumber = "???";

            // If plugin.yml contains a jenkins build number
            if (config.contains("build-number")) {
                pluginBuildNumber = config.getString("build-number");

                // If build number is composed of purely numbers, prepend with # for readability
                if (pluginBuildNumber.matches("[0-9]+")) {
                    pluginBuildNumber = "#" + pluginBuildNumber;
                }
            }

            String pluginVersion = config.getString("version");

            return new PluginInformation(userId, premium, pluginVersion, pluginBuildNumber, pluginBuildDate);
        }
    }

    private static void doSecondaryCheck() {
        File[] files = new File("plugins/LibsDisguises/").listFiles();

        if (files == null)
            return;

        for (File file : files) {
            if (!file.isFile())
                continue;

            if (!file.getName().endsWith(".jar"))
                continue;

            PluginInformation plugin;

            try {
                plugin = getInformation(file);
            }
            catch (ClassNotFoundException ex) {
                DisguiseUtilities.getLogger()
                        .warning("Found an unrecognized jar in the LibsDisguises folder (" + file.getName() + ")");
                continue;
            }
            catch (Exception ex) {
                DisguiseUtilities.getLogger().warning("Error while trying to handle the file " + file.getName());
                ex.printStackTrace();
                continue;
            }

            // Format into a string
            // v5.2.6, build #40, created 16/02/2019
            String fileInfo = String.format("v%s, build %s, created %s", plugin.getVersion(), plugin.getBuildNumber(),
                    plugin.getBuildDate());

            if (plugin.isPremium()) {
                thisPluginIsPaidFor = true;
                // Found a premium Lib's Disguises jar (v5.2.6, build #40, created 16/02/2019)
                DisguiseUtilities.getLogger()
                        .info("Found a premium Lib's Disguises jar (" + fileInfo + ")");

                break;
            } else {
                // You have a non-premium Lib's Disguises jar (LibsDisguises.jar v5.2.6, build #40, created
                // 16/02/2019) in the LibsDisguises folder!
                DisguiseUtilities.getLogger().warning(
                        "You have a non-premium Lib's Disguises jar (" + file.getName() + " " + fileInfo +
                                ") in the LibsDisguises folder!");
            }
        }
    }

    public static void check(String version) {
        thisPluginIsPaidFor = isPremium();

        if (!isPremium()) {
            doSecondaryCheck();
        }

        if (isPremium()) {
            DisguiseUtilities.getLogger().info("Premium enabled, thank you for supporting Lib's Disguises!");
        }
    }
}
