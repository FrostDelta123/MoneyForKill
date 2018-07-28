package ru.frostdelta.spongepowered;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;
import java.math.BigDecimal;


public class ConfigManager {

    public static void setConfig(String key, Integer value) {
        ConfigurationLoader<CommentedConfigurationNode> configManager = Loader.getConfigManager();
        Loader.config.getNode(new Object[]{key}).setValue((Object)value);
        try {
            configManager.save(Loader.config);
            configManager.load();
        }
        catch (IOException e) {
            System.out.println("Failed to save " + key + "!");
        }
    }

    public static BigDecimal getAmount(String group, String type){

        ConfigurationNode valueNode = Loader.config.getNode((group + "." + type));

        return BigDecimal.valueOf(valueNode.getDouble());
    }

}