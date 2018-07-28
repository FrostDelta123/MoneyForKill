package ru.frostdelta.spongepowered;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "moneyforkill",
        name = "MoneyForKill",
        description = "Get money for kill",
        authors = {
                "FrostDelta123"
        }
)
public class Loader {

    public static ConfigurationNode config;
    public static ConfigurationLoader<CommentedConfigurationNode> configurationManager;
    private static Loader instance;
    @Inject
    @DefaultConfig(sharedRoot=true)
    private File dConfig;
    @Inject
    @DefaultConfig(sharedRoot=true)
    private ConfigurationLoader<CommentedConfigurationNode> confManager;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    protected Logger logger;


    @Listener
    public void onServerStart(GameStartedServerEvent e) throws IOException {



        try {
            if (!this.dConfig.exists()) {
                this.dConfig.createNewFile();
                config = this.confManager.load();
                this.confManager.save(config);
            }
            configurationManager = this.confManager;
            config = this.confManager.load();
        }
        catch (IOException exception) {
            this.getLogger().error("The default configuration could not be loaded or created!");
        }

        logger.info("MFK loaded");
        Sponge.getEventManager().registerListeners(this, new EventListener(this));
        ConfigurationNode valueNode = config.getNode("default");
        double value = valueNode.getDouble();
        if(value == 0.0){
            ConfigManager.setConfig("default.lose", 10);
            ConfigManager.setConfig("default.reward", 10);
            confManager.save(config);
        }
    }

    public Logger getLogger(){
        return logger;
    }

    public static ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
        return configurationManager;
    }

    public static void main(String args[]){
    }

    public Path getConfigDirectory() {
        return configDir;
    }
}
