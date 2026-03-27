<p align="center">
    <img src="https://raw.githubusercontent.com/BuiltBrokenModding/MFFS_Classic/9bd18609f2dd87c20bd2fefba639254a425afbe4/src/main/resources/logo.png" alt="Logo" width="50%">
</p>

## About

**Modular Force Field System** (or MFFS) is a mod that adds force fields, high tech machinery and defensive measures to Minecraft.
Ever tired of nuclear explosions blowing up your house or want to keep people out of your secret bases?
**May the force (fields) be with you!**

This is a backport of MFFS v3.0+ which itself is a rewrite inspired by ThunderDark's MFFS mod. The foundation and reference for this version was the 1.21/1.20 Forge/NeoForge versions, however since this is essentially a seperate effort, neither this version or upstream versions are expected to have parody.

*This backport heavily utilized AI tools, but was thoroughly tested, and in some cases where the reference code used features that do not exist in minecraft 1.12.2, additional effort and features were added.*

## Usage

MFFS uses Fortron as its main power source for force fields. It contains a series of machines focused on converting energy to Forton for the use in generating force fields. The primary machine is the Force Field Projector which is powered by Fortron Capacitors (storage) and Coercion Derivers (generators).

The projector can be modified with various cards and upgrades that improve its stats. These upgrades include several field shapes, size scaling, position offset, and utility modules. For example, the upgrade to shock attacks, kill monsters, remove blocks, and protect tiles.

## Differences
- Lighting -- The upstream mod uses the updated lighting system to defer updates, on 1.12.2 the lighting has always been known as being heavy and laggy, to address this we slimmed down the scope for the glow module to only light force fields making contact with physical blocks (by default) and process these updates client side in a queue.
- Configurable -- We've made extra options for server owners to balance and tweak, such as easily disabling the steel ingot recipes, adding custom cataylsts, and changing module costs, effects, and even max stack sizes.
- Transitions -- Block translation difference checks. We avoid block costly block updates which do not need to be updated by comparing a difference between old and updated projections.
- Safety -- We've added simple safety measures to prevent base owners from locking themselves out of their base, wasting time of server OPs to fix their mistake. Hostile and restrictive features require active biometrics, ensuring at least someone has the keys to the kingdom.
- Feedback -- Instead of chat prints for getting close to an interdiction zone, we print a on screen message with a distance, so you have a better idea where not to go. Cached and calculated on the client!
- Interdiction Drop Collection -- With different configuration modes, by default a collection module is needed to get mob drops from interdiction mob kills, this is more performant for servers to not drop items on the ground.
- Commands -- A feature hopefully you never need, /mffs command that allows OPs to clean up orphan force fields that would otherwise be very hard to remove. Better to have and not need.
- Improved Tooltips -- While the mod still includes a Patchouli manual, we also add extra information via tooltips. Such as displaying a blank ID as "Everyone" as it functions as, or what some slots accept (i.e catalysts).
- Interdiction Damage -- Instead of running effectively a /kill on the target, we apply a damage value, based on the stack size of the module.
- Visuals -- Optional codechicken lib integration that applies some fancy glows on the machines.
- Performant -- The mod has been refactored in many ways to save resources, do as little on thread work as possible, cache as much as possible, and offload to client when possible. It should look cool, be functional, but be quiet on a performance profile scan.

### Contributing

Contributions are welcome, however if you use AI to code your changes, we expect you to pay attention to the edits it makes and to test everything it touches before submitting it. AI isn't a magic bullet, it messes stuff up all the time.

### Credits

**1.12.2 Port** - Rsslone

Block highlighting render code - [DarkKronicle's BetterBlockOutline renderer](https://github.com/DarkKronicle/BetterBlockOutline)

#### Past Developers

**Project Lead Developer** - Calclavia  
**Code** - Thutmose, Briman  
**Art** - Comply_cat_Ed, Sweet Walrus, mousecop, mr_hazard  
**Original mod by** Thunderdark  

## TemplateDevEnv

Utilizes Cleanroom's template workspace for modding Minecraft 1.12.2. Licensed under MIT, it is made for public use.

Runs on **Java 25**, **Gradle 9.2.1** + **[RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) 2.0.2** + **Forge 14.23.5.2847**.

With **coremod and mixin support** that is easy to configure.
