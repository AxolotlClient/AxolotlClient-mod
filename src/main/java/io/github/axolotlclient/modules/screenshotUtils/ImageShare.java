package io.github.axolotlclient.modules.screenshotUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.Util;
import lombok.Getter;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class ImageShare {

    private final String separator = "ⓢ¢€ⓢ¢";

    @Getter
    private static final ImageShare Instance = new ImageShare();
    private ImageShare(){}

    public void uploadImage(String url, File file){
        String downloadUrl = upload(url+"/api/paste", file);

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

        try (CloseableHttpClient client = HttpClients.createDefault()){
            RequestBuilder requestBuilder = RequestBuilder.post(url);
            requestBuilder.setHeader("Content-Type", "application/json");
            String data = encodeB64(file);
            Files.writeString(QuiltLoader.getGameDir().resolve("data.txt"), data);
            requestBuilder.setEntity(new StringEntity("{\"content\":\""+data+"\", \"language\": \"image:png/base64\", \"expiration\": \"168\", \"password\":\"\"}"));

            /*MultipartEntityBuilder builder = MultipartEntityBuilder.create(); // If this is not needed remove httpmime dependency
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", file);
            builder.addTextBody("expiration", "168");
            requestBuilder.setEntity(builder.build());*/

            HttpResponse response = client.execute(requestBuilder.build());

            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            try {
                JsonElement element = JsonParser.parseString(body);
                return element.getAsJsonObject().get("id").getAsString();
            } catch (Exception e){
                Logger.warn("Not Json data: \n"+body);
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
        }
        return download(ScreenshotUtils.getInstance().shareUrl.get()+"/api/"+id);
    }

    public ImageInstance download(String url){

        if(!url.isEmpty()) {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet get = new HttpGet(url);
                get.setHeader("Content-Type", "application/json");

                HttpResponse response = client.execute(get);

                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                try {
                    String content = JsonParser.parseString(body)
                            .getAsJsonObject().get("content").getAsString();
                    return decodeB64(content);
                } catch (Exception e){
                    Logger.warn("Failed to parse JSON: "+body);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // failed to download
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
