You are generating a single JSON file for the **Rasoi DiagramData viewer** (React Flow + C4 drill-down).

## Output format

- Output **valid JSON only**: one root object. No markdown fences, no commentary before or after.
- Required top-level keys: `version` (number), `scopeLabels`, `context`, `containers`, `components`, `codeViews`.

## C4 mapping (all in one file)

- **context**: C4 system context. Must include **exactly one** node with `"kind": "software-system"` (the product boundary). Optional `person` and `external` actors. `edges` connect node ids. `layouts` has an entry per node id in `context.nodes`. `viewBox` is a string like `"0 0 800 400"`.
- **containers**: Deployable/runnable units inside the system. The **same** `software-system` id appears only in **context**, not as a container. Each drillable unit is `"kind": "container"` with an id that will be a key under `components`. Reuse the same person/external ids as in context when they are the same entity. `layouts` must include every id in `containers.nodes`.
- **components**: Non-empty object. **Each key** `k` must match a container id from `containers.nodes` where `kind === "container"`. Value is a full inner diagram: `viewBox`, `nodes`, `edges`, `layouts`. Inner nodes are usually `"kind": "component"`; neighbors outside the boundary can be `"kind": "external"`. Prefer globally unique component ids (e.g. prefixes per container).
- **codeViews**: Object; use `{}` if none. Keys must be **component** ids from some `components[*].nodes`. Values are small UML-like graphs (`code-class` / `code-interface`, `members`, `edges`, `layouts`, `viewBox`, optional `packageLabel`).

## Hard rules (viewer will reject if violated)

These match the Rasoi validator in `src/validateDiagramData.ts`:

1. `version` is a **number** (e.g. `4`).
2. `components` is a non-empty object (at least one container scope).
3. `scopeLabels` is an object with a **non-empty string** for **every** key in `components`.
4. `context.nodes` contains **exactly one** node with `"kind": "software-system"`.
5. For each key `k` in `components`:
   - `containers.nodes[k]` exists and has `"kind": "container"`.
   - `containers.layouts[k]` exists.
   - `components[k]` has `viewBox`, `nodes` (object), `edges` (array), `layouts` (object).
6. `codeViews` must be present and be an object (use `{}` if unused).

## Quality rules (recommended; improves rendering)

- In each diagram section, **every** id in `nodes` has a matching key in `layouts` for that section; `layouts[id].kind` matches `nodes[id].kind`.
- Every `edges[].from` and `edges[].to` refers to an id in that section’s `nodes`.
- Use consistent ids across context and containers for the same person or external system.

## Node kinds (C4 / code)

- Context & containers & components diagrams: `person`, `software-system`, `external`, `container`, `component`.
- Code diagrams (`codeViews`): `code-class`, `code-interface`.

Each node should include: `id`, `kind`, `title`, `summary`. Optional: `techLabel`, `containerOverview` (on software-system).

Each edge: `id`, `from`, `to`, `label`. Optional: `dashed` (boolean).

## Large or complex codebases

Do **not** sacrifice accuracy for a single huge response. Prefer:

1. Outline entrypoints, services, data stores, and boundaries (short text).
2. Emit **context** + **containers** + `components` keys with complete inner diagrams per drillable container.
3. Refine each `components[containerId]` with real modules/packages tied to files.

Name containers after **actual** boundaries (repo paths, services, apps), not generic placeholders unless accurate.

## Task

Analyze the user-supplied codebase path or files and produce **`diagram-data.json`** content following the above. Ground `summary` and `techLabel` in real files, frameworks, and config when possible.
