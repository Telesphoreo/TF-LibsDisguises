package me.libraryaddict.disguise.utilities;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import me.libraryaddict.disguise.LibsDisguises;
import me.libraryaddict.disguise.utilities.mineskin.MineSkinResponse;
import me.libraryaddict.disguise.utilities.parser.DisguiseParseException;
import me.libraryaddict.disguise.utilities.reflection.LibsProfileLookup;
import me.libraryaddict.disguise.utilities.reflection.ReflectionManager;
import me.libraryaddict.disguise.utilities.translations.LibsMsg;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by libraryaddict on 1/01/2020.
 */
public class SkinUtils {
    public interface SkinCallback {
        void onError(LibsMsg msg, Object... args);

        void onInfo(LibsMsg msg, Object... args);

        void onSuccess(WrappedGameProfile profile);
    }

    public static void handleFile(File file, SkinCallback callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    MineSkinResponse response = DisguiseUtilities.getMineSkinAPI().generateFromFile(callback, file);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (response == null) {
                                return;
                            } else if (response.getGameProfile() == null) {
                                callback.onError(LibsMsg.SKIN_API_FAIL);
                                return;
                            }

                            handleProfile(response.getGameProfile(), callback);
                        }
                    }.runTask(LibsDisguises.getInstance());
                }
                catch (IllegalArgumentException e) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            callback.onError(LibsMsg.SKIN_API_BAD_FILE);
                        }
                    }.runTask(LibsDisguises.getInstance());
                }
            }
        }.runTaskAsynchronously(LibsDisguises.getInstance());
    }

    public static void handleUrl(String url, SkinCallback callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MineSkinResponse response = DisguiseUtilities.getMineSkinAPI().generateFromUrl(callback, url);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            return;
                        } else if (response.getGameProfile() == null) {
                            callback.onError(LibsMsg.SKIN_API_FAIL);
                        }

                        handleProfile(response.getGameProfile(), callback);
                    }
                }.runTask(LibsDisguises.getInstance());
            }
        }.runTaskAsynchronously(LibsDisguises.getInstance());
    }

    public static void handleName(String playerName, SkinCallback callback) {
        WrappedGameProfile gameProfile = DisguiseUtilities.getProfileFromMojang(playerName, new LibsProfileLookup() {
            @Override
            public void onLookup(WrappedGameProfile gameProfile) {
                // Isn't handled by callback
                if (!Pattern.matches("([A-Za-z0-9_]){1,16}", playerName)) {
                    return;
                }

                if (gameProfile == null || gameProfile.getProperties().isEmpty()) {
                    callback.onError(LibsMsg.CANNOT_FIND_PLAYER_NAME, playerName);
                    return;
                }

                handleProfile(gameProfile, callback);
            }
        });

        // Is handled in callback
        if (gameProfile == null) {
            return;
        }

        if (gameProfile.getProperties().isEmpty()) {
            callback.onError(LibsMsg.CANNOT_FIND_PLAYER_NAME, playerName);
            return;
        }

        handleProfile(gameProfile, callback);
    }

    public static void handleProfile(GameProfile profile, SkinCallback callback) {
        handleProfile(WrappedGameProfile.fromHandle(profile), callback);
    }

    public static void handleProfile(WrappedGameProfile profile, SkinCallback callback) {
        callback.onSuccess(profile);
    }

    public static void handleUUID(UUID uuid, SkinCallback callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                WrappedGameProfile profile = ReflectionManager
                        .getSkullBlob(new WrappedGameProfile(uuid, "AutoGenerated"));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (profile == null || profile.getProperties().isEmpty()) {
                            callback.onError(LibsMsg.CANNOT_FIND_PLAYER_UUID, uuid.toString());
                            return;
                        }

                        handleProfile(profile, callback);
                    }
                }.runTask(LibsDisguises.getInstance());
            }
        }.runTaskAsynchronously(LibsDisguises.getInstance());
    }

    public static boolean isUsable() {
        return getUsableStatus() == null;
    }

    public static String getUsableStatus() {
        if (DisguiseUtilities.getMineSkinAPI().isInUse()) {
            return LibsMsg.SKIN_API_IN_USE.get();
        }

        if (DisguiseUtilities.getMineSkinAPI().nextRequestIn() > 0) {
            return LibsMsg.SKIN_API_TIMER.get(DisguiseUtilities.getMineSkinAPI().nextRequestIn());
        }

        return null;
    }

    public static void grabSkin(String param, SkinCallback callback) {
        if (param.matches("https?:\\/\\/.+")) {
            // Its an url
            callback.onInfo(LibsMsg.SKIN_API_USING_URL);
            handleUrl(param, callback);
        } else {
            // Check if it contains legal file characters
            if (!param.matches("[a-zA-Z0-9 -_]+(\\.png)?")) {
                callback.onError(LibsMsg.SKIN_API_INVALID_NAME);
                return;
            }

            File file = new File(LibsDisguises.getInstance().getDataFolder(),
                    "/Skins/" + param + (param.toLowerCase().endsWith(".png") ? "" : ".png"));

            if (!file.exists()) {
                file = null;

                if (param.toLowerCase().endsWith(".png")) {
                    callback.onError(LibsMsg.SKIN_API_BAD_FILE_NAME);
                    return;
                }
            }

            if (file != null) {
                callback.onInfo(LibsMsg.SKIN_API_USING_FILE);
                handleFile(file, callback);
                // We're using a file!
            } else {
                // We're using a player name or UUID!
                if (param.contains("-")) {
                    try {
                        UUID uuid = UUID.fromString(param);

                        callback.onInfo(LibsMsg.SKIN_API_USING_UUID);
                        handleUUID(uuid, callback);
                        return;
                    }
                    catch (Exception ignored) {
                    }
                }

                callback.onInfo(LibsMsg.SKIN_API_USING_NAME);
                handleName(param, callback);
            }
        }
    }
}
