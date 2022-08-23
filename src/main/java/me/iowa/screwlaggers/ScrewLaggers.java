package me.iowa.screwlaggers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.iowa.screwlaggers.data.DataManager;
import me.iowa.screwlaggers.data.PlayerData;
import me.iowa.screwlaggers.settings.LaggerConfig;
import me.iowa.screwlaggers.util.Ticker;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class ScrewLaggers extends JavaPlugin {
    @Getter
    private static ScrewLaggers plugin;

    private final LegacyComponentSerializer componentSerializer = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.AMPERSAND_CHAR)
            .hexCharacter(LegacyComponentSerializer.HEX_CHAR).build();

    private LaggerConfig laggerConfig;

    private Ticker ticker;

    private DataManager dataManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(true).bStats(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        this.laggerConfig = new LaggerConfig(this);

        if (laggerConfig.isSendPackets()) {
            this.ticker = new Ticker();
        }

        this.dataManager = new DataManager();

        if (!getServer().spigot().getConfig().getBoolean("settings.late-bind", true)) {
            Bukkit.getLogger().warning("[ScrewLaggers] Late bind is disabled, this can allow players" +
                    " to join your server before the plugin loads leaving you vulnerable to crashers.");
        }

        PacketEvents.getAPI().getEventManager().registerListener(new SimplePacketListenerAbstract() {
            //TODO: merge these into one method so there's no duplicate code.
            @Override
            public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
                PlayerData data = dataManager.getPlayerData(event.getUser());

                if (data == null) return;

                long now = System.currentTimeMillis();

                if (event.getPacketType() == PacketType.Play.Client.WINDOW_CONFIRMATION) {
                    data.setLastAcknowledgement(now);
                } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {

                    WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
                    if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                        return;
                    }

                    if (now - data.getLastAcknowledgement() > laggerConfig.getLagThreshold()) {
                        event.setCancelled(true);
                    }
                }
            }
        });
        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable() {
        if (laggerConfig.isSendPackets()) {
            this.ticker.getTask().cancel();
        }

        PacketEvents.getAPI().terminate();
    }
}
