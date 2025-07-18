package me.howandev.velocitystats.velocity;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import lombok.Getter;
import me.howandev.velocitystats.configuration.ConfigurationHandler;
import me.howandev.velocitystats.protocol.messages.ServerInfoResponse;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Getter
public class VelocityStatsPlugin {
    @Getter
    private static VelocityStatsPlugin instance = null;

    private final ProxyServer proxy;
    private final Logger pluginLogger;
    private final ConfigurationHandler pluginConfig;

    @Inject
    public VelocityStatsPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.pluginLogger = logger;
        this.proxy = server;

        this.pluginConfig = new ConfigurationHandler(dataDirectory);
        try {
            pluginConfig.loadOrCreate();
        } catch (Exception e) {
            logger.error("Unable to read configuration, using default values.", e);
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommunicationHandler handler = new CommunicationHandler(this, proxy);
        handler.registerChannels();

        proxy.getScheduler()
                .buildTask(this, () -> {
                    var allServers = proxy.getAllServers().stream().toList();

                    allServers.forEach(server -> {
                        long start = System.currentTimeMillis();

                        server.ping().whenComplete((result, throwable) -> {
                            ServerInfoResponse info;
                            if (throwable != null) {
                                info = new ServerInfoResponse(
                                        server.getServerInfo().getName(),
                                        false,
                                        0,
                                        0,
                                        0
                                );
                            } else {
                                int ping = (int) Math.min(System.currentTimeMillis() - start, Integer.MAX_VALUE);
                                int playerCount = result.getPlayers().map(ServerPing.Players::getOnline).orElse(0);
                                int playerLimit = result.getPlayers().map(ServerPing.Players::getMax).orElse(0);

                                info = new ServerInfoResponse(
                                        server.getServerInfo().getName(),
                                        true,
                                        ping,
                                        playerCount,
                                        playerLimit
                                );
                            }

                            allServers.forEach(recipient -> handler.sendPluginMessageToBackend(recipient, info));
                        });
                    });
                })
                .repeat(5, TimeUnit.SECONDS)
                .schedule();
    }

}
