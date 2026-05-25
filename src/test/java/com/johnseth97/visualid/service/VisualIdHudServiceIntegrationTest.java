package com.johnseth97.visualid.service;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import com.johnseth97.visualid.config.VisualIdConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class VisualIdHudServiceIntegrationTest {

    ServerMock server;
    MockPlugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private VisualIdConfig defaultConfig() {
        return new VisualIdConfig(new YamlConfiguration());
    }

    @Test
    void start_schedulesRepeatingTaskWithoutError() {
        VisualIdHudService svc = new VisualIdHudService(plugin, defaultConfig(), new TargetInfoService());
        assertDoesNotThrow(svc::start);
        server.getScheduler().performTicks(10);
    }

    @Test
    void stop_beforeStart_doesNotThrow() {
        VisualIdHudService svc = new VisualIdHudService(plugin, defaultConfig(), new TargetInfoService());
        assertDoesNotThrow(svc::stop);
    }

    @Test
    void stop_afterStart_cancelsTask() {
        VisualIdHudService svc = new VisualIdHudService(plugin, defaultConfig(), new TargetInfoService());
        svc.start();
        assertDoesNotThrow(svc::stop);
        server.getScheduler().performTicks(10); // no task should fire
    }

    @Test
    void reload_replacesScheduledTask() {
        VisualIdHudService svc = new VisualIdHudService(plugin, defaultConfig(), new TargetInfoService());
        svc.start();

        YamlConfiguration newCfg = new YamlConfiguration();
        newCfg.set("update-interval-ticks", 10L);
        assertDoesNotThrow(() -> svc.reload(new VisualIdConfig(newCfg)));

        server.getScheduler().performTicks(15);
        svc.stop();
    }
}
