package me.howandev.velocitystats.papermc.integrations;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.howandev.velocitystats.papermc.integrations.papi.PlaceholderApiIntegration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class IntegrationManager {
    @Getter
    @Accessors(fluent = true)
    private final Plugin plugin;

    @Getter
    private final @NotNull PlaceholderApiIntegration placeholderApi;

    public IntegrationManager(final @NotNull Plugin plugin) {
        this.plugin = plugin;

        placeholderApi = new PlaceholderApiIntegration(plugin);
    }

    public void initializeIntegrations() {
        if (placeholderApi.isAvailable())
            placeholderApi.initialize();
    }
}
