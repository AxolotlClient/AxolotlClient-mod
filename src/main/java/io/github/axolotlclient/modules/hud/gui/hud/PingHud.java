package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionBase;
import io.github.axolotlclient.util.ThreadExecuter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.net.InetAddress;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class PingHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "pinghud");
    private int currentServerPing;

    private final IntegerOption refreshDelay = new IntegerOption("refreshTime", 4, 1, 15);

    public PingHud() {
        super();
    }

    @Override
    public String getValue() {
        return currentServerPing + " ms";
    }

    @Override
    public String getPlaceholder() {
        return "68 ms";
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean tickable() {
        return true;
    }

    private int second;
    @Override
    public void tick() {
        if(second>=refreshDelay.get()*20){
            if(MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                getRealTimeServerPing(MinecraftClient.getInstance().getCurrentServerEntry());
            }
            second=0;
        } else second++;
    }

    @Override
    public void addConfigOptions(List<OptionBase<?>> options) {
        super.addConfigOptions(options);
        options.add(refreshDelay);
    }

    //Indicatia removed this feature...
    //We still need it :(
    private void getRealTimeServerPing(ServerInfo server)
    {
        ThreadExecuter.submit(() -> {
            try {
                ServerAddress address = ServerAddress.parse(server.address);

                ClientConnection manager = ClientConnection.connect(InetAddress.getByName(address.getAddress()), address.getPort(), false);
                manager.setPacketListener(new ClientQueryPacketListener() {

                    @Override
                    public void onResponse(QueryResponseS2CPacket packet) {
                        this.currentSystemTime = net.minecraft.util.Util.getMeasuringTimeMs();
                        manager.send(new QueryPingC2SPacket(this.currentSystemTime));
                    }

                    @Override
                    public void onPong(QueryPongS2CPacket packet) {
                        long time = this.currentSystemTime;
                        long latency = net.minecraft.util.Util.getMeasuringTimeMs();
                        currentServerPing = (int) (latency - time);
                        manager.disconnect(Text.of(""));
                    }

                    private long currentSystemTime = 0L;

                    @Override
                    public void onDisconnected(Text reason) {}

                    @Override
                    public ClientConnection getConnection()
                    {
                        return manager;
                    }
                });
                manager.send(new HandshakeC2SPacket(address.getAddress(), address.getPort(), NetworkState.STATUS));
                manager.send(new QueryRequestC2SPacket());
            } catch (Exception ignored){}
        });
    }
}
