/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.rpc.gameSdk;

import de.jcm.discordgamesdk.Core;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.util.Logger;
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
 * This DiscordRPC module is derived from <a href="https://github.com/DeDiamondPro/HyCord">HyCord</a>.
 *
 * @author DeDiamondPro
 * @license GPL-3.0
 */

public class GameSdkDownloader {

	public static void downloadSdk(Logger logger, BooleanOption rpcEnabled) {
		File target = new File("config/game-sdk");
		logger.info("Downloading SDK!");
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
					throw new RuntimeException("Could not determine OS type: " + System.getProperty("os.name")
							+ " Detected " + OSUtil.getOS());
				}

				fileName = "discord_game_sdk.so";
			}

			File sdk = new File("config/game-sdk/" + fileName);
			File jni = new File("config/game-sdk/" + (OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS
					? "discord_game_sdk_jni.dll"
					: "libdiscord_game_sdk_jni" + (OSUtil.getOS() == OSUtil.OperatingSystem.MAC ? ".dylib" : ".so")));
			if (!sdk.exists()) {
				downloadSdk(sdk, fileName);
			}

			if (!jni.exists()) {
				extractJni(jni, logger, rpcEnabled);
			}

			if (!sdk.exists() || !jni.exists()) {
				logger.error("Could not download GameSDK, no copy is available. RPC will be disabled.");
				rpcEnabled.setForceOff(true, "crash");
				return;
			}

			loadNative(sdk, jni, logger, rpcEnabled);
		} catch (Exception e) {
			e.printStackTrace();
			rpcEnabled.set(false);
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
		while ((entry = zin.getNextEntry()) != null) {
			if (entry.getName().equals("lib/" + arch + "/" + fileName)) {
				Files.copy(zin, sdk.toPath(), StandardCopyOption.REPLACE_EXISTING);
				break;
			}

			zin.closeEntry();
		}

		zin.close();
	}

	private static boolean retriedExtractingJni = false;

	private static void extractJni(File jni, Logger logger, BooleanOption rpcEnabled) throws IOException {
		String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
		if (arch.equals("x86_64")) {
			arch = "amd64";
		}

		String path = "/native/"
				+ (OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS ? "windows"
				: (OSUtil.getOS() == OSUtil.OperatingSystem.MAC ? "macos" : "linux"))
				+ "/" + arch + "/"
				+ (OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS ? "discord_game_sdk_jni.dll"
				: "libdiscord_game_sdk_jni"
				+ (OSUtil.getOS() == OSUtil.OperatingSystem.MAC ? ".dylib" : ".so"));

		InputStream in = GameSdkDownloader.class.getResourceAsStream(path);
		if (in != null) {
			Files.copy(in, jni.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} else if (!retriedExtractingJni) {
			logger.warn("Extracting JNI failed, retrying!");
			retriedExtractingJni = true;
			extractJni(jni, logger, rpcEnabled);
		} else {
			retriedExtractingJni = false;
			logger.error("Extracting Jni failed, restart your game to try again.");
			rpcEnabled.setForceOff(true, "crash");
		}
	}

	private static void loadNative(File sdk, File jni, Logger logger, BooleanOption rpcEnabled) {
		logger.info("Loading GameSDK");

		try {
			if (OSUtil.getOS() == OSUtil.OperatingSystem.WINDOWS) {
				System.load(sdk.getAbsolutePath());
			}

			if (jni.getAbsolutePath().contains(" ")) {
				throw new IllegalStateException("JNI Path: <" + jni.getAbsolutePath() + "> contains spaces!");
			}

			System.load(jni.getAbsolutePath()); // it WILL crash if your path contains spaces. No, escaping them does not help.
			Core.initDiscordNative(sdk.getAbsolutePath());
		} catch (Throwable e) {
			logger.warn("Discord RPC failed to load: ");
			e.printStackTrace();
			rpcEnabled.set(false);
		}
	}
}
