# INFO
This log contains changes made to the project. Each entry contains changed made after the last version but before the number was changed. Any changes made after a number change are considered part of the next release. This is regardless if versions are still being released with that version number attached. 

If this is a problem, use exact build numbers to track changes. As each build logs the git-hash it was created from to better understand changes made.

# Versions
## 4.0.0 - 11/3/2017
### Runtime Changes
Reworked: Configs (folder change, file name change, content change, field name change)
Reworked: Coercion deriver (functions roughly the same)
Added: Extensive settings for coercion deriver
Added: UE power support
Fixed: Issues with RF power handling
Removed: Unused configs
Changed: All configs
Changed: block ids, added mappings to prevent map save issues :P
Changed: Power cost of field to be massively cheaper
Changed: Coercion deriver recipe to include electronics


### Development Changes
Moved: Everything around to be easier to find (no package names are the same)
Deprecated: half of the API and all prefabs (Everything is being rewritten)
Implemented: Module inventory, replaces IModuleAccessor

## Before 4.0.0 - 6 months before 4.0.0
Version # were not tracked properly with all builds being generated as 0.51 or 0.1.0. So no changes are going to be maintained past this point.

That being said the major changes were updating from 1.6.4 to 1.7.10 with minor changes to support VoltzEngine. As well minor bug fixes, patches, and adjustements to make the mod usable.

Credit to Poopsicle for the update
