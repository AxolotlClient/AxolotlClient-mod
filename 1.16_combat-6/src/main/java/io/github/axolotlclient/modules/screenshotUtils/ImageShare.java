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

package io.github.axolotlclient.modules.screenshotUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.util.NetworkHelper;
import io.github.axolotlclient.util.Util;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ImageShare {

	@Getter
	private static final ImageShare Instance = new ImageShare();
	private final String separator = ";";//"ⓢ¢€ⓢ¢";

	private ImageShare() {
	}

	public void uploadImage(String url, File file) {
		Util.sendChatMessage(new TranslatableText("imageUploadStarted"));
		String downloadUrl = upload(url + "/api/stream", file);

		if (downloadUrl.isEmpty()) {
			Util.sendChatMessage(new TranslatableText("imageUploadFailure"));
		} else {
			Util.sendChatMessage(new TranslatableText("imageUploadSuccess").append(" ")
				.append(new LiteralText(url + "/" + downloadUrl)
					.setStyle(Style.EMPTY
						.withFormatting(Formatting.UNDERLINE, Formatting.DARK_PURPLE)
						.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, url + "/" + downloadUrl))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("clickToCopy"))))));
		}
	}

	public String upload(String url, File file) {

		try (CloseableHttpClient client = createHttpClient()) {

			AxolotlClient.LOGGER.info("Uploading image " + file.getName());

			return ImageNetworking.upload(encodeB64(file), url, client, AxolotlClient.LOGGER);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private CloseableHttpClient createHttpClient() {
		String modVer = FabricLoader.getInstance().getModContainer("axolotlclient").orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
		return HttpClients.custom().setUserAgent("AxolotlClient/" + modVer + " ImageShare").build();
	}

	private String encodeB64(File file) {
		try {
			return file.getName() + separator + Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
		} catch (Exception ignored) {
		}
		;

		return "Encoding failed!";
	}

	public ImageInstance downloadImage(String id) {
		if (id.contains(ScreenshotUtils.getInstance().shareUrl.get() + "/api/")) {
			return download(id);
		} else if (id.contains(ScreenshotUtils.getInstance().shareUrl.get()) && !id.contains("api")) {
			return downloadImage(id.substring(id.lastIndexOf("/") + 1));
		} else if (id.startsWith("https://") && id.contains("api")) {
			download(id);
		}
		return download(ScreenshotUtils.getInstance().shareUrl.get() + "/api/" + id);
	}

	public ImageInstance download(String url) {

		if (!url.isEmpty()) {
			JsonElement element = NetworkHelper.getRequest(url, createHttpClient());
			if (element != null) {
				JsonObject response = element.getAsJsonObject();
				String content = response.get("content").getAsString();

				return decodeB64(content);
			}
		}
		return null;
	}

	private ImageInstance decodeB64(String data) {
		try {
			String[] info = data.split(separator);
			byte[] bytes = Base64.getDecoder().decode(info[info.length - 1]);
			return new ImageInstance(NativeImage.read(new ByteArrayInputStream(bytes)), info[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Logger.warn("Not base64 data: "+data);
		return null;
	}
}
