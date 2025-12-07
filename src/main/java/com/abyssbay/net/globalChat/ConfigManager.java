package com.abyssbay.net.globalChat;

import java.util.*;
import java.nio.file.*;
import java.io.IOException;

public class ConfigManager {
    private Set<String> blacklistedServers;
    private Path configPath;
    private Map<String, String> serverTags; // Server name -> Tag (e.g., "cb" -> "[CB]")
    
    // Config values
    private boolean globalChatEnabled;
    private String globalChatFormat;
    private String privateMsgFormatSender;
    private String privateMsgFormatReceiver;
    private String socialspyFormat;
    private String awayFormat;
    private String awayDefaultReason;
    private boolean useLuckperms;
    private String fallbackPrefix;
    private String fallbackSuffix;
    private Map<String, String> messages;
    
    // Discord Webhook values
    private String discordWebhookUrl;
    private String discordBotName;
    private String discordBotAvatar;

    public ConfigManager(Path configPath) {
        this.configPath = configPath;
        this.blacklistedServers = new HashSet<>();
        this.serverTags = new HashMap<>();
        this.messages = new HashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        try {
            if (Files.exists(configPath)) {
                String content = Files.readString(configPath);
                parseYaml(content);
            } else {
                createDefaultConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseYaml(String content) {
        String[] lines = content.split("\n");
        String currentSection = "";
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            // Skip empty lines and comments
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            
            // Section detection - lines that end with : and have no indent
            if (trimmed.endsWith(":") && !line.startsWith(" ") && !line.startsWith("\t")) {
                currentSection = trimmed.replace(":", "").toLowerCase();
                continue;
            }
            
            // Key-value parsing - must have indentation (2 spaces)
            if (trimmed.contains(":") && (line.startsWith("  ") && !line.startsWith("   "))) {
                String[] parts = trimmed.split(":", 2);
                String key = parts[0].trim();
                String value = parts.length > 1 ? parts[1].trim() : "";
                
                switch (currentSection) {
                    case "global-chat":
                        parseGlobalChat(key, value);
                        break;
                    case "private-message":
                        parsePrivateMessage(key, value);
                        break;
                    case "away":
                        parseAway(key, value);
                        break;
                    case "prefixes":
                        parsePrefixes(key, value);
                        break;
                    case "messages":
                        messages.put(key, value);
                        break;
                    case "discord":
                        parseDiscord(key, value);
                        break;
                    case "blacklist":
                        if (key.equals("servers") && value.startsWith("[")) {
                            parseBlacklist(value);
                        }
                        break;
                    case "server-tags":
                        // Parse server tags: cb: '[CB]'
                        String tagValue = value.replaceAll("^['\"]|['\"]$", "");
                        serverTags.put(key.toLowerCase(), tagValue);
                        break;
                }
            }
        }
    }

    private void parseGlobalChat(String key, String value) {
        switch (key) {
            case "enabled":
                globalChatEnabled = value.equalsIgnoreCase("true");
                break;
            case "format":
                globalChatFormat = value.replaceAll("^['\"]|['\"]$", "");
                break;
        }
    }

    private void parsePrivateMessage(String key, String value) {
        String cleanValue = value.replaceAll("^['\"]|['\"]$", "");
        switch (key) {
            case "format-sender":
                privateMsgFormatSender = cleanValue;
                break;
            case "format-receiver":
                privateMsgFormatReceiver = cleanValue;
                break;
            case "socialspy-format":
                socialspyFormat = cleanValue;
                break;
        }
    }

    private void parseAway(String key, String value) {
        String cleanValue = value.replaceAll("^['\"]|['\"]$", "");
        switch (key) {
            case "format":
                awayFormat = cleanValue;
                break;
            case "default-reason":
                awayDefaultReason = cleanValue;
                break;
        }
    }

    private void parseDiscord(String key, String value) {
        String cleanValue = value.replaceAll("^['\"]|['\"]$", "");
        switch (key) {
            case "webhook-url":
                discordWebhookUrl = cleanValue;
                break;
            case "bot-name":
                discordBotName = cleanValue;
                break;
            case "bot-avatar":
                discordBotAvatar = cleanValue;
                break;
        }
    }

    private void parsePrefixes(String key, String value) {
        String cleanValue = value.replaceAll("^['\"]|['\"]$", "");
        switch (key) {
            case "use-luckperms":
                useLuckperms = cleanValue.equalsIgnoreCase("true");
                break;
            case "fallback-prefix":
                fallbackPrefix = cleanValue;
                break;
            case "fallback-suffix":
                fallbackSuffix = cleanValue;
                break;
        }
    }

    private void parseBlacklist(String value) {
        String cleaned = value.replaceAll("[\\[\\]\"\\s]", "");
        String[] servers = cleaned.split(",");
        for (String server : servers) {
            String serverName = server.trim();
            if (!serverName.isEmpty()) {
                blacklistedServers.add(serverName.toLowerCase());
            }
        }
    }

    private void createDefaultConfig() {
        String defaultConfig = """
            # ===== GLOBALE CHAT KONFIGURATION =====
            # Platzhalter:
            #   <player> - Der Name des Spielers der schreibt
            #   <message> - Der Nachrichteninhalt
            #   <server> - Der Server des Spielers
            global-chat:
              enabled: true
              format: <#87CEFA><player><#D3D3D3> » <#FFFFFF><message>

            # ===== PRIVATE NACHRICHTEN KONFIGURATION =====
            # Platzhalter:
            #   <sender> - Der Name des Absenders
            #   <recipient> - Der Name des Empfängers
            #   <message> - Der Nachrichteninhalt
            #   <server> - Der Server des Absenders
            private-message:
              format-sender: <#D3D3D3>[<#C7FFD8>Du <#D3D3D3>-> <#87CEFA><recipient><#D3D3D3>] <#F8F8FF><message>
              format-receiver: <#D3D3D3>[<#87CEFA><sender> <#D3D3D3>-> <#C7FFD8>Du<#D3D3D3>] [<server>] <#F8F8FF><message>
              socialspy-format: <#555555>[<#C7FFD8>SS<#555555>] <#AAAAAA><sender> <#555555>-> <#AAAAAA><recipient><#555555>: <#FFFFFF><message>

            # ===== AWAY STATUS KONFIGURATION =====
            # Platzhalter:
            #   <player> - Der Name des Spielers
            #   <reason> - Der Grund warum der Spieler weg ist
            away:
              format: <#FFD700><player> ist gerade nicht verfügbar: <reason>
              default-reason: AFK

            # ===== PRÄFIXE UND SUFFIXE =====
            prefixes:
              use-luckperms: true
              fallback-prefix: <#808080>
              fallback-suffix: 

            # ===== ALLE NACHRICHTEN =====
            # Jede Nachricht kann verschiedene Platzhalter verwenden:
            #   <player> - Spielername
            #   <reason> - Grund/Begründung
            #   <sender> - Absender
            #   <recipient> - Empfänger
            #   <message> - Nachrichteninhalt
            #   <server> - Server-Name
            messages:
              # Allgemeine Fehlermeldungen
              no-permission: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Du hast keine Berechtigung dafür!
              
              # Verwendungshinweise
              usage-msg: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Verwendung: /msg <Spieler> <Nachricht>
              usage-reply: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Verwendung: /reply <Nachricht>
              usage-away: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Verwendung: /away <Grund> oder /away off
              usage-ignore: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Verwendung: /ignore <Spieler>
              
              # Private Nachrichten (Platzhalter: <player>)
              no-reply-target: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Du hast niemanden, dem du antworten kannst!
              player-not-online: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Der Spieler <player> ist nicht online!
              player-blacklisted: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Der Spieler <player> befindet sich auf einem deaktivierten Server!
              player-ignored: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Der Spieler <player> hat dich ignoriert!
              
              # Away Status (Platzhalter: <player>, <reason>)
              away-enabled: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Du bist jetzt weg: <reason>
              away-disabled: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Willkommen zurück!
              player-away: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Der Spieler <player> ist gerade nicht verfügbar: <reason>
              
              # Ignore System (Platzhalter: <player>)
              ignore-added: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700><player> wurde ignoriert!
              ignore-removed: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700><player> wurde nicht mehr ignoriert!
              
              # SocialSpy (Platzhalter: <sender>, <recipient>, <message>)
              socialspy-enabled: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>SocialSpy wurde <green>aktiviert<#808080>!
              socialspy-disabled: <gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>SocialSpy wurde <red>deaktiviert<#808080>!

            # ===== DISCORD WEBHOOK KONFIGURATION =====
            # Sende alle Private Messages zu einem Discord Webhook
            # Setze auf "none" um zu deaktivieren
            discord:
              webhook-url: "none"
              bot-name: "GlobalChat"
              bot-avatar: "https://cdn.discordapp.com/embed/avatars/0.png"

            # ===== BLACKLIST KONFIGURATION =====
            # Server auf dieser Liste können nicht am Global-Chat oder Private Messages teilnehmen
            blacklist:
              servers: []
            """;
        
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, defaultConfig);
            parseYaml(defaultConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBlacklisted(String serverName) {
        return blacklistedServers.contains(serverName.toLowerCase());
    }

    public Set<String> getBlacklistedServers() {
        return new HashSet<>(blacklistedServers);
    }

    public String getGlobalChatFormat() {
        return globalChatFormat != null ? globalChatFormat : "<player> » <message>";
    }

    public boolean isGlobalChatEnabled() {
        return globalChatEnabled;
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "");
    }

    public String getPrivateMsgFormatSender() {
        return privateMsgFormatSender != null ? privateMsgFormatSender : 
            "<#D3D3D3>[<#C7FFD8>Du <#D3D3D3>-> <#87CEFA><recipient><#D3D3D3>] <#F8F8FF><message>";
    }

    public String getPrivateMsgFormatReceiver() {
        return privateMsgFormatReceiver != null ? privateMsgFormatReceiver : 
            "<#D3D3D3>[<#87CEFA><sender> <#D3D3D3>-> <#C7FFD8>Du<#D3D3D3>] <#F8F8FF><message>";
    }

    public String getServerTag(String serverName) {
        return serverTags.getOrDefault(serverName.toLowerCase(), "");
    }

    public void reloadConfig() {
        blacklistedServers.clear();
        serverTags.clear();
        messages.clear();
        loadConfig();
    }

    // Away Getters
    public String getAwayFormat() {
        return awayFormat != null ? awayFormat : "<#FFD700><player> ist gerade nicht verfügbar: <reason>";
    }

    public String getAwayDefaultReason() {
        return awayDefaultReason != null ? awayDefaultReason : "AFK";
    }
    
    // Discord Webhook Getters
    public String getDiscordWebhookUrl() {
        return discordWebhookUrl != null ? discordWebhookUrl : "";
    }
    
    public String getDiscordBotName() {
        return discordBotName != null ? discordBotName : "GlobalChat";
    }
    
    public String getDiscordBotAvatar() {
        return discordBotAvatar != null ? discordBotAvatar : "";
    }
}

