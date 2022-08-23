package me.iowa.screwlaggers.settings;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import me.iowa.screwlaggers.ScrewLaggers;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@Setter
public class LaggerConfig {
    @Getter
    private static LaggerConfig instance;

    private final long lagThreshold;
    private final boolean sendPackets;

    public LaggerConfig(ScrewLaggers plugin) {
        instance = this;

        plugin.saveDefaultConfig();

        FileConfiguration configuration = plugin.getConfig();
        configuration.addDefaults(ImmutableMap.<String, Object>builder()
                        .put("lagThreshold", 200)
                        .put("sendPackets", true)
                .build());

        this.lagThreshold = configuration.getLong("lagThreshold", 200);
        this.sendPackets = configuration.getBoolean("sendPackets", true);
    }
}
