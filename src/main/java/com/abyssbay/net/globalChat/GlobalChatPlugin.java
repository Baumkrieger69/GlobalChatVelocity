package com.abyssbay.net.globalChat;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(
    id = "globalchat",
    name = "GlobalChat",
    version = "1.0.0",
    description = "Global chat system for Velocity proxy with server blacklisting support",
    authors = {"Baumkrieger69"}
)
public class GlobalChatPlugin {
    private final ProxyServer proxyServer;
    private final Logger logger;
    private ConfigManager configManager;

    @Inject
    public GlobalChatPlugin(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        // Initialize configuration manager
        Path configPath = Paths.get("plugins", "globalchat", "config.yml");
        configManager = new ConfigManager(configPath);

        // Register chat listeners
        proxyServer.getEventManager().register(this, new ChatListener(proxyServer, configManager));
        proxyServer.getEventManager().register(this, new MinecraftChatCommandHandler(proxyServer, configManager));

        // Register commands
        CommandManager commandManager = proxyServer.getCommandManager();
        
        // /msg command
        CommandMeta msgMeta = commandManager.metaBuilder("msg")
            .aliases("m", "pm", "message", "whisper")
            .build();
        commandManager.register(msgMeta, new PrivateMessageCommand(proxyServer, configManager));
        
        // /reply command
        CommandMeta replyMeta = commandManager.metaBuilder("reply")
            .aliases("r")
            .build();
        commandManager.register(replyMeta, new ReplyCommand(proxyServer, configManager));
        
        // /gcreload command
        CommandMeta reloadMeta = commandManager.metaBuilder("gcreload")
            .build();
        commandManager.register(reloadMeta, new ReloadCommand(configManager));

        logger.info("GlobalChat plugin enabled!");
        logger.info("Global Chat: " + (configManager.isGlobalChatEnabled() ? "enabled" : "disabled"));
        logger.info("Blacklisted servers: " + configManager.getBlacklistedServers());
        logger.info("Commands registered: /msg, /reply, /gcreload");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
