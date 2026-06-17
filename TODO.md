## TODO/IDEAS
- Add scaling to the custom shape mode.
-       Change maxCustomModeScale to use volume or max bounding and to allow -1 for no max.
-       Add 3rd mode for hollow shape. (Might be too complicated)
- Interdiction Matrix mode to use in-field instead of its own scale.
-       Scale modules could add to blocks outside of field.
-       Min scale modules could be required.
-       Option could be from center of force field projection.
-           Might not apply to custom shapes well.
- Glow modules apply artificial lighting to block surfaces inside a force field.
- Beacon Effects in-zone.
- Harvest speedup?
- Only send the interdiction warn zone to clients when isActive(); when biometrics is active.
- Make the non-square camo filter an option instead of forced, some people are delusional.
- Force Fields protect from gravitational anomalies

## Optimization
- Use ConcurrentHashMap.newKeySet() instead of a lock in FrequencyGrid.
- Test shape calculation at higher sizes, might be a bit heavy still.

## Known Bugs
- Items phase through bottoms of force fields
- Rendering on projector disintegration/stabilization is flickery and generally bad.

# Uh??
- Projector: large fields skip unloaded chunks during selection and may appear stalled once all loaded positions are filled. Consider queued retries or safe chunk ticketing??
- Projector: investigate large-field `prewarmProjectionCache()` cost. Radius-64 spheres can synchronously scan hundreds of thousands of positions on the server thread.