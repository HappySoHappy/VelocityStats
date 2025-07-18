package me.howandev.velocitystats.papermc.integrations.papi;

import me.howandev.velocitystats.papermc.integrations.PluginIntegration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PlaceholderApiIntegration extends PluginIntegration {
    private final VelocityStatsExpansion expansion;
    public PlaceholderApiIntegration(@NotNull Plugin plugin) {
        super(plugin);

        expansion = new VelocityStatsExpansion();
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    @Override
    public void initialize() {
        if (!isAvailable())
            throw new IllegalStateException("Unable to initialize PlaceholderAPI integration - Unavailable");

        if (!expansion.register())
            throw new IllegalStateException("Unable to register VeloStats expansion with PlaceholderAPI!");

        getLogger().info("Successfully initialized PlaceholderAPI integration!");
    }
}
