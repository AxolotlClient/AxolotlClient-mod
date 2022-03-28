package io.github.moehreag.axolotlclient.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.modules.sky.FSBSkyboxInstance;
import io.github.moehreag.axolotlclient.modules.sky.MCPSkyboxInstance;
import io.github.moehreag.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * https://github.com/AMereBagatelle/FabricSkyBoxes
 **/

public class SkyResourceManager{

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void reload(List<ResourcePack> resourcePacks) {
        SkyboxManager.getInstance().clearSkyboxes();
        for (ResourcePack pack : resourcePacks) {
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
                loadMCPSky(pack, "mcpatcher");
            } catch (IOException ignored) {
            }
            try{
                pack.open(new Identifier("minecraft", "optifine/sky/world0/sky1.properties"));
                loadMCPSky(pack, "optifine");
            } catch (IOException ignored) {
            }
        }
    }

    public static void loadSky(String json){
        JsonObject object = gson.fromJson(json, JsonObject.class);
        SkyboxManager.getInstance().addSkybox(new FSBSkyboxInstance(object));
    }

    public static void loadMCPSky(ResourcePack pack, String loader){
        int i = 1;
        Axolotlclient.LOGGER.info("Loading MCP/OF sky!");
        while (true) {
            try {
                String source = "";
                int startFadeIn = 0;
                int endFadeIn = 0;
                int startFadeOut= 0;
                int endFadeOut = 0;
                InputStream stream = pack.open(new Identifier("minecraft", loader + "/sky/world0/sky" + i + ".properties"));

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


                    } catch (Exception ignored){}
                }
                String text = "{" +
                        "\"source\":\"" +source+ "\", " +
                        "\"startFadeIn\":"+startFadeIn/2+", " +
                        "\"endFadeIn\":"+endFadeIn/2+", " +
                        "\"startFadeOut\":"+startFadeOut/2+", " +
                        "\"endFadeOut\":"+endFadeOut/2+
                        "}";
                JsonObject object = gson.fromJson(text, JsonObject.class);
                if(!source.contains("sunflare"))SkyboxManager.getInstance().addSkybox(new MCPSkyboxInstance(object));
            } catch (IOException e) {
                break;
            }
            i++;
        }
    }
}
