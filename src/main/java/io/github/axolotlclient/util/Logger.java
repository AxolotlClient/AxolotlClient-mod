package io.github.axolotlclient.util;

import io.github.axolotlclient.AxolotlClient;
import org.quiltmc.loader.api.QuiltLoader;
import org.slf4j.LoggerFactory;

public class Logger {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("AxolotlClient");

    private static final String prefix = QuiltLoader.isDevelopmentEnvironment() ? "" : "(AxolotlClient)";

    public static void info(String msg, Object... args){
        LOGGER.info(prefix, msg, args);
    }

    public static void warn(String msg, Object... args){
        LOGGER.warn(prefix, msg, args);
    }
    public static void error(String msg, Object... args){
        LOGGER.error(prefix, msg, args);
    }
    public static void debug(String msg, Object... args){
        if(AxolotlClient.CONFIG.debugLogOutput.get()) {
            LOGGER.debug(prefix, msg, args);
        }
    }
}
