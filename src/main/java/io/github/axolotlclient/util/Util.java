package io.github.axolotlclient.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.ClientCommands;
import io.github.axolotlclient.AxolotlclientConfig.util.clientCommands.Command;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.util.Window;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Collectors;


public class Util {

    public static Color GlColor = new Color();
    public static String lastgame;
    public static String game;

    @ApiStatus.Internal
    public static Window window;

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

    public static int toGlCoordsX(int x){
        if (window==null) {
            window = new Window(MinecraftClient.getInstance());
        }
        return x * window.getScaleFactor();
    }

    public static int toGlCoordsY(int y){
        if (window==null) {
            window = new Window(MinecraftClient.getInstance());
        }
        int scale = window.getScaleFactor();
        return MinecraftClient.getInstance().height - y * scale - scale;
    }

    public static int toMCCoordsX(int x){
        if (window==null) {
           window = new Window(MinecraftClient.getInstance());
        }
        return x * window.getWidth() / MinecraftClient.getInstance().width;
    }
    public static int toMCCoordsY(int y){
        if (window==null) {
            window = new Window(MinecraftClient.getInstance());
        }
        return window.getHeight() - y * window.getHeight() / MinecraftClient.getInstance().height - 1;
    }

    public static Window getWindow(){
        if(window==null){
            window = new Window(MinecraftClient.getInstance());
        }
        return window;
    }

    public static void sendChatMessage(String msg){
        MinecraftClient.getInstance().player.sendChatMessage(msg);
    }

    public static void sendChatMessage(Text msg){
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(msg);
    }

    public static void registerCommand(String command, Command.CommandSuggestionCallback suggestions, Command.CommandExecutionCallback onExecution){
        ClientCommands.getInstance().registerCommand(command, suggestions, onExecution);
    }

    public static String[] copyArrayWithoutFirstEntry(String[] strings) {
        String[] strings2 = new String[strings.length - 1];
        System.arraycopy(strings, 1, strings2, 0, strings.length - 1);
        return strings2;
    }

    public static String splitAtCapitalLetters(String string){
        if(string==null || string.isEmpty()){
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for(char c:string.toCharArray()){
            if(Character.isUpperCase(c) && c != string.charAt(0)){
                builder.append(" ");
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public static String getGame(){

        List<String> sidebar = getSidebar();

        if(sidebar.isEmpty()) game = "";
        else if (MinecraftClient.getInstance().getCurrentServerEntry() != null && MinecraftClient.getInstance().getCurrentServerEntry().address.toLowerCase().contains(sidebar.get(0).toLowerCase())){
            if ( sidebar.get(sidebar.size() -1).toLowerCase(Locale.ROOT)
                    .contains(MinecraftClient.getInstance().getCurrentServerEntry().address.toLowerCase(Locale.ROOT)) ||
                    sidebar.get(sidebar.size()-1).contains("Playtime")){
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

    public static double calculateDistance(Vec3d pos1, Vec3d pos2){
        return calculateDistance(pos1.x, pos2.x, pos1.y, pos2.y, pos1.z, pos2.z);
    }

    public static double calculateDistance(double x1, double x2, double y1, double y2, double z1, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
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

    public static void applyScissor(Rectangle scissor){
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Window window = new Window(MinecraftClient.getInstance());
        int scale = window.getScaleFactor();
        GL11.glScissor(scissor.x * scale, (int) ((window.getScaledHeight() - scissor.height - scissor.y) * scale), scissor.width * scale, scissor.height * scale);
    }

    public static float lerp(float start, float end, float percent) {
        return start + ((end - start) * percent);
    }

    public static float easeInOutSine(float start, float end, float percent){
        return (float) (start + (-(Math.cos(Math.PI * percent) - end) / 2));
    }

    // https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
    public static String toRoman(int number) {
        if(number>0) {
            return String.join("", Collections.nCopies(number, "I"))
                    .replace("IIIII", "V")
                    .replace("IIII", "IV")
                    .replace("VV", "X")
                    .replace("VIV", "IX")
                    .replace("XXXXX", "L")
                    .replace("XXXX", "XL")
                    .replace("LL", "C")
                    .replace("LXL", "XC")
                    .replace("CCCCC", "D")
                    .replace("CCCC", "CD")
                    .replace("DD", "M")
                    .replace("DCD", "CM");
        }
        return "";
    }
}
