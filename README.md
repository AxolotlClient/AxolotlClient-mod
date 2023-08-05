# Axolotl - A Comprehensive Minecraft Client

[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0) 

## Introduction

AxolotlClient-mod is a nearly complete mod for Minecraft that offers a multitude of features across different game versions.

This README is also available in [Türkçe](https://github.com/AxolotlClient/AxolotlClient-mod/blob/multiversion/doc/README-tr.md) and [Deutsch](https://github.com/AxolotlClient/AxolotlClient-mod/blob/multiversion/doc/README-de.md).

## Features

AxolotlClient-mod provides a wide range of features including but not limited to:

- Custom Skies Implementation
- Freelook (disabled on some servers)
- Nametag, Beacon Beams, Dynamic FOV
- Low Fire / Low Shield, Hit Color
- Screenshot Utils, Zoom
- Various Hud Modules (port of KronHUD)
- Hypixel Features (AutoGG / GF / GLHF, LevelHead, Nick Hider, etc.)
- Custom Block Outlines
- Time Changer, Fullbright, Motion Blur
- TNT Time, Scrollable Tooltips, Particles
- Discord RPC, Custom Badges
- And many more!

Refer to the complete list in the Features section for more details.

## Links

For additional information and updates, visit the following links:

- [AxolotlClient Modrinth](https://modrinth.com/mod/axolotlclient)
- [AxolotlClient GitHub Releases](https://github.com/AxolotlClient/AxolotlClient-mod/releases)
- [AxolotlClient Discord](https://discord.gg/WyMjeX3vka)

## Contributing

Contributions to AxolotlClient-mod are welcomed. Feel free to join the Discord to discuss new features or open a pull request with your feature or a new/updated translation. Please document your changes in CHANGELOG.md for inclusion in the next version's changelog.

## Building

To build the project, use the command `./gradlew build`. Use `-Paxolotlclient.modules.<version_name>=true` to add a version to the build or `-Paxolotlclient.modules.all=true` to build all versions.

## Licensing

AxolotlClient-mod is licensed under the LGPL-3.0 License. See the [LICENSE](https://github.com/AxolotlClient/AxolotlClient-mod/blob/main/LICENSE) file for more information.

## API Requirements

Before downloading, please also download the respective API package for your version:

- 1.8.9 requires Legacy Fabric API
- 1.16.x requires Fabric API (necessary parts shipped with the mod)
- 1.19+ requires QSL (necessary parts shipped with the mod)

## Our Mission

Our goal is to create a better environment for everyone, including you, the user. With AxolotlClient, you decide what to use and what not. Every aspect of the mod is toggleable with over 200 options. We believe in transparency and user empowerment.

## Disclaimers

Please note, we do not endorse nor support cheats/hacks. If you believe any options packaged by this mod could be considered as such, let us know and we'll address the concern.

The only data sent to our server is your public Minecraft account UUID. It is not stored longer than one game session.

## Credits

AxolotlClient-mod wouldn't be possible without the open-source contributions of many, including DarkKronicle, the creator of KronHUD on which our hud modules are based, and AMereBagatelle, the author of the wonderful FabricSkyBoxes mod.

Enjoy AxolotlClient-mod, your customizable alternative to popular PvP clients!
