# GrandioseAPI Dependency Review (MLG-Fortress org)

## What GrandioseAPI currently does

GrandioseAPI is a Bukkit/Paper plugin that centralizes **persistent per-player YAML state** behind a tiny wrapper API:

- `GrandPlayerManager` lazily caches `GrandPlayer` wrappers keyed by UUID.
- Each `GrandPlayer` wraps one `YamlConfiguration` file under `grandPlayers/<uuid>`.
- It exposes convenience methods for save, get, getString, and one specific feature (`nameColor`) with deterministic fallback by UUID hash.

In short: it is a shared persistence layer for plugin-to-plugin player metadata, with one built-in nickname color utility.

## Is it useful?

### Useful when
- Multiple plugins in the same server need shared, durable player metadata.
- You want a stable plugin-facing API instead of each plugin writing its own player file format.

### Not very useful when
- Only one plugin needs this data.
- You only need one field (for example, nickname color).
- You can store data directly in that plugin's own data file or use PDC/SQL.

Current code is intentionally simple, but this also means it provides only a small amount of real abstraction.

## Which MLG-Fortress plugins depend on it

Based on direct checks of repository manifests/build files and source references:

1. **MountainDewritoes**
   - Declares `GrandioseAPI` as a Maven dependency in `pom.xml`.
   - Declares `GrandioseAPI` in `softdepend` in `plugin.yml`.
   - Uses `GrandioseAPI` and `GrandPlayer` directly in code (`NickCommand`) to store/read player name color.

2. **No other obvious active dependencies found in checked repos**
   - Checked representative sibling plugins (e.g., DeathSpectating, PrettySimpleShop, BetterTPA, RecipeBook, MultiGenerator) and did not find direct `GrandioseAPI` dependency markers in inspected files.
   - Because anonymous GitHub code search is restricted, this review is strong but not mathematically exhaustive across every file in every org repository.

## Can this be implemented better with less complexity?

Yes. For current observed usage (name color persistence), a simpler path exists.

### Option A (least complexity): move feature into MountainDewritoes
- Keep a local `playerNameColor.yml` (or use existing config pathing).
- Read/write by UUID directly.
- Remove cross-plugin dependency entirely.

### Option B (modern Bukkit): use PersistentDataContainer
- Store name color on player via namespaced key.
- Still keep optional migration for old YAML entries.
- Removes separate API plugin and file lifecycle complexity.

### Option C (if true multi-plugin sharing is still desired)
Keep GrandioseAPI, but trim and harden it:
- Make it a **library module** rather than required runtime plugin when possible.
- Replace ad-hoc Object getters with typed methods.
- Add unload/eviction strategy for `grandPlayers` map.
- Document schema and version it.
- Add migration + tests.

## Recommendation

Given current observed dependency profile, GrandioseAPI appears **overweight for present usage**.

If MountainDewritoes is the only real consumer in the org, consolidate name-color persistence into MountainDewritoes and deprecate GrandioseAPI. Keep a small migration bridge for existing YAML files.
