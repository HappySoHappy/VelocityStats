package me.howandev.velocitystats.papermc.events;

import lombok.Getter;
import me.howandev.velocitystats.protocol.messages.ServerInfoResponse;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ReceiveServerInfoEvent extends PluginMessageEvent<ServerInfoResponse> {
    private static final HandlerList handlers = new HandlerList();

    private final String serverName;
    private final boolean online;
    private final int ping;
    private final int playerCount;
    public ReceiveServerInfoEvent(ServerInfoResponse message) {
        super(message);

        serverName = message.getServerName();
        online = message.isOnline();
        ping = message.getPing();
        playerCount = message.getPlayerCount();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
