package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.net.InetAddress;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class PingHud extends SimpleTextHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "pinghud");

    private int currentServerPing;

    private final IntegerOption refreshDelay = new IntegerOption("axolotlclient.refreshTime", 4, 1, 15);

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

    private void updatePing(){
        if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            if (MinecraftClient.getInstance().getCurrentServerEntry().ping==1 ||
                    MinecraftClient.getInstance().getCurrentServerEntry().ping == -1) {
                getRealTimeServerPing(MinecraftClient.getInstance().getCurrentServerEntry());
            } else {
                currentServerPing = (int) MinecraftClient.getInstance().getCurrentServerEntry().ping;
            }
        } else if (MinecraftClient.getInstance().isIntegratedServerRunning()){
            currentServerPing = 1;
        }
    }

    private int second;
    @Override
    public void tick() {
        if(second>=refreshDelay.get()*40){
            updatePing();
            second=0;
        } else second++;
    }

    @Override
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(refreshDelay);
        return options;
    }

    //Indicatia removed this feature...
    //We still need it :(
    private void getRealTimeServerPing(ServerInfo server) {
        ThreadExecuter.scheduleTask(() -> {
            try {
                ServerAddress address = ServerAddress.parse(server.address);
                final ClientConnection manager = ClientConnection.connect(InetAddress.getByName(address.getAddress()), address.getPort(), false);

                manager.setPacketListener(new ClientQueryPacketListener() {
                    @Override
                    public void onResponse(QueryResponseS2CPacket packet) {
                        this.currentSystemTime = MinecraftClient.getTime();
                        manager.send(new QueryPingC2SPacket(this.currentSystemTime));
                    }

                    @Override
                    public void onPong(QueryPongS2CPacket packet) {
                        long time = this.currentSystemTime;
                        long latency = MinecraftClient.getTime();
                        currentServerPing = (int) (latency - time);
                        manager.disconnect(new LiteralText(""));
                    }

                    private long currentSystemTime = 0L;

                    @Override
                    public void onDisconnected(Text reason) {

                    }
                });
                manager.send(new HandshakeC2SPacket(47, address.getAddress(), address.getPort(), NetworkState.STATUS));
                manager.send(new QueryRequestC2SPacket());
            }
            catch (Exception ignored){}
        });
    }
}
