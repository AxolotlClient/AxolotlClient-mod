package io.github.axolotlclient.util;

import io.github.axolotlclient.AxolotlClient;
import org.apache.logging.log4j.LogManager;

public class Logger {

    public static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("AxolotlClient");

    public static void warn(String message, Object... args){
        LOGGER.warn(message, args);
    }

    public static void error(String message, Object... args){
        LOGGER.error(message, args);
    }

    public static void info(String message, Object... args){
        LOGGER.info(message, args);
    }

    public static void debug(String message, Object... args){
        if(AxolotlClient.CONFIG.debugLogOutput.get()) {
            info("[DEBUG] "+message, args);
        }
    }
}
