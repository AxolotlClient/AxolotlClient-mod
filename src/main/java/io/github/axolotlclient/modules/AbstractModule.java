package io.github.axolotlclient.modules;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractModule {

    protected MinecraftClient client;

    public AbstractModule(){
        client=MinecraftClient.getInstance();
    }

    public abstract void init();

    public void lateInit(){}

    public void tick(){}
}
