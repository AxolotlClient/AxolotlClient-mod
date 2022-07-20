package io.github.axolotlclient.modules.rpc.gameSdk;

import de.jcm.discordgamesdk.Core;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.rpc.DiscordRPC;
import io.github.axolotlclient.util.OSUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This DiscordRPC module is derived from https://github.com/DeDiamondPro/HyCord.
 * License: GPL-3.0
 * @author DeDiamondPro
 */

public class GameSdkDownloader {

    public static void downloadSdk() {
        File target = new File("config/game-sdk");
        AxolotlClient.LOGGER.info("Downloading SDK!");
        try {
            if (!target.exists() && !target.mkdir()) {
                throw new IllegalStateException("Could not create game-sdk folder");
            }

            String fileName;
            if (OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS) {
                fileName = "discord_game_sdk.dll";
            } else if (OSUtil.getOS() == OSUtil.OperatingSystem.MAC) {
                fileName = "discord_game_sdk.dylib";
            } else {
                if (OSUtil.getOS() != OSUtil.OperatingSystem.LINUX) {
                    throw new RuntimeException("Could not determine OS type: " + System.getProperty("os.name") + " Detected " + OSUtil.getOS());
                }

                fileName = "discord_game_sdk.so";
            }

            File sdk = new File("config/game-sdk/" + fileName);
            File jni = new File(
                    "config/game-sdk/"
                            + (
                            OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS
                                    ? "discord_game_sdk_jni.dll"
                                    : "libdiscord_game_sdk_jni" + (OSUtil.getOS() == OSUtil.OperatingSystem.MAC ? ".dylib" : ".so")
                    )
            );
            if (!sdk.exists()) {
                downloadSdk(sdk, fileName);
            }

            if (!jni.exists()) {
                extractJni(jni);
            }

            if (!sdk.exists() || !jni.exists()) {
                AxolotlClient.LOGGER.error("Could not download GameSDK, no copy is available. RPC will be disabled.");
                return;
            }

            loadNative(sdk, jni);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void downloadSdk(File sdk, String fileName) throws IOException {
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
        if (arch.equals("amd64")) {
            arch = "x86_64";
        }

        URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/3.2.1/discord_game_sdk.zip");
        URLConnection con = downloadUrl.openConnection();
        con.setRequestProperty("User-Agent", "AxolotlClient");
        ZipInputStream zin = new ZipInputStream(con.getInputStream());

        ZipEntry entry;
        while((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals("lib/" + arch + "/" + fileName)) {
                Files.copy(zin, sdk.toPath(), StandardCopyOption.REPLACE_EXISTING);
                break;
            }

            zin.closeEntry();
        }

        zin.close();
    }

    private static void extractJni(File jni) throws IOException {
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        if (arch.equals("x86_64")) {
            arch = "amd64";
        }

        String path = "/native/"
                + (OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS ?
                "windows" :
                (OSUtil.getOS() == OSUtil.OperatingSystem.MAC ? "macos" : "linux"))
                + "/" + arch + "/" +
                (OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS ?
                        "discord_game_sdk_jni.dll" :
                        "libdiscord_game_sdk_jni" + (OSUtil.getOS() == OSUtil.OperatingSystem.MAC ? ".dylib" : ".so")
        );

        InputStream in = DiscordRPC.class.getResourceAsStream(path);
        Files.copy(in, jni.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void loadNative(File sdk, File jni) {
        AxolotlClient.LOGGER.info("Loading GameSDK");

        if (OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS) {
            System.load(sdk.getAbsolutePath());
        }
        System.load(jni.getAbsolutePath());
        Core.initDiscordNative(sdk.getAbsolutePath());
    }
}
