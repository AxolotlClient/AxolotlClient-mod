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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.NetworkUtil;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

@UtilityClass
public class ImageNetworking {

	public String upload(String data, String url, CloseableHttpClient client, Logger logger) throws IOException {

		JsonElement el = NetworkUtil.getRequest(url, client);
		if (el != null) {
			JsonObject initGet = el.getAsJsonObject();
			String tempId = initGet.get("id").getAsString();
			int chunkSize = initGet.get("chunkSize").getAsInt();
			int maxChunks = initGet.get("maxChunks").getAsInt();

			List<String> dataList = new ArrayList<>();

			for (char c : data.toCharArray()) {
				dataList.add(String.valueOf(c));
			}

			List<String> chunks = new ArrayList<>();
			Lists.partition(dataList, chunkSize).forEach(list -> chunks.add(String.join("", list)));

			if (chunks.size() > maxChunks) {
				throw new IllegalStateException("Too much Data!");
			}

			long index = 0;
			for (String content : chunks) {
				RequestBuilder requestBuilder = RequestBuilder.post().setUri(url + "/" + tempId);
				requestBuilder.setHeader("Content-Type", "application/json");
				requestBuilder.setEntity(new StringEntity("{" +
					"\"index\":" + index + "," +
					"  \"content\": \"" + content + "\"" +
					"}"));
				logger.debug(EntityUtils.toString(client.execute(requestBuilder.build()).getEntity()));
				index += content.getBytes(StandardCharsets.UTF_8).length;
			}

			logger.debug("Finishing Stream... tempId was: " + tempId);

			RequestBuilder requestBuilder = RequestBuilder.post().setUri(url + "/" + tempId + "/end");
			requestBuilder.setHeader("Content-Type", "application/json");

			requestBuilder.setEntity(new StringEntity("{\"language\": \"image:png/base64\", \"expiration\": 168, \"password\":\"\"}"));

			HttpResponse response = client.execute(requestBuilder.build());

			String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			try {
				JsonElement element = new JsonParser().parse(body);

				return element.getAsJsonObject().get("pasteId").getAsString();
			} catch (JsonParseException e) {
				logger.warn("Not Json data: \n" + body);
			}
		} else {
			logger.error("Server Error!");
		}
		return "";
	}
}
