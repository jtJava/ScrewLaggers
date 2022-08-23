package me.iowa.screwlaggers.data;

import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class PlayerData {
    private final User user;

    private long lastAcknowledgement;
}
