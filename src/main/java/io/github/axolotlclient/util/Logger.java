package io.github.axolotlclient.util;

import io.github.axolotlclient.AxolotlClient;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;

public class Logger {

    public static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("AxolotlClient");

    private static final String modId = FabricLoader.getInstance().isDevelopmentEnvironment() ? "" : "(AxolotlClient) ";

    public static void warn(String message, Object... args){
        LOGGER.warn(modId + message, args);
    }

    public static void error(String message, Object... args){
        LOGGER.error(modId + message, args);
    }

    public static void info(String message, Object... args){
        LOGGER.info(modId + message, args);
    }

    public static void debug(String message, Object... args){
        if(AxolotlClient.CONFIG.debugLogOutput.get()) {
            info(modId + "[DEBUG] " + message, args);
        }
    }
}
