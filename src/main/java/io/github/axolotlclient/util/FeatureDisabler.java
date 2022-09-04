package io.github.axolotlclient.util;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.DisableReason;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

import java.util.Locale;

public class FeatureDisabler {

    private static final String[] fullbrightServers = new String[]{
            "gommehd"
    };

    private static final String[] timeChangerServers = new String[]{
            "gommehd"
    };

    public static void onServerJoin(ServerInfo info){
        disableFullbright(info.address);
        disableTimeChanger(info.address);
    }

    private static void disableFullbright(String address){
        boolean ban = false;
        for(String s:fullbrightServers){
            if (address.toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                ban = true;
                break;
            }
        }

        if(AxolotlClient.CONFIG.fullBright.getForceDisabled() != ban) {
            AxolotlClient.CONFIG.fullBright.setForceOff(ban, DisableReason.BAN_REASON);
        }
    }

    private static void disableTimeChanger(String address){
        boolean ban = false;
        for(String s:timeChangerServers){
            if (address.toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                ban = true;
                break;
            }
        }

        if(AxolotlClient.CONFIG.timeChangerEnabled.getForceDisabled() != ban) {
            AxolotlClient.CONFIG.timeChangerEnabled.setForceOff(ban, DisableReason.BAN_REASON);
        }
    }
}
