# Visual Identification (VisualID)

Lightweight server-side WAILA/Jade-style plugin for Paper. Shows players what block or entity they are looking at via the actionbar. No client mod required.

## Requirements

- Paper 1.21.4 (API 1.21.4-R0.1-SNAPSHOT)
- Java 21

## Build

```bash
./gradlew clean build
```

Output: `build/libs/VisualID-1.0.1.jar`

## Install

Copy the jar to your server's `plugins/` folder and restart. On first start, a `plugins/VisualID/config.yml` is created with defaults.

```bash
sudo cp build/libs/VisualID-*.jar /srv/minecraft/paper/plugins/
```

## Commands

| Command | Description | Permission |
|---|---|---|
| `/visual-id` | One-shot: show what you're looking at, held for 3× the normal interval | `visualid.use` |
| `/visual-id toggle` | Enable/disable your personal HUD | `visualid.toggle` |
| `/visual-id status` | Show HUD state and which sections are enabled | `visualid.use` |
| `/visual-id config <section> enable\|disable` | Toggle a display section and save | `visualid.reload` |
| `/visual-id reload` | Reload config from disk | `visualid.reload` |

### Display sections

| Section | What it shows |
|---|---|
| `name` | Pretty block/entity name |
| `key` | Namespaced ID (e.g. `minecraft:oak_log`) |
| `coordinates` | Block XYZ |
| `biome` | Biome name |
| `light` | Light level at the block |
| `health` | Entity current/max health with colour coding |

Example: `/visual-id config biome enable`

## Permissions

| Permission | Default | Description |
|---|---|---|
| `visualid.use` | true | Receive the HUD |
| `visualid.toggle` | true | Toggle own HUD on/off |
| `visualid.reload` | op | Reload config / toggle sections |

## Config

`plugins/VisualID/config.yml`:

```yaml
# Command keyword. Change this if another plugin already uses /visual-id.
# Requires a server restart to take effect.
command: visual-id

enabled-by-default: true
update-interval-ticks: 5
max-distance: 8.0
prefer-entities: true

show:
  material-name: true
  namespaced-key: true
  coordinates: true
  biome: false
  light-level: false
  entity-health: true
```

## Example output

Looking at a block:
```
Packed Ice  ·  minecraft:packed_ice  ·  12, 64, -8  ·  Minecraft
```

Looking at an entity:
```
Zombie  ·  minecraft:zombie  ·  ❤ 18/20  ·  Minecraft
```

## Development deploy (VS Code)

Copy `.env.example` to `.env` and fill in your server details, then use the **Build & Deploy VisualID to Server** run configuration or the **Build & Deploy** task.
