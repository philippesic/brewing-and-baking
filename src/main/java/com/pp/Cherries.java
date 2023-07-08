package com.pp;


import net.fabricmc.api.ModInitializer;
import java.lang.String;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pp.item.ModItems;


public class Cherries implements ModInitializer {

    public static final String MOD_ID = "cherries";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

        @Override
        public void onInitialize() {

            ModItems.registerModItems();
            ModItems.initializeGroups();
            ModItems.init();
    }
}
