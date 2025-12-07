package com.abyssbay.net.globalChat;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHistory {
    private static final int MAX_MESSAGES_PER_USER = 100;
    private static final ConcurrentHashMap<java.util.UUID, List<ChatMessage>> messageHistory = new ConcurrentHashMap<>();

    public static class ChatMessage {
        public final java.util.UUID sender;
        public final java.util.UUID recipient;
        public final String message;
        public final Instant timestamp;
        public final String server;

        public ChatMessage(java.util.UUID sender, java.util.UUID recipient, String message, String server) {
            this.sender = sender;
            this.recipient = recipient;
            this.message = message;
            this.timestamp = Instant.now();
            this.server = server;
        }
    }

    /**
     * Speichert eine neue Nachricht in der Historie
     */
    public static void addMessage(java.util.UUID sender, java.util.UUID recipient, String message, String server) {
        // Für Sender speichern
        messageHistory.computeIfAbsent(sender, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new ChatMessage(sender, recipient, message, server));

        // Für Recipient speichern
        messageHistory.computeIfAbsent(recipient, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new ChatMessage(sender, recipient, message, server));

        // Max messages pro User begrenzen
        trimHistory(sender);
        trimHistory(recipient);
    }

    /**
     * Gibt die letzten N Nachrichten für einen Spieler zurück
     */
    public static List<ChatMessage> getMessages(java.util.UUID playerUuid, int limit) {
        List<ChatMessage> allMessages = messageHistory.getOrDefault(playerUuid, new ArrayList<>());
        int startIndex = Math.max(0, allMessages.size() - limit);
        return new ArrayList<>(allMessages.subList(startIndex, allMessages.size()));
    }

    /**
     * Gibt alle Nachrichten für einen Spieler zurück
     */
    public static List<ChatMessage> getAllMessages(java.util.UUID playerUuid) {
        return new ArrayList<>(messageHistory.getOrDefault(playerUuid, new ArrayList<>()));
    }

    /**
     * Gibt die letzten 10 Nachrichten zurück (Standard)
     */
    public static List<ChatMessage> getRecentMessages(java.util.UUID playerUuid) {
        return getMessages(playerUuid, 10);
    }

    /**
     * Entfernt die ältesten Nachrichten, wenn das Limit überschritten wird
     */
    private static void trimHistory(java.util.UUID playerUuid) {
        List<ChatMessage> history = messageHistory.get(playerUuid);
        if (history != null && history.size() > MAX_MESSAGES_PER_USER) {
            synchronized (history) {
                while (history.size() > MAX_MESSAGES_PER_USER) {
                    history.remove(0);
                }
            }
        }
    }

    /**
     * Löscht alle Nachrichten für einen Spieler
     */
    public static void clearPlayerHistory(java.util.UUID playerUuid) {
        messageHistory.remove(playerUuid);
    }

    /**
     * Löscht alle Nachrichten (wird beim Neustart aufgerufen)
     */
    public static void clearAllHistory() {
        messageHistory.clear();
    }

    /**
     * Gibt die Anzahl der Nachrichten für einen Spieler zurück
     */
    public static int getMessageCount(java.util.UUID playerUuid) {
        return messageHistory.getOrDefault(playerUuid, new ArrayList<>()).size();
    }
}
