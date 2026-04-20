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

## Can plugins share state without sharing a plugin dependency?

Yes.

Common patterns:
- **External shared store** (SQL/Redis): both plugins write/read same schema. No plugin-to-plugin runtime dependency.
- **Shared file contract** (YAML/JSON): both plugins read/write same file path + schema. Cheap but fragile unless schema/versioning is strict.
- **Bukkit Services API**: one plugin registers service interface at runtime; consumer plugin uses service lookup. Consumer can avoid hard `depend` and only use `softdepend` + runtime checks.
- **Plugin messaging/events**: exchange data on demand; persistence still needs file/DB/PDC backing somewhere.

So yes: state sharing does **not** require shipping a separate API plugin JAR as a hard dependency.

## Is PDC the right way?

**For single-plugin owned player metadata:** yes, usually best first choice.

**For cross-plugin shared state:** maybe, with caveats:
- PDC is attached to Bukkit holders (player/entity/item/chunk/world), not a general-purpose server database.
- Data visibility across plugins depends on agreeing on key format and holder lifecycle.
- For offline/global queries, SQL/YAML is often simpler than forcing holder loads.
- PDC is good for lightweight metadata tied to holder lifecycle; less good for broad analytics/reporting or complex joins.

Practical guidance:
- If only MountainDewritoes uses name color: move to PDC (or local YAML) in MountainDewritoes.
- If multiple plugins must share and query this data: use SQL (or a well-defined shared storage contract) plus optional service facade.

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
