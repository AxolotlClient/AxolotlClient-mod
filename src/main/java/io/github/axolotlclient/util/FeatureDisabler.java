package io.github.axolotlclient.util;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.DisableReason;
import io.github.axolotlclient.modules.freelook.Freelook;
import net.minecraft.client.network.ServerInfo;

import java.util.HashMap;
import java.util.Locale;

public class FeatureDisabler {

    private static final HashMap<BooleanOption, String[]> disabledServers = new HashMap<>();

    public static void init(){
        setServers(AxolotlClient.CONFIG.fullBright, "gommehd");
        setServers(AxolotlClient.CONFIG.timeChangerEnabled, "gommehd");
        setServers(Freelook.getInstance().enabled, "hypixel", "mineplex", "gommehd");
    }

    public static void onServerJoin(ServerInfo info){
        disabledServers.forEach((option, strings) -> disableOption(option, strings, info.address));
    }

    public static void clear(){
        disabledServers.forEach((option, strings) -> {
            option.setForceOff(false, DisableReason.BAN_REASON);
        });
    }

    private static void disableOption(BooleanOption option, String[] servers, String currentServer){
        boolean ban = false;
        for(String s:servers){
            if (currentServer.toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                ban = true;
                break;
            }
        }

        if(option.getForceDisabled() != ban) {
            option.setForceOff(ban, DisableReason.BAN_REASON);
        }
    }

    private static void setServers(BooleanOption option, String... servers){
        disabledServers.put(option, servers);
    }
}
