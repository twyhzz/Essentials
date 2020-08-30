package com.earth2me.essentials;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ModeCooldowns {

    private static final Map<Player, Long> cooldowns = new HashMap<>();

    private ModeCooldowns() {
        throw new UnsupportedOperationException();
    }

    public static void put(final Player player) {
        remove(player);
        final long time = System.currentTimeMillis();
        cooldowns.put(player, time + (60 * 1000));
    }

    public static void remove(final Player player) {
        cooldowns.remove(player);
    }

    public static boolean isExpired(final Player player) {
        final boolean state = cooldowns.getOrDefault(player, 0L) >= System.currentTimeMillis();
        if(state) remove(player);
        return state;
    }

}
