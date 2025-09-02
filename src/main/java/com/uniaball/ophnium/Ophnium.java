package com.uniaball.ophnium;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ophnium implements ModInitializer {
    public static final String MOD_ID = "ophnium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Ophnium-1.0.0 initialized!");
    }
}
