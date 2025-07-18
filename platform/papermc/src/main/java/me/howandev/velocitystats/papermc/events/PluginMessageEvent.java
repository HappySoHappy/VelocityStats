package me.howandev.velocitystats.papermc.events;

import lombok.Getter;
import me.howandev.velocitystats.protocol.PluginMessage;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class PluginMessageEvent<T extends PluginMessage> extends org.bukkit.event.Event {
    private static final HandlerList handlers = new HandlerList();
    protected final T message;

    public PluginMessageEvent(T message) {
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
