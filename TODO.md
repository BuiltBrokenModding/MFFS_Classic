List of changes that need to be made to the mod


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
