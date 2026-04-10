# Visualization JSON generator kit (DiagramData)

Use this folder to produce **`diagram-data.json`** files compatible with the Rasoi **DiagramData viewer** ([`src/validateDiagramData.ts`](../src/validateDiagramData.ts)).

## Steps

1. Open [`DIAGRAM_DATA_PROMPT.md`](DIAGRAM_DATA_PROMPT.md) and paste it into your AI assistant, together with the codebase (or path) to analyze.
2. Attach [`diagram-data.minimal.json`](diagram-data.minimal.json) and [`diagram-data.example.json`](diagram-data.example.json) as shape references if helpful.
3. Save the model output as `diagram-data.json`.
4. Validate:

   ```bash
   node visualization-json-generator/validate-diagram-data.mjs diagram-data.json
   ```

   Or from repo root:

   ```bash
   npm run validate:diagram-data -- path/to/diagram-data.json
   ```

Fix any reported errors before uploading the file into the viewer.
