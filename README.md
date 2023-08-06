# Axolotl Client

This README is also available in [Türkçe](https://github.com/AxolotlClient/AxolotlClient-mod/blob/multiversion/doc/README-tr.md) and [Deutsch](https://github.com/AxolotlClient/AxolotlClient-mod/blob/multiversion/doc/README-de.md).

## Introduction

Axolotl  is a Client for Minecraft that introduces a range of features across various game versions, promoting a better, customizable environment for the user. 

## Features


Axolotl Client offers a multitude of features including:

- Custom Skies Implementation
- Freelook (Disabled on some servers)
- Nametag
- Beacon Beams
- Dynamic FOV
- Low Fire / Low Shield
- Hit Color
- Screenshot Utils
- Zoom
- Various Hud Modules (port of KronHUD, but with additions)
    - Including, but not limited to:
        - Ping
        - FPS
        - CPS
        - Armor
        - Potions
        - Keystrokes
        - ToggleModifiers
        - Server IP
        - Icon
        - Speed
        - Scoreboard
        - Crosshair
        - Coordinates
        - ActionBar
        - BossBar
        - Arrow
        - Item Update
        - Pack Display
        - Real Time
        - Reach
        - Hotbar
        - Memory
        - PlayerCount
        - Compass
        - TPS (Ticks per second)
        - Combo
        - Player
        - Chat
- Hypixel Features
    - AutoGG / GF / GLHF
    - LevelHead
    - Nick Hider
    - Skyblock
    - AutoTip
    - AutoBoop
- Custom Block Outlines
- Time Changer
- Fullbright
- Motion Blur
- TNT Time
- Scrollable Tooltips
- Particles
- Discord RPC
- Custom Badges

## Links

For more details and updates, visit the links below:

[![Modrinth](https://camo.githubusercontent.com/cbc928a24d8bfc17acc4dd4600e6b651e47d8106e9969f53cf5def874df1c95f/68747470733a2f2f63646e2e6a7364656c6976722e6e65742f6e706d2f40696e746572677261762f646576696e732d62616467657340322f6173736574732f636f7a792f617661696c61626c652f6d6f6472696e74685f3634682e706e67)](https://modrinth.com/mod/axolotlclient)
[![Github](https://camo.githubusercontent.com/b2b212fcee6a4bd63c24ebcb88087fd64b23e1c4e76bff1ec2b1bc9b1b70ebbc/68747470733a2f2f63646e2e6a7364656c6976722e6e65742f6e706d2f40696e746572677261762f646576696e732d62616467657340322f6173736574732f636f7a792f617661696c61626c652f6769746875625f3634682e706e67)](https://github.com/AxolotlClient/AxolotlClient-mod/releases)
[![Discord](https://camo.githubusercontent.com/1170b09fbb3ad106a8297b881d54adae5ec106729986ee560d0babcff15560e4/68747470733a2f2f63646e2e6a7364656c6976722e6e65742f6e706d2f40696e746572677261762f646576696e732d62616467657340332f6173736574732f636f7a792f736f6369616c2f646973636f72642d706c7572616c5f3634682e706e67)](https://discord.gg/WyMjeX3vka)

## Contributing

We welcome all contributions to Axolotl Client. Join our Discord community to discuss new features or open a pull request if you have a feature or a new/updated translation to share. Remember to note your changes in `CHANGELOG.md` for inclusion in the next version's changelog.

## Building from Source

For those interested in the most current version or building from the source directly, follow the instructions below:

### Basic Build

To build the project, use the following command:

```bash
./gradlew build
```

### Building Specific Versions

If you wish to build a specific version, use:

```bash
./gradlew build -Paxolotlclient.modules.<version_name>=true
```

Replace `<version_name>` with the desired version name.

### Building All Versions

To build all available versions at once, use:

```bash
./gradlew build -Paxolotlclient.modules.all=true
```

### IDE Tips

For those using an Integrated Development Environment (IDE), you can add the respective properties mentioned above to the `gradle.properties` files or remove them as required.

## API Requirements

Ensure to download the respective API package for your version before you download:

- 1.8.9: Legacy Fabric API
- 1.16.x: Fabric API (necessary parts are shipped with the mod)
- 1.19+: QSL (necessary parts are shipped with the mod)

## Disclaimers

We do not endorse or support cheats/hacks. If you believe any options packaged by this mod could be considered as such, please let us know.

The only data sent to our server is your public Minecraft account UUID, which isn't stored longer than one game session.

## Credits

Our heartfelt thanks to the open-source contributors, particularly DarkKronicle, the creator of KronHUD, and AMereBagatelle, the author of the FabricSkyBoxes mod. Without their contributions, Axolotl Client would not be as comprehensive as it is.

## License
![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)

Axolotl Client is licensed under the LGPL-3.0 License. For more details, see the [LICENSE](https://github.com/AxolotlClient/AxolotlClient-mod/blob/main/LICENSE) file. 
