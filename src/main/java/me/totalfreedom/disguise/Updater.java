package me.totalfreedom.disguise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import me.libraryaddict.disguise.LibsDisguises;
import org.bukkit.plugin.java.JavaPlugin;
import static me.libraryaddict.disguise.utilities.DisguiseUtilities.getLogger;

public class Updater
{
    private LibsDisguises plugin;
    private String path = this.getFilePath();

    public void update(LibsDisguises libsDisguises)
    {
        plugin = libsDisguises;
        String currentVersion = plugin.getBuildNo();
        try
        {
            String versionLink = "https://www.telesphoreo.me/libsdisguises/version.txt";
            URL url = new URL(versionLink);
            URLConnection urlConnection = url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            if (!bufferedReader.ready())
            {
                return;
            }

            String newVersion = bufferedReader.readLine();
            bufferedReader.close();

            if (newVersion.equals(currentVersion))
            {
                getLogger().info("There are no updates available for LibsDisguises.");
                return;
            }

            String dlLink = "https://telesphoreo.me/libsdisguises/LibsDisguises.jar";
            url = new URL(dlLink);
            urlConnection = url.openConnection();
            InputStream in = urlConnection.getInputStream();
            FileOutputStream out = new FileOutputStream(path);
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, size);
            }

            out.close();
            in.close();
            getLogger().info("An update has been successfully been applied to LibsDisguises.");
        }
        catch (IOException ex)
        {
            getLogger().info("There was an issue fetching the server for an update.");
        }
    }

    private String getFilePath()
    {
        if (plugin != null)
        {
            try
            {
                Method method = JavaPlugin.class.getDeclaredMethod("getFile");
                boolean wasAccessible = method.isAccessible();
                method.setAccessible(true);
                File file = (File)method.invoke(plugin);
                method.setAccessible(wasAccessible);

                return file.getPath();
            }
            catch (Exception e)
            {
                return "plugins" + File.separator + plugin.getName();
            }
        }
        else
        {
            return "plugins" + File.separator + "LibsDisguises.jar";
        }
    }
}