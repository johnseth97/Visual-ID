package com.johnseth97.info.service;

import com.johnseth97.info.config.InfoConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InfoHudService {

    private final Plugin plugin;
    private final TargetInfoService targetInfoService;
    private final Set<UUID> disabledPlayers = new HashSet<>();

    private BukkitTask task;
    private InfoConfig config;

    public InfoHudService(Plugin plugin, InfoConfig config, TargetInfoService targetInfoService) {
        this.plugin = plugin;
        this.config = config;
        this.targetInfoService = targetInfoService;
    }

    public void start() {
        if (task != null) task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0L, config.updateIntervalTicks);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void reload(InfoConfig newConfig) {
        this.config = newConfig;
        start();
    }

    public boolean toggle(Player player) {
        UUID id = player.getUniqueId();
        if (config.enabledByDefault) {
            if (disabledPlayers.contains(id)) {
                disabledPlayers.remove(id);
                return true;
            } else {
                disabledPlayers.add(id);
                return false;
            }
        } else {
            if (disabledPlayers.contains(id)) {
                disabledPlayers.remove(id);
                return false;
            } else {
                disabledPlayers.add(id);
                return true;
            }
        }
    }

    public boolean isEnabled(Player player) {
        boolean inSet = disabledPlayers.contains(player.getUniqueId());
        return config.enabledByDefault != inSet;
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("info.use")) continue;
            if (!isEnabled(player)) continue;

            Component component = targetInfoService.getTargetComponent(player, config);
            if (component != null) {
                player.sendActionBar(component);
            }
        }
    }
}
