package me.howandev.velocitystats.papermc;

import lombok.Getter;
import me.howandev.velocitystats.configuration.ConfigurationHandler;
import me.howandev.velocitystats.papermc.events.ReceiveServerInfoEvent;
import me.howandev.velocitystats.papermc.integrations.IntegrationManager;
import me.howandev.velocitystats.protocol.messages.ServerInfoResponse;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class VelocityStatsPlugin extends JavaPlugin {
    @Getter
    private static VelocityStatsPlugin instance = null;
    @Getter
    private static Logger pluginLogger;

    private final Map<String, ServerInfoResponse> serverInfoCache = new ConcurrentHashMap<>();
    private final ConfigurationHandler pluginConfig;
    private CommunicationHandler communicationHandler;
    private IntegrationManager integrationManager;

    public VelocityStatsPlugin() {
        super();

        instance = this;
        pluginLogger = getLogger();

        this.pluginConfig = new ConfigurationHandler(getDataFolder().toPath());
        try {
            pluginConfig.loadOrCreate();
        } catch (Exception e) {
            pluginLogger.log(Level.SEVERE, "Unable to read configuration, using default values.", e);
        }
    }

    @Override
    public void onEnable() {
        communicationHandler = new CommunicationHandler(this);
        if (!communicationHandler.registerChannels()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        communicationHandler.registerHandler(ServerInfoResponse.class, (msg, player) -> {
            serverInfoCache.put(msg.getServerName(), msg);
            getServer().getPluginManager().callEvent(new ReceiveServerInfoEvent(msg));
        });

        integrationManager = new IntegrationManager(this);
        integrationManager.initializeIntegrations();
    }
}
