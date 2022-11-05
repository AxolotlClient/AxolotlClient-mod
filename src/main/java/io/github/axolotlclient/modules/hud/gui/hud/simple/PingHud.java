package io.github.axolotlclient.modules.hud.gui.hud.simple;

import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.ThreadExecuter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class PingHud extends SimpleTextHudEntry {
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

    private void updatePing(){
        if (MinecraftClient.getInstance().getCurrentServerEntry() != null) {
            Logger.debug("Refreshing server ping, using "+
                    (MinecraftClient.getInstance().getCurrentServerEntry().ping == 1 ||
                    MinecraftClient.getInstance().getCurrentServerEntry().ping == -1?
                            "Pinger" : "ServerInfo"));
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
                var address = ServerAddress.parse(server.address);
                var optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);


                if (optional.isPresent()) {

                    ClientConnection manager = ClientConnection.connect(optional.get(), false);
                    manager.setPacketListener(new ClientQueryPacketListener() {
                        @Override
                        public void onResponse(QueryResponseS2CPacket packet) {
                            this.currentSystemTime = net.minecraft.util.Util.getMeasuringTimeMs();
                            manager.send(new QueryPingC2SPacket(this.currentSystemTime));
                        }

                        @Override
                        public void onPong(QueryPongS2CPacket packet) {
                            var time = this.currentSystemTime;
                            var latency = net.minecraft.util.Util.getMeasuringTimeMs();
                            currentServerPing = (int) (latency - time);
                            manager.disconnect(Text.of(""));
                        }

                        private long currentSystemTime = 0L;

                        @Override
                        public void onDisconnected(Text reason) {

                        }

                        @Override
                        public ClientConnection getConnection() {
                            return manager;
                        }
                    });
                    manager.send(new HandshakeC2SPacket(address.getAddress(), address.getPort(), NetworkState.STATUS));
                    manager.send(new QueryRequestC2SPacket());
                }
            } catch (Exception ignored) {}
        });
    }
}
