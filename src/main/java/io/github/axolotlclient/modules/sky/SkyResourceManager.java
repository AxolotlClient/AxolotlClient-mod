package io.github.axolotlclient.modules.sky;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClient;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * https://github.com/AMereBagatelle/FabricSkyBoxes
 **/

public class SkyResourceManager{

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static List<ResourcePack> packs;

    public static void reload(List<ResourcePack> resourcePacks) {
        if(!AxolotlClient.CONFIG.customSky.get())return;
        SkyLoadingScreen loadingScreen = new SkyLoadingScreen();
        loadingScreen.render();
        SkyboxManager.getInstance().clearSkyboxes();
        for (ResourcePack pack : resourcePacks) {
            if(SkyLoadingScreen.currentlyShown)loadingScreen.update(pack);
            if (pack.getNamespaces().contains("fabricskyboxes")) {
                int i = 1;
                while (true) {
                    try {
                        InputStream stream = pack.open(new Identifier("fabricskyboxes", "sky/sky" + i + ".json"));
                        String text = new BufferedReader(
                                new InputStreamReader(stream, StandardCharsets.UTF_8))
                                .lines()
                                .collect(Collectors.joining("\n"));
                        loadSky(text);
                    } catch (IOException e) {
                        break;
                    }
                    i++;
                }
            }
            try{
                pack.open(new Identifier("minecraft", "mcpatcher/sky/world0/sky1.properties"));
                loadMCPSky(pack, "mcpatcher", loadingScreen);
            } catch (IOException ignored) {
            }
            try{
                pack.open(new Identifier("minecraft", "optifine/sky/world0/sky1.properties"));
                loadMCPSky(pack, "optifine", loadingScreen);
            } catch (IOException ignored) {
            }
        }
        loadingScreen.finish();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (Exception ignored){}
    }

    public static void onStartup(){
        if(!packs.isEmpty())reload(packs);
        AxolotlClient.initalized=true;
    }

    public static void loadSky(String json){
        JsonObject object = gson.fromJson(json, JsonObject.class);
        SkyboxManager.getInstance().addSkybox(new FSBSkyboxInstance(object));
    }

    public static void loadMCPSky(ResourcePack pack, String loader, SkyLoadingScreen loadingScreen){
        int i = 1;
        AxolotlClient.LOGGER.info("Loading MCP/OF skies in pack "+pack.getName()+" !");
        while (true) {
            try {
                String source = "";
                int startFadeIn = 0;
                int endFadeIn = 0;
                int startFadeOut= 0;
                int endFadeOut = 0;
                String blend="alpha";
                InputStream stream = pack.open(new Identifier("minecraft", loader + "/sky/world0/sky" + i + ".properties"));

                loadingScreen.setDesc("sky"+i+".properties");

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                String string;
                while((string = reader.readLine()) != null ){
                    try{
                        String[] option = string.split("=");
                        if(option[0].equals("source"))source= loader+"/sky/world0/"+option[1].split("/")[1];
                        if(option[0].equals("startFadeIn"))startFadeIn= Integer.parseInt(option[1].split(":")[0]+option[1].split(":")[1]);
                        if(option[0].equals("endFadeIn"))endFadeIn= Integer.parseInt(option[1].split(":")[0]+option[1].split(":")[1]);
                        if(option[0].equals("startFadeOut"))startFadeOut= Integer.parseInt(option[1].split(":")[0]+option[1].split(":")[1]);
                        if(option[0].equals("endFadeOut"))endFadeOut= Integer.parseInt(option[1].split(":")[0]+option[1].split(":")[1]);
                        if(option[0].equals("blend")) blend=option[1];


                    } catch (Exception ignored){}
                }
                String text = "{" +
                        "\"source\":\"" +source+ "\", " +
                        "\"startFadeIn\":"+startFadeIn/2+", " +
                        "\"endFadeIn\":"+endFadeIn/2+", " +
                        "\"startFadeOut\":"+startFadeOut/2+", " +
                        "\"endFadeOut\":"+endFadeOut/2+", " +
                        "\"blend\":\""+blend+ "\""+
                        "}";
                JsonObject object = gson.fromJson(text, JsonObject.class);
                if(!source.contains("sunflare")) SkyboxManager.getInstance().addSkybox(new MCPSkyboxInstance(object));
            } catch (IOException e) {
                break;
            }
            i++;
        }
    }
}
