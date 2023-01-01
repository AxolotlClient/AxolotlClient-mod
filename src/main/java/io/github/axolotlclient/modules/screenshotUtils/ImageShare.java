package io.github.axolotlclient.modules.screenshotUtils;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.axolotlclient.NetworkHelper;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.Util;
import lombok.Getter;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ImageShare {

    private final String separator = ";";//"ⓢ¢€ⓢ¢";

    @Getter
    private static final ImageShare Instance = new ImageShare();
    private ImageShare(){}

    private CloseableHttpClient createHttpClient(){
        String modVer = QuiltLoader.getModContainer("axolotlclient").orElseThrow(RuntimeException::new).metadata().version().raw();
        return HttpClients.custom().setUserAgent("AxolotlClient/"+modVer+" ImageShare").build();
    }

    public void uploadImage(String url, File file){
        String downloadUrl = upload(url+"/api/stream", file);

        if(downloadUrl.isEmpty()){
            Util.sendChatMessage(Text.translatable("imageUploadFailure"));
        } else {
            Util.sendChatMessage(Text.translatable("imageUploadSuccess").append(" ")
                    .append(Text.literal(downloadUrl)
                            .setStyle(Style.EMPTY
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, downloadUrl))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("clickToCopy"))))));
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
                requestBuilder.setHeader("Content-Type", "application/json");

                requestBuilder.setEntity(new StringEntity("{\"language\": \"image:png/base64\", \"expiration\": 168, \"password\":\"\"}"));

                HttpResponse response = client.execute(requestBuilder.build());

                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                try {
                    JsonElement element = JsonParser.parseString(body);

                    return element.getAsJsonObject().get("pasteId").getAsString();
                } catch (Exception e) {
                    Logger.warn("Not Json data: \n" + body);
                }
            } else {
                Logger.error("Server Error!");
            }
        } catch (Exception e){
            e.printStackTrace();
            // upload failed.
        }

        return "";
    }

    public ImageInstance downloadImage(String id){
        if(id.contains(ScreenshotUtils.getInstance().shareUrl.get()+"/api/")){
            return download(id);
        } else if(id.contains("/")){
            return null;
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
            return new ImageInstance(NativeImage.read(new ByteArrayInputStream(bytes)), info[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
        //Logger.warn("Not base64 data: "+data);
        return null;
    }
}
