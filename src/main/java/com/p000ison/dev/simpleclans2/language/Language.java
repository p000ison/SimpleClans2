package com.p000ison.dev.simpleclans2.language;

import com.p000ison.dev.simpleclans2.util.Logging;
import org.bukkit.ChatColor;

import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Level;

public class Language {
    private static LanguageMap defaultBundle;
    private static LanguageMap bundle;
    private static final String DEFAULT_FILE_NAME = "lang.properties";

    public static void setInstance(File folder, String language)
    {
        defaultBundle = new LanguageMap("/languages/lang.properties", true);
        defaultBundle.load();
        bundle = new LanguageMap(new File(folder, "lang.properties").getAbsolutePath(), false);
        bundle.setDefault(defaultBundle);
        bundle.load();
    }

    public static String getTranslation(String key, Object... args)
    {
        String bundleOutput = bundle.get(key);

        if (bundleOutput == null) {
            Logging.debug(ChatColor.RED + "The language for the key %s was not found!", Level.WARNING, key);

            if (defaultBundle != null) {
                String defaultBundleOutput = (String) defaultBundle.get(key);
                if (defaultBundleOutput == null) {
                    Logging.debug(ChatColor.RED + "The language for the key %s was not found in the default bundle!", Level.WARNING, key);
                    return "Error!";
                }

                if (args.length > 0) {
                    return MessageFormat.format(defaultBundleOutput, args);
                }
                return defaultBundleOutput;
            }

            return "Error!";
        }

        if (args.length > 0) {
            return MessageFormat.format(bundleOutput, args);
        }
        return bundleOutput;
    }

    public static void clear()
    {
        bundle.clear();
        if (defaultBundle != null) {
            defaultBundle.clear();
        }
        bundle = null;
        defaultBundle = null;
    }
}