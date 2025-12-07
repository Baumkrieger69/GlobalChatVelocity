# GlobalChat Velocity Plugin - Features

Dieses Plugin bietet ein umfassendes Chat-System für Velocity Proxy-Server mit erweiterten Funktionen.

## Installierte Features

### 1. **Global Chat** (`globalChat`)
- Nachrichten von Spielern auf verschiedenen Servern sind server-übergreifend sichtbar
- Konfigurierbare Nachrichtenformate
- Blacklist für Server, die nicht am Global Chat teilnehmen

#### Platzhalter:
- `<player>` - Spielername
- `<message>` - Nachrichteninhalt
- `<server>` - Server-Name (wird vom Plugin gesetzt)

**Kommandos:** `/msg` (automatisch vom Minecraft-Server abgefangen)

---

### 2. **Private Nachrichten** (`/msg`, `/reply`)
- Spieler können sich direkt untereinander Nachrichten senden
- `/msg <Spieler> <Nachricht>` - Nachricht senden
- `/reply <Nachricht>` - Auf die letzte Nachricht antworten
- Unterstützung für Aliases: `/m`, `/pm`, `/message`, `/whisper`, `/r`
- Blockiert Nachrichten zu Spielern auf blacklistrierten Servern
- Überprüft den Ignore-Status des Empfängers
- Zeigt Away-Status des Empfängers (wenn aktiviert)

#### Platzhalter:
- `<sender>` - Sender der Nachricht
- `<recipient>` - Empfänger der Nachricht
- `<message>` - Nachrichteninhalt
- `<server>` - Server des Senders
- `<reason>` - Away-Grund (nur im Away-Status-Meldungen)

**Kommandos:** 
- `/msg <Spieler> <Nachricht>`
- `/reply <Nachricht>`

---

### 3. **Away Status** (`/away`)
- Spieler können sich als "weg" markieren
- Wird nur für Private Messages angezeigt
- Globale Chat-Nachrichten sind nicht betroffen
- `/away <Grund>` oder `/away` - Toggle mit Standardgrund
- `/back` - Alias für Rückkehr (oder `/away` nochmal drücken)

#### Konfiguration:
- `away.default-reason` - Standardgrund wenn kein Grund angegeben
- `away.format` - Nachrichtenformat für Away-Benachrichtigungen

**Kommandos:**
- `/away <Grund>` - Status setzen
- `/away` - Mit Standardgrund
- `/back` - Alias zum Deaktivieren

---

### 4. **Ignore System** (`/ignore`)
- Spieler können andere Spieler ignorieren
- Ignorierte Spieler können keine Private Messages senden
- `/ignore <Spieler>` - Toggle (ignorieren oder nicht mehr ignorieren)
- `/ignorelist` - Zeigt alle ignorierten Spieler

#### Konfiguration:
- `ignore-added` - Nachricht wenn jemand ignoriert wird
- `ignore-removed` - Nachricht wenn Ignore aufgehoben wird

**Kommandos:**
- `/ignore <Spieler>` - Spieler ignorieren/nicht ignorieren
- `/ignorelist` - Liste der ignorierten Spieler

---

### 5. **SocialSpy** (`/socialspy`)
- Admins können alle Private Messages von anderen Spielern sehen
- `/socialspy` oder `/ss` - Toggle SocialSpy an/aus
- Benötigt Permission: `globalchat.socialspy`
- Zeigt Sender, Empfänger und Nachrichteninhalt

#### Konfiguration:
- `private-message.socialspy-format` - Format für SocialSpy-Nachrichten

**Kommandos:**
- `/socialspy` - SocialSpy aktivieren/deaktivieren
- `/ss` - Alias für SocialSpy

---

### 6. **Message History**
- Alle Private Messages werden im Speicher gespeichert
- Max. 100 Nachrichten pro Spieler
- Wird automatisch beim Proxy-Neustart geleert
- Wird nicht persistent gespeichert (In-Memory)

**Automatisch integriert:** Alle Private Messages werden automatisch gespeichert.

---

### 7. **Permissions Provider** (`PermissionProvider`)
- Zentrale Verwaltung von Berechtigungen
- Unterstützt LuckPerms (falls aktiviert)
- Fallback auf Standard Velocity Permissions

#### Verfügbare Permissions:
- `globalchat.socialspy` - Erlaubt SocialSpy Nutzung
- `globalchat.admin` - Admin-Rechte
- `globalchat.reload` - Erlaubt /gcreload

---

## Konfigurationsdatei (config.yml)

Die Konfiguration wird automatisch erstellt unter: `plugins/globalchat/config.yml`

```yaml
# Global Chat
global-chat:
  enabled: true
  format: '<#87CEFA><player><#D3D3D3> » <#FFFFFF><message>'

# Private Messages
private-message:
  format-sender: '...'
  format-receiver: '...'
  socialspy-format: '...'

# Away Status
away:
  format: '...'
  default-reason: 'AFK'

# Prefixes (LuckPerms Integration)
prefixes:
  use-luckperms: true
  fallback-prefix: '<#808080>'
  fallback-suffix: ''

# Messages (Alle Nachrichten-Texte)
messages:
  no-permission: '...'
  usage-msg: '...'
  player-not-online: '...'
  # ... weitere Nachrichten

# Blacklist
blacklist:
  servers: []
```

---

## Verfügbare Platzhalter nach Feature

### Global Chat
- `<player>` - Spielername
- `<message>` - Nachrichteninhalt

### Private Messages
- `<sender>` - Absender
- `<recipient>` - Empfänger
- `<message>` - Nachrichteninhalt
- `<server>` - Server des Senders

### Away Status
- `<player>` - Spielername
- `<reason>` - Away-Grund

### SocialSpy
- `<sender>` - Absender
- `<recipient>` - Empfänger
- `<message>` - Nachrichteninhalt
- `<server>` - Server des Senders

---

## Befehle (Übersicht)

| Befehl | Aliases | Beschreibung |
|--------|---------|-------------|
| `/msg <Spieler> <Nachricht>` | `/m`, `/pm`, `/message`, `/whisper` | Private Nachricht senden |
| `/reply <Nachricht>` | `/r` | Auf letzte Nachricht antworten |
| `/away <Grund>` oder `/away off` | `/brb` | Away-Status setzen/entfernen |
| `/ignore <Spieler>` | - | Spieler ignorieren |
| `/socialspy` | `/ss` | SocialSpy toggle |
| `/gcreload` | - | Config neuladen (Console only) |

---

## Konfigurationsbeispiel

### Farben verwenden (MiniMessage Format)
```
format: '<#87CEFA><player><#D3D3D3> » <#FFFFFF><message>'
```

### Farbverläufe
```
format: '<gradient:#4A9FD8:#FFFFE0><bold>Abyssbay</bold></gradient> <#808080>| <#FFD700>Text'
```

### Bold/Kursiv/Unterstrichen
```
format: '<bold><player></bold> <italic><message></italic>'
```

---

## Logging

Das Plugin logged wichtige Aktionen:
- Away-Status Änderungen
- Ignore-Aktionen
- SocialSpy Aktivitäten
- /gcreload Ausführungen

---

## Entwickler-Informationen

### Statische Methoden für Integration

**AwayCommand:**
- `isAway(UUID playerUuid)` - Prüft ob Spieler weg ist
- `getAwayReason(UUID playerUuid)` - Grund abrufen
- `setAway(UUID playerUuid, String reason)` - Status setzen

**IgnoreCommand:**
- `isIgnoring(UUID ignoringPlayer, UUID targetPlayer)` - Prüft Ignore-Status
- `addIgnore(UUID ignoringPlayer, UUID targetPlayer)` - Ignorieren
- `removeIgnore(UUID ignoringPlayer, UUID targetPlayer)` - Nicht mehr ignorieren

**SocialSpyCommand:**
- `isSocialSpyEnabled(UUID playerUuid)` - Prüft SocialSpy Status
- `broadcastPrivateMessage(...)` - Broadcast an alle SocialSpy Nutzer

**MessageHistory:**
- `addMessage(UUID sender, UUID recipient, String message, String server)` - Nachricht speichern
- `getMessages(UUID playerUuid, int limit)` - Letzte N Nachrichten
- `getAllMessages(UUID playerUuid)` - Alle Nachrichten

---

## Version: 1.0.0
**Autor:** Baumkrieger69
