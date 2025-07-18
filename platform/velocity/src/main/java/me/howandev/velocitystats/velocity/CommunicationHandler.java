package me.howandev.velocitystats.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import me.howandev.velocitystats.protocol.MessageSerializer;
import me.howandev.velocitystats.protocol.PluginMessage;
import me.howandev.velocitystats.protocol.messages.ServerInfoRequest;
import me.howandev.velocitystats.protocol.messages.ServerInfoResponse;

import java.io.IOException;
import java.util.Optional;

public class CommunicationHandler {
    private final VelocityStatsPlugin plugin;
    private final ProxyServer proxy;

    public CommunicationHandler(VelocityStatsPlugin plugin, ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;

        proxy.getEventManager().register(plugin, this);
    }

    public void registerChannels() {
        proxy.getChannelRegistrar().register(Constants.IDENTIFIER);
    }

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(Constants.IDENTIFIER)) return;

        event.setResult(PluginMessageEvent.ForwardResult.handled());
        if (!(event.getSource() instanceof ServerConnection backend)) return;

        try {
            PluginMessage message = MessageSerializer.deserialize(event.getData());
            if (message instanceof ServerInfoRequest infoRequest) {
                String name = infoRequest.getServerName();

                Optional<RegisteredServer> optionalServer = proxy.getServer(name);
                if (optionalServer.isEmpty()) {
                    return;
                }

                optionalServer.ifPresent(server -> {
                    long start = System.currentTimeMillis();
                    server.ping().whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            sendPluginMessageToBackend(backend.getServer(),
                                    new ServerInfoResponse(
                                            name,
                                            false,
                                            0,
                                            0,
                                            0
                                    )
                            );
                            return;
                        }

                        int ping = (int) Math.min(System.currentTimeMillis() - start, Integer.MAX_VALUE);
                        int playerCount = result.getPlayers().map(ServerPing.Players::getOnline).orElse(0);
                        int playerLimit = result.getPlayers().map(ServerPing.Players::getMax).orElse(0);

                        sendPluginMessageToBackend(backend.getServer(),
                                new ServerInfoResponse(
                                        name,
                                        true,
                                        ping,
                                        playerCount,
                                        playerLimit
                                )
                        );
                    });
                });
            }

        } catch (Exception ignored) { }
    }

    public void sendPluginMessageToBackend(RegisteredServer server, PluginMessage message) {
        try {
            byte[] data = MessageSerializer.serialize(message);
            server.sendPluginMessage(Constants.IDENTIFIER, data);
        } catch (IOException e) {
            plugin.getPluginLogger().error("Failed to send plugin message to backend: " + server.getServerInfo().getName(), e);
        }
    }
}
