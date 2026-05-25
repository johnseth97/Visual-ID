package com.johnseth97.info;

import com.johnseth97.info.command.InfoCommand;
import com.johnseth97.info.config.InfoConfig;
import com.johnseth97.info.service.InfoHudService;
import com.johnseth97.info.service.TargetInfoService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class InfoPlugin extends JavaPlugin {

    private InfoConfig infoConfig;
    private InfoHudService hudService;
    private TargetInfoService targetInfoService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        infoConfig = new InfoConfig(getConfig());

        targetInfoService = new TargetInfoService();
        hudService = new InfoHudService(this, infoConfig, targetInfoService);
        hudService.start();

        String keyword = getConfig().getString("command", "info");
        registerCommand(keyword);

        getLogger().info("Registered command '/" + keyword + "'. Distance: "
                + infoConfig.maxDistance + ", interval: " + infoConfig.updateIntervalTicks + " ticks.");
    }

    @Override
    public void onDisable() {
        if (hudService != null) hudService.stop();
        getLogger().info("Info plugin disabled.");
    }

    public InfoConfig getInfoConfig() {
        return infoConfig;
    }

    public void reloadInfoConfig() {
        reloadConfig();
        infoConfig = new InfoConfig(getConfig());
        hudService.reload(infoConfig);
    }

    private void registerCommand(String name) {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());
            commandMap.register(getName().toLowerCase(), new InfoCommand(name, this, hudService, targetInfoService));
        } catch (Exception e) {
            getLogger().severe("Failed to register command '/" + name + "': " + e.getMessage());
        }
    }
}
