# Audio Asset Requirements (Production)

All assets must be legally sourced for commercial mobile release.

## Placement and format

- **Folder:** `app/src/main/res/raw/`
- **Preferred format:** `OGG Vorbis` (or AAC/MP3 if already licensed)
- **Sample rate:** `44.1 kHz`
- **Channels:** SFX mono/stereo, music stereo
- **Loudness target:** normalized and consistent across events

## Runtime naming convention

The app resolves resources from enum names in lowercase, so filenames should match these names.

## Required SFX assets

| Filename (without extension) | Event purpose | Recommended duration | Loop |
|---|---|---:|---|
| `button_click` | UI tap/buttons | 50–150 ms | No |
| `tile_select` | Tile selection feedback | 50–120 ms | No |
| `swap_valid` | Valid swap accepted | 80–180 ms | No |
| `swap_invalid` | Invalid swap rejection | 80–180 ms | No |
| `match_small` | Standard match | 100–250 ms | No |
| `match_large` | Large/special match | 150–350 ms | No |
| `cascade` | Chain/cascade feedback | 120–300 ms | No |
| `coin_collect` | Coin reward | 120–250 ms | No |
| `star_earned` | Star reward | 150–300 ms | No |
| `xp_gained` | XP gain | 120–250 ms | No |
| `level_unlock` | Level unlocked | 200–500 ms | No |
| `country_unlock` | Country unlocked | 300–800 ms | No |
| `victory` | Generic victory stinger | 600–1800 ms | No |
| `defeat` | Defeat stinger | 500–1500 ms | No |
| `italy_victory` | Country-specific victory | 600–1800 ms | No |
| `japan_victory` | Country-specific victory | 600–1800 ms | No |
| `mexico_victory` | Country-specific victory | 600–1800 ms | No |

## Required music assets

| Filename (without extension) | Screen/country usage | Recommended duration | Loop |
|---|---|---:|---|
| `world_map` | Map and hub background | 45–120 s | Yes |
| `germany_theme` | Germany gameplay | 45–120 s | Yes |
| `italy_theme` | Italy gameplay | 45–120 s | Yes |
| `france_theme` | France gameplay | 45–120 s | Yes |
| `japan_theme` | Japan gameplay | 45–120 s | Yes |
| `mexico_theme` | Mexico gameplay | 45–120 s | Yes |

## Delivery checklist for asset integration

1. File names exactly match table names.
2. Files are added under `res/raw`.
3. Loudness is balanced between SFX and music.
4. Start/end points are click-free for looped tracks.
