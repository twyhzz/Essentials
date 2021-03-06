package com.earth2me.essentials;

import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import net.ess3.api.IEssentials;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;


public class EssentialsBlockListener implements Listener {
    private final transient IEssentials ess;

    public EssentialsBlockListener(final IEssentials ess) {
        this.ess = ess;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        // Do not rely on getItemInHand();
        // http://leaky.bukkit.org/issues/663
        final ItemStack is = LocationUtil.convertBlockToItem(event.getBlockPlaced());

        if (is == null) {
            return;
        }

        if (is.getType() == MaterialUtil.SPAWNER && event.getItemInHand() != null && event.getPlayer() != null && event.getItemInHand().getType() == MaterialUtil.SPAWNER) {
            final BlockState blockState = event.getBlockPlaced().getState();
            if (blockState instanceof CreatureSpawner) {
                final CreatureSpawner spawner = (CreatureSpawner) blockState;
                final EntityType type = ess.getSpawnerItemProvider().getEntityType(event.getItemInHand());
                if (type != null && Mob.fromBukkitType(type) != null) {
                    if (ess.getUser(event.getPlayer()).isAuthorized("essentials.spawnerconvert." + Mob.fromBukkitType(type).name().toLowerCase(Locale.ENGLISH))) {
                        spawner.setSpawnedType(type);
                        spawner.update();
                    }
                }
            }
        }

        final User user = ess.getUser(event.getPlayer());
        if (user.hasUnlimited(is) && user.getBase().getGameMode() == GameMode.SURVIVAL) {
            class UnlimitedItemSpawnTask implements Runnable {
                @Override
                public void run() {
                    user.getBase().getInventory().addItem(is);
                    user.getBase().updateInventory();
                }
            }
            ess.scheduleSyncDelayedTask(new UnlimitedItemSpawnTask());
        }
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final User user = ess.getUser(player);
        final GameMode mode = player.getGameMode();
        if(user.isVanished() || player.isFlying() || user.isGodModeEnabled() || mode != GameMode.SURVIVAL) {
            event.setCancelled(true);
            player.sendMessage(tl("cannotInMode"));
        }
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final User user = ess.getUser(player);
        final GameMode mode = player.getGameMode();
        if(user.isVanished() || player.isFlying() || user.isGodModeEnabled() || mode != GameMode.SURVIVAL) {
            event.setCancelled(true);
            player.sendMessage(tl("cannotInMode"));
        }
    }

}
