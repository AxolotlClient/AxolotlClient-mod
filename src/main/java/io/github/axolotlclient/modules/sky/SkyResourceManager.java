package io.github.axolotlclient.modules.sky;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.Logger;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleSynchronousResourceReloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * <a href="https://github.com/AMereBagatelle/FabricSkyBoxes">Github Link.</a>
 * @license MIT
 **/

public class SkyResourceManager extends AbstractModule implements SimpleSynchronousResourceReloader {

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final static SkyResourceManager Instance = new SkyResourceManager();

    public static SkyResourceManager getInstance(){
        return Instance;
    }

    private static JsonObject loadMCPSky(String loader, Identifier id, Resource resource){
        JsonObject object = new JsonObject();
        String line;

        try (BufferedReader reader = resource.openBufferedReader()) {

            while ((line = reader.readLine()) != null){

                if(!line.startsWith("#")) {
                    String[] option = line.split("=");

                    if (option[0].equals("source")) {
                        if(option[1].startsWith("assets")){
                            option[1] = option[1].replace("./", "").replace("assets/minecraft/", "");
                        } else {
                            if(id.getPath().contains("world")) {
                                option[1] = loader + "/sky/world" + id.getPath().split("world")[1].split("/")[0] + "/" + option[1].replace("./", "");
                            }
                        }
                    }
                    if (option[0].equals("startFadeIn") || option[0].equals("endFadeIn") || option[0].equals("startFadeOut") || option[0].equals("endFadeOut")) {
                        option[1] = option[1].replace(":", "").replace("\\", "");
                    }

                    object.addProperty(option[0], option[1]);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public void init() {

    }

    @Override
    public @NotNull Identifier getQuiltId() {
        return new Identifier("axolotlclient", "custom_skies");
    }

    @Override
    public void reload(ResourceManager manager) {
        Logger.debug("Loading Custom Skies!");
        SkyboxManager.getInstance().clearSkyboxes();

        for(Map.Entry<Identifier, Resource> entry: manager.findResources("sky", identifier -> identifier.getPath().endsWith(".json")).entrySet()){
            Logger.debug("Loading FSB sky from " + entry.getKey());
            try (BufferedReader reader = entry.getValue().openBufferedReader()) {
                SkyboxManager.getInstance().addSkybox(new FSBSkyboxInstance(gson.fromJson(reader.lines().collect(Collectors.joining("\n")),
                        JsonObject.class)));
                Logger.debug("Loaded FSB sky from " + entry.getKey());
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }

        for(Map.Entry<Identifier, Resource> entry: manager.findResources("mcpatcher", identifier -> identifier.getPath().endsWith(".properties")).entrySet()){
            Logger.debug("Loading MCP sky from " + entry.getKey());
            SkyboxManager.getInstance().addSkybox(new MCPSkyboxInstance(loadMCPSky("mcpatcher", entry.getKey(), entry.getValue())));
            Logger.debug("Loaded MCP sky from "+entry.getKey());
        }

        for(Map.Entry<Identifier, Resource> entry: manager.findResources("optifine", identifier -> identifier.getPath().endsWith(".properties")).entrySet()){
            Logger.debug("Loading OF sky from " + entry.getKey());
            SkyboxManager.getInstance().addSkybox(new MCPSkyboxInstance(loadMCPSky("optifine", entry.getKey(), entry.getValue())));
            Logger.debug("Loaded OF sky from "+entry.getKey());
        }

        Logger.debug("Finished Loading Custom Skies!");
    }
}
