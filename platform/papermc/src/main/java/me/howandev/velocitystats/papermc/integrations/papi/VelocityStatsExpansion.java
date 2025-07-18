package me.howandev.velocitystats.papermc.integrations.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.howandev.velocitystats.papermc.VelocityStatsPlugin;
import me.howandev.velocitystats.protocol.messages.ServerInfoResponse;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class VelocityStatsExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "proxy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "HowanDev";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getRequiredPlugin() {
        return "VelocityStats";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.startsWith("players_on_")) {
            String server = params.substring("players_on_".length());

            ServerInfoResponse cached = VelocityStatsPlugin.getInstance().getServerInfoCache().get(server);
            if (cached != null) {
                return String.valueOf(cached.getPlayerCount());
            }

            return "0";
        }

        if (params.startsWith("max_players_on_")) {
            String server = params.substring("max_players_on_".length());

            ServerInfoResponse cached = VelocityStatsPlugin.getInstance().getServerInfoCache().get(server);
            if (cached != null) {
                return String.valueOf(cached.getMaxPlayerCount());
            }

            return "0";
        }

        if (params.startsWith("ping_")) {
            String server = params.substring("ping_".length());

            ServerInfoResponse cached = VelocityStatsPlugin.getInstance().getServerInfoCache().get(server);
            if (cached != null) {
                return String.valueOf(cached.getPing());
            }

            return "0";
        }

        if (params.startsWith("online_")) {
            String server = params.substring("online_".length());

            ServerInfoResponse cached = VelocityStatsPlugin.getInstance().getServerInfoCache().get(server);
            if (cached != null) {
                return String.valueOf(cached.isOnline());
            }

            return "false";
        }

        return null;
    }
}
