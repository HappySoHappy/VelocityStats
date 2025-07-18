package me.howandev.velocitystats.papermc;

import me.howandev.velocitystats.protocol.MessageSerializer;
import me.howandev.velocitystats.protocol.PluginMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommunicationHandler implements PluginMessageListener {

    private final JavaPlugin plugin;
    private final Map<Class<? extends PluginMessage>, PluginMessageHandler<?>> handlerMap = new HashMap<>();

    public CommunicationHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean registerChannels() {
        try {
            Messenger messenger = plugin.getServer().getMessenger();
            if (!messenger.isOutgoingChannelRegistered(plugin, Constants.CHANNEL_ID)) {
                messenger
                        .registerOutgoingPluginChannel(plugin, Constants.CHANNEL_ID);
            }

            if (!messenger.isIncomingChannelRegistered(plugin, Constants.CHANNEL_ID)) {
                messenger
                        .registerIncomingPluginChannel(plugin, Constants.CHANNEL_ID, this);
            }

            return true;
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to register communication channels", ex);
            return false;
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals(Constants.CHANNEL_ID)) return;

        try {
            PluginMessage pluginMessage = MessageSerializer.deserialize(message);

            // decryption logic here, discard bad requests.
            dispatch(pluginMessage, player);
        } catch (Exception ignored) { }
    }

    public boolean sendPluginMessageToBackend(PluginMessage message) {
        Player player = plugin.getServer().getOnlinePlayers().stream().findAny().orElse(null);
        if (player == null || !player.isOnline()) {
            return false;
        }

        try {
            byte[] data = MessageSerializer.serialize(message);
            player.sendPluginMessage(plugin, Constants.CHANNEL_ID, data);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to send plugin message", e);
            return false;
        }
    }

    public <T extends PluginMessage> void registerHandler(Class<T> type, PluginMessageHandler<T> handler) {
        handlerMap.put(type, handler);
    }

    @SuppressWarnings("unchecked")
    private <T extends PluginMessage> void dispatch(T message, Player player) {
        PluginMessageHandler<T> handler = (PluginMessageHandler<T>) handlerMap.get(message.getClass());
        if (handler != null) {
            handler.handle(message, player);
        } else {
            plugin.getLogger().warning("Received message for unregistered handler " + message.getSubchannel()
                    + " from " + player.getName() + "(" + player.getUniqueId() + ")");
        }
    }

    public interface PluginMessageHandler<T extends PluginMessage> {
        void handle(T message, Player player);
    }
}
