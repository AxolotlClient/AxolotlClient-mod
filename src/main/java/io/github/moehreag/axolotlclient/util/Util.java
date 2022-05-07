package io.github.moehreag.axolotlclient.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.Texture;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Util {

    public static Color GlColor = new Color();

    private static Map<Identifier, Texture> textures;

    private static final ThreadPoolExecutor REALTIME_PINGER = new ScheduledThreadPoolExecutor(3, new ThreadFactoryBuilder().setNameFormat("Real Time Server Pinger #%d").setDaemon(true).build());
    public static int currentServerPing = 0;

    public static String lastgame;
    public static String game;


    public static Map<Identifier, Texture> getTextures(){
        return textures;
    }

    public static void setTextures(Map<Identifier, Texture> textures){
        Util.textures =textures;
    }

    /**
     * Gets the amount of ticks in between start and end, on a 24000 tick system.
     *
     * @param start The start of the time you wish to measure
     * @param end   The end of the time you wish to measure
     * @return The amount of ticks in between start and end
     */
    public static int getTicksBetween(int start, int end) {
        if (end < start) end += 24000;
        return end - start;
    }


    public static String getGame(){

        List<String> sidebar = getSidebar();

        if(sidebar.isEmpty()) game = "";
        else if (MinecraftClient.getInstance().getCurrentServerEntry() != null && MinecraftClient.getInstance().getCurrentServerEntry().address.toLowerCase().contains(sidebar.get(0).toLowerCase())){
            if ( sidebar.get(sidebar.size() -1).toLowerCase(Locale.ENGLISH).contains(MinecraftClient.getInstance().getCurrentServerEntry().address.toLowerCase(Locale.ENGLISH)) || sidebar.get(sidebar.size()-1).contains("Playtime")){
                game = "In Lobby";
            }  else {
                if (sidebar.get(sidebar.size()-1).contains("--------")){
                    game = "Playing Bridge Practice";
                } else {
                    game = "Playing "+ sidebar.get(sidebar.size() -1);
                }
            }
        } else {
            game = "Playing "+ sidebar.get(0);
        }

        if(!Objects.equals(lastgame, game) && game.equals("")) game = lastgame;
        else lastgame = game;

        if (game==null){game="";}

        return Formatting.strip( game);
    }


    public static List<String> getSidebar() {
        List<String> lines = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return lines;

        Scoreboard scoreboard = client.world.getScoreboard();
        if (scoreboard == null) return lines;
        ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(1);
        if (sidebar == null) return lines;

        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(sidebar);
        List<ScoreboardPlayerScore> list = scores.stream()
                .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }

        for (ScoreboardPlayerScore score : scores) {
            Team team = scoreboard.getPlayerTeam(score.getPlayerName());
            if (team == null) return lines;
            String text = team.getPrefix() + team.getSuffix();
            if (text.trim().length() > 0)
                lines.add(text);
        }

        lines.add(sidebar.getDisplayName());
        Collections.reverse(lines);

        return lines;
    }



    //Indicatia removed this feature...
    //We still need it :(
    public static void getRealTimeServerPing(ServerInfo server) {
        REALTIME_PINGER.submit(() -> {
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
                            Util.currentServerPing = (int) (latency - time);
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

    public static class Color {
        public float red = 1.0F;
        public float green = 1.0F;
        public float blue = 1.0F;
        public float alpha = 1.0F;

        public Color() {
        }

        public Color(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }

}
