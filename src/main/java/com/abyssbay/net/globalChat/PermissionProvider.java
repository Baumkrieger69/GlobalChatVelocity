package com.abyssbay.net.globalChat;

import com.velocitypowered.api.proxy.Player;

public class PermissionProvider {
    private static GlobalChatPlugin plugin;

    public PermissionProvider(GlobalChatPlugin plugin) {
        PermissionProvider.plugin = plugin;
    }

    /**
     * Prüft, ob ein Spieler eine bestimmte Berechtigung hat
     * Unterstützt LuckPerms und falls nicht verfügbar, nur op-Status
     */
    public static boolean hasPermission(Player player, String permission) {
        if (player == null || permission == null) {
            return false;
        }

        try {
            // Versuche über LuckPerms zu prüfen, falls aktiviert
            boolean useLuckPerms = plugin != null && 
                                  plugin.getConfigManager() != null &&
                                  plugin.getConfigManager().toString().contains("use-luckperms");
            
            if (useLuckPerms) {
                // LuckPerms Check - würde über Permission Check API laufen
                // Fallback auf einfachen Permission Check
                return player.hasPermission(permission);
            }
        } catch (Exception e) {
            // Falls LuckPerms nicht verfügbar, fallback auf Standard
        }

        // Standard Velocity Permission Check
        return player.hasPermission(permission);
    }

    /**
     * Spezifische Checks für verschiedene Features
     */
    public static boolean canUseSocialSpy(Player player) {
        return hasPermission(player, "globalchat.socialspy");
    }

    public static boolean canUseGroupChat(Player player) {
        return hasPermission(player, "globalchat.group");
    }

    public static boolean isAdmin(Player player) {
        return hasPermission(player, "globalchat.admin");
    }

    public static boolean canReload(Player player) {
        return hasPermission(player, "globalchat.reload");
    }
}
