## TODO/IDEAS
Add scaling to the custom shape mode.
    Investigate the maxCustomModeScale var.
    Add 3rd mode for hollow shape
Interdiction Matrix mode to use in-field instead of its own scale.
    Scale modules could add to blocks outside of field.
    Min scale modules could be required.
    Option could be from center of force field projection.
        Might not apply to custom shapes well.
Glow modules apply artificial lighting to block surfaces inside a force field.
Beacon Effects in-zone.
Harvest speedup?
Dye colors for force fields.
    Use the camo module or make a new module - module could support setting color without a dye.
Only send the interdiction warn zone to clients when isActive(); when biometrics is active.
Make the non-square camo filter an option instead of forced, some people are delusional.

## Optimization
The lighting not making contact with a physical block should have its own independent max value (~3-7).
    Lighting updates higher than this cause cascading light updates.
    Create a variable for this value and place in performance section.
Implement CCL emissives on force field blocks.
    Based on amount of glow modules, up to 64.

Remove the projectionCycleTicks from the settings, this is not as useful of a setting.
    **New default of 1 then lower max speeds to smooth FF placements**
Investigate useCache var.
    We may have hard coded the cache by accident... tehe :D
zeroFieldBlockLights is called on a soft destroy, probably not optimal if its not needed.


## Known Bugs
Look into rendering of power cables not showing emittions on deriver.
    Draining cable too fast?
Items phase through bottoms of force fields