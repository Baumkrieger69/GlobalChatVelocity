# GlobalChat Configuration Guide (German)

Diese Datei erklärt alle Konfigurationsoptionen und Platzhalter für das GlobalChat Plugin.

## Dateiort
```
plugins/globalchat/config.yml
```

Die Konfiguration wird automatisch beim ersten Start erstellt.

---

## 1. Global Chat Konfiguration

```yaml
global-chat:
  enabled: true  # Aktiviert/Deaktiviert den Global Chat
  format: '<#87CEFA><player><#D3D3D3> » <#FFFFFF><message>'
```

### Verfügbare Platzhalter:
- `<player>` - Der Name des Spielers, der die Nachricht schreibt
- `<message>` - Der Nachrichteninhalt
- `<server>` - Der Name des Servers, auf dem der Spieler ist (optional)

### Beispiele:
```yaml
# Einfaches Format
format: '<player>: <message>'

# Mit Farben
format: '<#87CEFA><player><#D3D3D3> » <#FFFFFF><message>'

# Mit Farbverlauf und Bold
format: '<gradient:#4A9FD8:#FFFFE0><bold>[Global]</bold></gradient> <#D3D3D3><player>: <#FFFFFF><message>'

# Mit Server-Anzeige
format: '<#87CEFA><player><#D3D3D3> [<server>]: <#FFFFFF><message>'
```

---

## 2. Private Nachrichten Konfiguration

```yaml
private-message:
  format-sender: '<#D3D3D3>[<#C7FFD8>Du <#D3D3D3>-> <#87CEFA><recipient><#D3D3D3>] <#F8F8FF><message>'
  format-receiver: '<#D3D3D3>[<#87CEFA><sender> <#D3D3D3>-> <#C7FFD8>Du<#D3D3D3>] [<server>] <#F8F8FF><message>'
  socialspy-format: '<#555555>[<#C7FFD8>SS<#555555>] <#AAAAAA><sender> <#555555>-> <#AAAAAA><recipient><#555555>: <#FFFFFF><message>'
```

### Verfügbare Platzhalter:
- `<sender>` - Der Absender der Nachricht
- `<recipient>` - Der Empfänger der Nachricht
- `<message>` - Der Nachrichteninhalt
- `<server>` - Der Server des Absenders
- `<reason>` - Der Grund (nur in Away-Meldungen)

### Erklärung:
- **format-sender**: Die Nachricht, die der Sender sieht (nach dem Senden)
- **format-receiver**: Die Nachricht, die der Empfänger sieht (Eingang)
- **socialspy-format**: Format für Admin-SocialSpy Nachrichten

### Beispiele:
```yaml
# Einfaches Format
format-sender: 'Du -> <recipient>: <message>'
format-receiver: '<sender> -> Du: <message>'

# Mit Servern
format-sender: '[Du -> <recipient> auf <server>]: <message>'
format-receiver: '[<sender> von <server> -> Dir]: <message>'

# Fancy mit Farben
format-sender: '<#00AA00>Du<#00FF00> → <#00AAFF><recipient><#00FF00>: <#FFFFFF><message>'
format-receiver: '<#FF5555><sender><#FF00FF> → <#00AA00>Dir<#FF00FF>: <#FFFFFF><message>'
```

---

## 3. Away Status Konfiguration

```yaml
away:
  format: '<#FFD700><player> ist gerade nicht verfügbar: <reason>'
  default-reason: 'AFK'  # Standardgrund wenn kein Grund angegeben
```

### Verfügbare Platzhalter:
- `<player>` - Der Name des Spielers
- `<reason>` - Der Grund, warum der Spieler weg ist

### Beispiele:
```yaml
# Einfach
format: '<player> ist weg: <reason>'

# Mit Farben
format: '<#FFD700><player><#FFFFFF> ist nicht verfügbar: <#FF6B6B><reason>'

# Fancy
format: '<gradient:#FF0000:#FFFF00><player></gradient> ist gerade weg: <#AAAAAA><reason>'

# Standardgründe
default-reason: 'AFK'          # Weg
default-reason: 'Pausen'       # Pause
default-reason: 'Im Shop'      # Beim Einkaufen
```

---

## 5. Prefixes & Suffixes (LuckPerms Integration)

```yaml
prefixes:
  use-luckperms: true          # LuckPerms aktivieren?
  fallback-prefix: '<#808080>' # Fallback wenn keine Rolle
  fallback-suffix: ''          # Fallback Suffix
```

### Hinweise:
- Wenn `use-luckperms: false`, werden nur die Fallback-Werte verwendet
- Dies wird derzeit hauptsächlich für zukünftige Erweiterungen vorgesehen

---

## 6. Messages Konfiguration

Alle Nachrichten-Texte, die dem Spieler angezeigt werden.

```yaml
messages:
  # Allgemeine Fehlermeldungen
  no-permission: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Du hast keine Berechtigung dafür!'
  
  # Verwendungshinweise
  usage-msg: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Verwendung: /msg <Spieler> <Nachricht>'
  usage-reply: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Verwendung: /reply <Nachricht>'
  usage-away: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Verwendung: /away <Grund>'
  usage-ignore: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Verwendung: /ignore <Spieler>'
  
  # Private Nachrichten
  no-reply-target: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Du hast niemanden, dem du antworten kannst!'
  player-not-online: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Der Spieler <player> ist nicht online!'
  player-blacklisted: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Der Spieler <player> befindet sich auf einem deaktivierten Server!'
  player-ignored: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Der Spieler <player> hat dich ignoriert!'
  
  # Away Status
  away-enabled: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Du bist jetzt weg: <reason>'
  away-disabled: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Willkommen zurück!'
  player-away: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Der Spieler <player> ist gerade nicht verfügbar: <reason>'
  
  # Ignore System
  ignore-added: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700><player> wurde ignoriert!'
  ignore-removed: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700><player> wurde nicht mehr ignoriert!'
  
  # SocialSpy
  socialspy-enabled: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>SocialSpy wurde <green>aktiviert<#808080>!'
  socialspy-disabled: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>SocialSpy wurde <red>deaktiviert<#808080>!'
```

### Verfügbare Platzhalter (je nach Kontext):
- `<player>` - Spielername
- `<reason>` - Grund/Nachricht
- `<sender>` - Absender
- `<recipient>` - Empfänger
- `<message>` - Nachrichteninhalt
- `<server>` - Server-Name

---

## 6. Blacklist Konfiguration

```yaml
blacklist:
  servers:
    - "lobby"      # Beispiel: Server nicht im Global Chat
    - "creative"   # Weitere Server...
```

### Was ist die Blacklist?
- Server auf dieser Liste können **nicht** am Global Chat teilnehmen
- Private Messages zu Spielern auf Blacklist werden **blockiert**
- Diese Server erhalten auch keine Global Chat Nachrichten

### Beispiele:
```yaml
# Leere Blacklist (alle Server im Global Chat)
blacklist:
  servers: []

# Mit Blacklist
blacklist:
  servers:
    - "lobby"
    - "hub"
    - "spawn"
    - "tutorial"
```

---

## MiniMessage Farb-Formate

Das Plugin verwendet das **MiniMessage** Format für Farben.

### Hex-Farben
```
<#87CEFA>Text  # Hellblau
<#D3D3D3>Text  # Grau
<#FFFFFF>Text  # Weiß
<#FFD700>Text  # Gold
```

### Named Colors
```
<red>Text
<green>Text
<blue>Text
<yellow>Text
<gray>Text
<gold>Text
```

### Farbverläufe
```
<gradient:#4A9FD8:#FFFFE0>Text</gradient>
<gradient:#FF0000:#00FF00>Rainbow</gradient>
```

### Formatierung
```
<bold>Bold Text</bold>
<italic>Italic Text</italic>
<underline>Underlined Text</underline>
<strikethrough>Strike Text</strikethrough>
```

### Kombinationen
```
<bold><#FFD700>Gold Bold</#FFD700></bold>
<gradient:#FF0000:#00FF00><bold>Rainbow Bold</bold></gradient>
```

---

## Komplette Config-Beispiele

### Minimalistisch
```yaml
global-chat:
  enabled: true
  format: '<player>: <message>'

private-message:
  format-sender: 'Du -> <recipient>: <message>'
  format-receiver: '<sender> -> Dir: <message>'
  socialspy-format: '[SS] <sender> -> <recipient>: <message>'

messages:
  no-permission: 'Du hast keine Berechtigung!'
  player-not-online: '<player> ist nicht online!'
  player-blacklisted: '<player> ist auf einem deaktivierten Server!'
  player-ignored: '<player> hat dich ignoriert!'

blacklist:
  servers: []
```

### Fancy mit Farben
```yaml
global-chat:
  enabled: true
  format: '<gradient:#4A9FD8:#FFFFE0><bold>[Global]</bold></gradient> <#D3D3D3><player>: <#FFFFFF><message>'

private-message:
  format-sender: '<#00AA00>Du<#00FF00> → <#00AAFF><recipient>: <#FFFFFF><message>'
  format-receiver: '<#FF5555><sender><#FF00FF> → <#00AA00>Dir: <#FFFFFF><message>'
  socialspy-format: '<#555555>[<#C7FFD8>SS<#555555>] <#AAAAAA><sender><#555555>-><#AAAAAA><recipient><#555555>: <#FFFFFF><message>'

messages:
  no-permission: '<gradient:#FF0000:#FFFF00><bold>❌ Keine Berechtigung!</bold></gradient>'
  player-not-online: '<#FFD700>⚠️ <player> ist nicht online!'
  player-blacklisted: '<#FF6B6B>❌ <player> ist auf einem deaktivierten Server!'
  player-ignored: '<#FF6B6B>❌ <player> hat dich ignoriert!'
  away-enabled: '<#FFD700>✓ Du bist weg: <reason>'
  away-disabled: '<#00FF00>✓ Willkommen zurück!'

blacklist:
  servers:
    - "lobby"
    - "hub"
```

---

## Fehlerbehebung

### Farben funktionieren nicht
- Stelle sicher, dass der Client MiniMessage unterstützt (sollte auf modernen Servern der Fall sein)
- Überprüfe die Syntax: `<#RRGGBB>` (6 Hex-Zeichen)

### Platzhalter werden nicht ersetzt
- Prüfe, dass der Platzhalter für den jeweiligen Kontext verfügbar ist
- `<server>` ist z.B. nur in Private Messages verfügbar

### Nachrichten sehen komisch aus
- Überprüfe auf Typos in der Config
- Stelle sicher, dass alles in Quotes steht wenn nötig
- Verwende `\n` für Zeilenumbrüche

---

**Stand:** 1.0.0
**Autor:** Baumkrieger69
