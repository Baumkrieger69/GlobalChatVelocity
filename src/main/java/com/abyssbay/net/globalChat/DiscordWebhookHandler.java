package com.abyssbay.net.globalChat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 * Sends private messages to Discord webhook for logging/monitoring
 */
public class DiscordWebhookHandler {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Sends a private message to Discord webhook
     */
    public static void sendPrivateMessage(String sender, String recipient, String message, 
                                         String senderServer, ConfigManager configManager) {
        String webhookUrl = configManager.getDiscordWebhookUrl();
        
        // Check if webhook is configured
        if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("none")) {
            return;
        }

        try {
            // Build Discord embed
            JSONObject embed = new JSONObject();
            embed.put("title", "Private Message");
            embed.put("description", message);
            embed.put("color", 3447003); // Blue color
            
            // Add fields
            JSONArray fields = new JSONArray();
            
            JSONObject senderField = new JSONObject();
            senderField.put("name", "Sender");
            senderField.put("value", sender);
            senderField.put("inline", true);
            fields.add(senderField);
            
            JSONObject recipientField = new JSONObject();
            recipientField.put("name", "Recipient");
            recipientField.put("value", recipient);
            recipientField.put("inline", true);
            fields.add(recipientField);
            
            JSONObject serverField = new JSONObject();
            serverField.put("name", "Server");
            serverField.put("value", senderServer);
            serverField.put("inline", true);
            fields.add(serverField);
            
            JSONObject timeField = new JSONObject();
            timeField.put("name", "Time");
            timeField.put("value", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            timeField.put("inline", true);
            fields.add(timeField);
            
            embed.put("fields", fields);
            embed.put("footer", new JSONObject().put("text", "GlobalChat Private Messages"));
            
            // Build webhook payload
            JSONObject payload = new JSONObject();
            payload.put("username", configManager.getDiscordBotName());
            payload.put("avatar_url", configManager.getDiscordBotAvatar());
            
            JSONArray embeds = new JSONArray();
            embeds.add(embed);
            payload.put("embeds", embeds);
            
            // Send to Discord
            sendWebhook(webhookUrl, payload.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends HTTP POST request to Discord webhook
     */
    private static void sendWebhook(String webhookUrl, String payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 204 && response.statusCode() != 200) {
                System.err.println("Discord webhook error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Error sending to Discord webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
