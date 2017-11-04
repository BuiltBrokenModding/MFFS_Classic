List of changes that need to be made to the mod

## Power
* Balance power cost
* Mod should be accessible mid game instead of end game
* Reactors should only be required for larger fields (200m radius+)
* Small should only require coal generators for power scale (10-20 coal generators as 40RF/t for 16 scale sphere field)
* Module cards should only cost power if they do work or modify the field
* Module cards should have a flat power cost unless they modify field behavior

## Configs
* Everything should have a config
* Server configs should be seperate from client configs
* All audio needs a config with GUI in game controls

## Power cards
* Sponge - power cost per block removed
* Camo - per block upkeep increase
* Disintegration - cost per block removed
* Glow - upkeep cost increase
* Projector types (cube, sphere, etc) - should all cost the same as long as the shape is simple
* Silence - no power cost by default, with config to enable power cost
* Stabilize - cost per block placed
* Invert - no cost, thing basicly spikes power cost by default
* Shock - power per attack


## Power grid
Right now the power systems work just like any other power system. We have generators, storage, inputs, and exports. This heavily reduces the effectiveness of the fields, limits power movement, and causes lag. To improve this we have two options. We can continue to use said system with tweaks to control behavior. OR, the system can be replaced with a graph of nodes that all act as one large machine. Effectively this would make the system highly passive with very few updates. 

How would this work? A network object would be created and machines would connect to the network. Each machine would supply a list of connections to other machines. The network object would then build a graph of inputs to outputs. Then map out how power flows through the network and is used by the network. 

The network would take full control over power balance and storage. If the network produces more power than used it will store power in each machine alonge the path. If the network is full it will go on silent mode until a change is made. 

How will a user handle this change? From the user point of view nothing should change. They will setup the machines the same way. The only difference will be the backend functionality. 

How will this be implemented? The first step will be to overhaul the existing functionality. Add in all the behavior changes to improve how the user can manage there network of connections. Once this is done then the network object can be created to start mapping connections and testing logic. After everything is shown to work then machine functionality can be replaced with network logic. 

## JSON port

* All Recipes need to be ported
* All registry calls need to be ported(Items, Blocks, entities, fluids, etc)

## Projector

* Convert all Vector3D used to IPos3d
* Convert all Vector3D creation to BlockPos for ints, Pos for doubles
* Store all Pos data as IPos3D
* Phase out grid-slot upgrade system for sliders
* Seperate upgrade inventories (field property cards vs machine upgrade cards)
* Change main GUI to show information only and simple controls (on/off)
* Add upgrade GUI
* Add settings GUI
* Add event GUI
* Add events to track field on/off 
* Add events to track attacks on field
* Add events to track changes to field
* Add max limits to field size
* Add graph to monitor power usage
* Add calculator GUI to predict field usage (blocks generated, cost for size, generators needed)

## Capacitor
* Replace grid/range implementation with something else
* Limit connections per capacitor, with config to ignore and upgrades to increase
* Increase range from 12 to a little higher to avoid using capactiors as relays
* Add a block to act as a relay as well
* Add a manual link system (Ex. Machine A -> Machine B)
* Add input/output controls
* Add input/output link settings (Output to A, Input from B)
* Add settings to control output levels (Ex. 10% to A, 90% to B)
* Add settings to control priority on self
* Add settings to control priority on outputs (Ex. A p1, B p2), should be used before self setting, off by default
* Change main GUI to show power and transfer rates
* Add settings GUI
* Add upgrade GUI
* Add info GUI
* Add details information (input rate, output rate, upgrade bonus applied)

## Missing content
* Add block mover
* Add custom module (with very limited setup or custom save format [nbt doesn't like a lot of data])

## New Content
* Add entity projector
* Create merged but weaker version of projector + fortron genertor (Used for simple doors)
* Add tiered versions of all tiles (limit abilities by tiers)
* Add remote control station to manage projectors over a large base
* Add redstone block to control fields remotely (allows for automated doors)
* Add block protector, can be a card or block (generates a protective field to prevent block breaking, only renders field when blocks are attacked)
* Add wall projector card (creates flat wall, for use in doors, GUI settings to change direction)
* Add box projector card (generates 4 walls, for use as a fence)

## Biometric Identifier
* Implement global permission system
* Phase ID cards out for GUI system

## Tiles 

* Phase out TileEntity extension for Node framework
* Phase out IFluidHandler for wrapper version
* Phase out IInventory for wrapper version
* Phase out power for wrapper version
* Phase out frequency system

## Cards

* Phase out unneeded cards
* Merged cards into one item

## Wiki

* Document everything
* Page per content
* Simple desc per content
* Usage per content
* All equations/math per content
* Relationship map per content

## Rendering

* Convert machine renders to JSON system
* Convert active render of static content to static renders
* Replace shape renders to cards to actual model objects (stop rendering 100+ micro cubes)
* Add configs and GUI option to disable active renders
* Add GUY option to disable projector card model render (honest its annoying sometimes)
* Add opaque renders options for some machines (mainly for map builders to hide machines)
* Allow color coding beam transfers (mainly for map builders)

## Textures

* Redo all of the card icons (there confusing, all looking roughly the same)
* Use color codes for card types (upgrade vs projector vs field property)
