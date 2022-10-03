package io.github.axolotlclient.modules.sky;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.moehreag.searchInResources.SearchableResourceManager;
import net.legacyfabric.fabric.api.resource.IdentifiableResourceReloadListener;
import net.legacyfabric.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * <a href="https://github.com/AMereBagatelle/FabricSkyBoxes">Github Link.</a>
 * @license MIT
 **/

public class SkyResourceManager extends AbstractModule implements IdentifiableResourceReloadListener {

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void loadSky(String json){
        JsonObject object = gson.fromJson(json, JsonObject.class);
        SkyboxManager.getInstance().addSkybox(new FSBSkyboxInstance(object));
    }

    private void loadMCPSky(String loader, Resource resource){
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        String string;
        try {
            String source = "";
            int startFadeIn = 0;
            int endFadeIn = 0;
            int startFadeOut= 0;
            int endFadeOut = 0;
            String blend="alpha";
            while ((string = reader.readLine()) != null) {
                try {

                    String[] option = string.split("=");
                    if (option[0].equals("source")) {
                        if(option[1].startsWith("assets")){
                            source = option[1].replace("./", "").replace("assets/minecraft/", "");
                        } else {
                            source = loader + "/sky/world0/" + option[1].replace("./", "");
                        }
                    }
                    if (option[0].equals("startFadeIn"))
                        startFadeIn = Integer.parseInt(option[1].replace(":", "").replace("\\", ""));
                    if (option[0].equals("endFadeIn"))
                        endFadeIn = Integer.parseInt(option[1].replace(":", "").replace("\\", ""));
                    if (option[0].equals("startFadeOut"))
                        startFadeOut = Integer.parseInt(option[1].replace(":", "").replace("\\", ""));
                    if (option[0].equals("endFadeOut"))
                        endFadeOut = Integer.parseInt(option[1].replace(":", "").replace("\\", ""));
                    if (option[0].equals("blend")) blend = option[1];


                } catch (Exception ignored) {
                }
            }
            String text = "{" +
                    "\"source\":\"" + source + "\", " +
                    "\"startFadeIn\":" + startFadeIn / 2 + ", " +
                    "\"endFadeIn\":" + endFadeIn / 2 + ", " +
                    "\"startFadeOut\":" + startFadeOut / 2 + ", " +
                    "\"endFadeOut\":" + endFadeOut / 2 + ", " +
                    "\"blend\":\"" + blend + "\"" +
                    "}";
            JsonObject object = gson.fromJson(text, JsonObject.class);
            if (!source.contains("sunflare")) SkyboxManager.getInstance().addSkybox(new MCPSkyboxInstance(object));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public net.legacyfabric.fabric.api.util.Identifier getFabricId() {
        return new net.legacyfabric.fabric.api.util.Identifier(AxolotlClient.modid);
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        SkyboxManager.getInstance().clearSkyboxes();
        for (Map.Entry<Identifier, Resource> entry : ((SearchableResourceManager)resourceManager).findResources("sky", identifier -> identifier.getPath().endsWith(".json")).entrySet()){
            loadSky(new BufferedReader(
                    new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n")));
        }

        for (Map.Entry<Identifier, Resource> entry : ((SearchableResourceManager)resourceManager).findResources("optifine/sky", identifier -> identifier.getPath().endsWith(".properties")).entrySet()){
            loadMCPSky("optifine", entry.getValue());
        }

        for (Map.Entry<Identifier, Resource> entry : ((SearchableResourceManager)resourceManager).findResources("mcpatcher/sky", identifier -> identifier.getPath().endsWith(".properties")).entrySet()) {
            loadMCPSky("mcpatcher", entry.getValue());
        }

        AxolotlClient.initalized = true;
    }

    @Override
    public void init() {
        ResourceManagerHelper.getInstance().registerReloadListener(this);
    }
}
