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

        String keyword = getConfig().getString("command", "visual-id");
        InfoCommand cmd = new InfoCommand(keyword, this, hudService, targetInfoService);

        // Always wire the plugin.yml-declared "visual-id" command — this ensures
        // Brigadier pushes it to the client so the command works correctly.
        getCommand("visual-id").setExecutor(cmd);
        getCommand("visual-id").setTabCompleter(cmd);

        // If the admin configured a different keyword, register that alias too.
        if (!keyword.equalsIgnoreCase("visual-id")) {
            registerDynamic(keyword, cmd);
            getLogger().info("Also registered alias '/" + keyword + "'.");
        }

        getLogger().info("Ready. Distance: " + infoConfig.maxDistance
                + ", interval: " + infoConfig.updateIntervalTicks + " ticks.");
    }

    @Override
    public void onDisable() {
        if (hudService != null) hudService.stop();
    }

    public InfoConfig getInfoConfig() {
        return infoConfig;
    }

    public void applyInfoConfig() {
        infoConfig = new InfoConfig(getConfig());
        hudService.reload(infoConfig);
    }

    public void reloadInfoConfig() {
        reloadConfig();
        applyInfoConfig();
    }

    private void registerDynamic(String name, InfoCommand cmd) {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());
            commandMap.register(getName().toLowerCase(), cmd);
        } catch (Exception e) {
            getLogger().severe("Failed to register alias '/" + name + "': " + e.getMessage());
        }
    }
}
