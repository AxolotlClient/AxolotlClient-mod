package io.github.axolotlclient.modules.screenshotUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.Util;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;

public class ImageShare {

    private final String separator = "ⓢ¢€ⓢ¢";

    private String localCache = "";

    @Getter
    private static final ImageShare Instance = new ImageShare();
    private ImageShare(){}

    public void uploadImage(String url, File file){
        String downloadUrl = upload(url+"/api/paste", file);

        if(downloadUrl.isEmpty()){
            Util.sendChatMessage(new LiteralText(I18n.translate("imageUploadFailure")));
        } else {
            Util.sendChatMessage(new LiteralText(I18n.translate("imageUploadSuccess") +" ")
                    .append(new LiteralText(downloadUrl)
                            .setStyle(new Style()
                                    .setClickEvent(new ScreenshotUtils.CustomClickEvent(null){
                                                       @Override
                                                       public void doAction() {
                                                           Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(downloadUrl), null);
                                                       }
                                                   }
                                            )
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(I18n.translate("clickToCopy")))))));
        }
    }

    public String upload(String url, File file){

        try (CloseableHttpClient client = HttpClients.createDefault()){
            RequestBuilder requestBuilder = RequestBuilder.post().setUri(url);
            requestBuilder.setHeader("Content-Type", "application/json");
            String data = encodeB64(file);
            requestBuilder.setEntity(new StringEntity("{\"content\":\""+data+"\", \"language\": \"image:png/base64\", \"expiration\": \"168\", \"password\":\"\"}"));

            /*MultipartEntityBuilder builder = MultipartEntityBuilder.create(); // If this is not needed remove httpmime dependency
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", file);
            builder.addTextBody("expiration", "168");
            requestBuilder.setEntity(builder.build());*/

            HttpResponse response = client.execute(requestBuilder.build());

            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            try {
                JsonElement element = new JsonParser().parse(body);
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
                    String content = new JsonParser().parse(body)
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
            return new ImageInstance(ImageIO.read(new ByteArrayInputStream(bytes)), info[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
        //Logger.warn("Not base64 data: "+data);
        return null;
    }
}
