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

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import io.github.axolotlclient.NetworkHelper;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.Util;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class ImageShare {

    private final String separator = ";";//"ⓢ¢€ⓢ¢";

    @Getter
    private static final ImageShare Instance = new ImageShare();
    private ImageShare(){}

    private CloseableHttpClient createHttpClient(){
        String modVer = FabricLoader.getInstance().getModContainer("axolotlclient").orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
        return HttpClients.custom().setUserAgent("AxolotlClient/"+modVer+" ImageShare").build();
    }

    public void uploadImage(String url, File file){
        String downloadUrl = upload(url + "/api/stream", file);

        if (downloadUrl.isEmpty()) {
            Util.sendChatMessage(new LiteralText(I18n.translate("imageUploadFailure")));
        } else {
            Util.sendChatMessage(new LiteralText(I18n.translate("imageUploadSuccess") + " ")
                    .append(new LiteralText(url + "/" + downloadUrl)
                            .setStyle(new Style()
                                    .setUnderline(true)
                                    .setFormatting(Formatting.DARK_PURPLE)
                                    .setClickEvent(new ScreenshotUtils.CustomClickEvent(null) {
                                        @Override
                                        public void doAction() {
                                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url + "/" + downloadUrl), null);
                                        }
                                    }
                                    )
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(I18n.translate("clickToCopy")))))));
        }

    }

    public String upload(String url, File file){

        try (CloseableHttpClient client = createHttpClient()){

            Logger.info("Uploading image "+file.getName());

            JsonElement el = NetworkHelper.getRequest(url, createHttpClient());
            if(el != null) {
                JsonObject initGet = el.getAsJsonObject();
                String tempId = initGet.get("id").getAsString();
                int chunkSize = initGet.get("chunkSize").getAsInt();
                int maxChunks = initGet.get("maxChunks").getAsInt();

                String data = encodeB64(file);

                List<String> dataList = new ArrayList<>();

                for (char c : data.toCharArray()) {
                    dataList.add(String.valueOf(c));
                }

                List<String> chunks = new ArrayList<>();
                Lists.partition(dataList, chunkSize).forEach(list -> chunks.add(String.join("", list)));

                if(chunks.size() > maxChunks){
                    throw new IllegalStateException("Too much Data!");
                }

                long index = 0;
                for (String content : chunks) {
                    RequestBuilder requestBuilder = RequestBuilder.post().setUri(url + "/" + tempId);
                    requestBuilder.setHeader("Content-Type", "application/json");
                    requestBuilder.setEntity(new StringEntity("{" +
                            "\"index\":"+index+"," +
                            "  \"content\": \"" + content + "\"" +
                            "}"));
                    Logger.debug(EntityUtils.toString(client.execute(requestBuilder.build()).getEntity()));
                    index += content.getBytes(StandardCharsets.UTF_8).length;
                }

                Logger.debug("Finishing Stream... tempId was: "+tempId);

                RequestBuilder requestBuilder = RequestBuilder.post().setUri(url + "/" + tempId + "/end");

                requestBuilder.setEntity(new StringEntity("{\"language\": \"image:png/base64\", \"expiration\": 168, \"password\":\"\"}", ContentType.APPLICATION_JSON));

                HttpResponse response = client.execute(requestBuilder.build());

                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                try {
                    JsonElement element = new JsonParser().parse(body);

                    return element.getAsJsonObject().get("pasteId").getAsString();
                } catch (JsonParseException e) {
                    Logger.warn("Not Json data: \n" + body);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            // upload failed.
        }

        return "";
    }

    public ImageInstance downloadImage(String id){
        if(id.contains(ScreenshotUtils.getInstance().shareUrl.get()+"/api/")) {
            return download(id);
        } else if(id.contains(ScreenshotUtils.getInstance().shareUrl.get()) && !id.contains("api")) {
            return downloadImage(id.substring(id.lastIndexOf("/")+1));
        } else if(id.startsWith("https://") && id.contains("api")) {
            download(id);
        }
        return download(ScreenshotUtils.getInstance().shareUrl.get()+"/api/"+id);
    }

    public ImageInstance download(String url){

        if(!url.isEmpty()) {
            JsonElement element = NetworkHelper.getRequest(url, createHttpClient());
            if(element != null) {
                JsonObject response = element.getAsJsonObject();
                String content = response.get("content").getAsString();

                return decodeB64(content);
            }
        }
        return null;
    }

    private String encodeB64(File file){
        try {
            return file.getName() + separator + Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        } catch (Exception ignored){};

        return "Encoding failed!";
    }

    private ImageInstance decodeB64(String data){
        try {
            String[] info = data.split(separator);
            byte[] bytes = Base64.getDecoder().decode(info[info.length-1]);
            return new ImageInstance(ImageIO.read(new ByteArrayInputStream(bytes)), info[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
        //Logger.warn("Not base64 data: "+data);
        return null;
    }
}
