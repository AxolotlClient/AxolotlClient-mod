package io.github.moehreag.axolotlclient.util;

import com.google.common.collect.Lists;
import io.github.moehreag.axolotlclient.Axolotlclient;
import net.legacyfabric.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetadataSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SkyResourceLoader implements IdentifiableResourceReloadListener {
    public Identifier[] textures;
    public static File resourcePath;

    private static final FileFilter FILE_FILTER = file -> {
        boolean bl = file.isFile() && file.getName().endsWith(".zip");
        boolean bl2 = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
        return bl || bl2;
    };

    public static List<ResourcePackLoader.Entry> packs = Lists.newArrayList();

    public static void load(){
        GameOptions gameOptions = MinecraftClient.getInstance().options;

        /*List<ResourcePackLoader.Entry> list = Lists.newArrayList();

        for(File file : this.method_5909()) {
            ResourcePackLoader.Entry entry = new ResourcePackLoader.Entry(file);
            if (!this.field_6621.contains(entry)) {
                try {
                    entry.method_5910();
                    list.add(entry);
                } catch (Exception var6) {
                    list.remove(entry);
                }
            } else {
                int i = this.field_6621.indexOf(entry);
                if (i > -1 && i < this.field_6621.size()) {
                    list.add(this.field_6621.get(i));
                }
            }
        }

        this.field_6621.removeAll(list);

        for(ResourcePackLoader.Entry entry2 : this.field_6621) {
            entry2.method_5912();
        }

        this.field_6621 = list;



        Iterator<String> iterator = gameOptions.resourcePacks.iterator();
        while(iterator.hasNext()) {
            String string = (String)iterator.next();

            for(ResourcePackLoader.Entry entry : ) {
                if (entry.getName().equals(string)) {
                    if (entry.getFormat() == 1 || gameOptions.incompatibleResourcePacks.contains(entry.getName())) {
                        packs.add(entry);
                        break;
                    }

                    iterator.remove();
                }
            }
        }*/

        try {
            File[] zipFiles = resourcePath.listFiles(FILE_FILTER);
        } catch (Exception ignored){}
        //Axolotlclient.LOGGER.info(Arrays.toString(filesList));
    }

    @Override
    public void reload(ResourceManager manager) {
        /*Iterator<String> iterator = MinecraftClient.getInstance().options.resourcePacks.iterator();
        while(iterator.hasNext()) {
            String string = (String)iterator.next();

            for(ResourcePackLoader.Entry entry : MinecraftClient.getInstance().options.resourcePacks) {
                if (entry.getName().equals(string)) {
                    if (entry.getFormat() == 1 || MinecraftClient.getInstance().options.incompatibleResourcePacks.contains(entry.getName())) {
                        packs.add(entry);
                        entry.
                        break;
                    }

                    iterator.remove();
                }
            }
        }
        int count=1;
        while(true) {
            try {
                List<String> packs = MinecraftClient.getInstance().getResourceManager().;
                Resource skies = manager.getResource(new Identifier("fabricskyboxes", "sky/sky"+count+".json"));//+(String string)->string.startsWith("sky")&&string.endsWith(".json")));
                BufferedInputStream stream = (BufferedInputStream) skies.getInputStream();
                String text = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                Axolotlclient.LOGGER.error(text);
            } catch (IOException e) {
                e.printStackTrace();
                break;
                //e.printStackTrace();
            }
            count++;
        }

        //skies.getInputStream().

        /*Iterator<ResourcePack> iterator = resourcePacks.iterator();
        while(iterator.hasNext()){
            for(ResourcePack pack : resourcePacks){
                if(pack.getNamespaces().contains("fabricskyboxes")){
                    //pack.
                }
                Axolotlclient.LOGGER.warn(pack.getNamespaces().toString());
            }
        }*/
    }

    @Override
    public Identifier getFabricId() {
        return null;
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return IdentifiableResourceReloadListener.super.getFabricDependencies();
    }


}

