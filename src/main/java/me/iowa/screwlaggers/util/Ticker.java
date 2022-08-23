package me.iowa.screwlaggers.util;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import lombok.Getter;
import me.iowa.screwlaggers.ScrewLaggers;
import me.iowa.screwlaggers.data.DataManager;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class Ticker {
    @Getter
    private static Ticker instance;

    private final BukkitTask task;

    public Ticker() {
        instance = this;

        ScrewLaggers plugin = ScrewLaggers.getPlugin();
        this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (User user : DataManager.getInstance().getPlayerData().keySet()) {
                user.sendPacket(new WrapperPlayServerWindowConfirmation(Integer.MAX_VALUE, Short.MIN_VALUE, false));
            }
        }, 1, 1);
    }
}
