package io.github.moehreag.axolotlclient.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.moehreag.axolotlclient.Axolotlclient;
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

public class SkyResourceManager{

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void reload(List<ResourcePack> resourcePacks) {
        Axolotlclient.sky_textures=null;
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
                        //e.printStackTrace();
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

/*
        Identifier[] textures = new Identifier[6];
        // 0 = bottom
        // 1 = north
        // 2 = south
        // 3 = top
        // 5 = east
        // 4 = west
        textures[0] = new Identifier(object.get("textures").getAsJsonObject().get("bottom").getAsString());
        textures[1] = new Identifier(object.get("textures").getAsJsonObject().get("north").getAsString());
        textures[2] = new Identifier(object.get("textures").getAsJsonObject().get("south").getAsString());
        textures[3] = new Identifier(object.get("textures").getAsJsonObject().get("top").getAsString());
        textures[4] = new Identifier(object.get("textures").getAsJsonObject().get("east").getAsString());
        textures[5] = new Identifier(object.get("textures").getAsJsonObject().get("west").getAsString());

        //Axolotlclient.LOGGER.info(Arrays.toString(textures));
        //Axolotlclient.sky_textures = textures;*/

    }
}
