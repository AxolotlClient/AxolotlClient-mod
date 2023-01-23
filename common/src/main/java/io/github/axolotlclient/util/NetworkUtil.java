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

package io.github.axolotlclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@UtilityClass
public class NetworkUtil {

    public JsonElement getRequest(String url, CloseableHttpClient client) throws IOException {
        return request(new HttpGet(url), client);
    }

    public JsonElement request(HttpUriRequest request, CloseableHttpClient client) throws IOException {
        HttpResponse response = client.execute(request);

        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            throw new IOException("API request failed, status code " + status + "\nBody: " + EntityUtils.toString(response.getEntity()));
        }

        String responseBody = EntityUtils.toString(response.getEntity());

        return new JsonParser().parse(responseBody);
    }

    public JsonElement postRequest(String url, String body, CloseableHttpClient client) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.post().setUri(url);
        requestBuilder.setHeader("Content-Type", "application/json");
        requestBuilder.setEntity(new StringEntity(body));
        return request(requestBuilder.build(), client);
    }

    public JsonElement deleteRequest(String url, String body, CloseableHttpClient client) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.delete().setUri(url);
        requestBuilder.setHeader("Content-Type", "application/json");
        requestBuilder.setEntity(new StringEntity(body));
        return request(requestBuilder.build(), client);
    }
}
