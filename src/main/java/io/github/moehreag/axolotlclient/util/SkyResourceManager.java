package io.github.moehreag.axolotlclient.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.moehreag.axolotlclient.modules.sky.SkyboxInstance;
import io.github.moehreag.axolotlclient.modules.sky.SkyboxManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
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
        for (ResourcePack pack : resourcePacks) {
            if (pack.getNamespaces().contains("fabricskyboxes")) {
                int i = 1;
                while (true) {
                    try {
                        BufferedInputStream stream = (BufferedInputStream) pack.open(new Identifier("fabricskyboxes", "sky/sky" + i + ".json"));
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
        }
    }

    public static void loadSky(String json){
        JsonObject object = gson.fromJson(json, JsonObject.class);
        SkyboxManager.getInstance().addSkybox(new SkyboxInstance(object));
    }
}
