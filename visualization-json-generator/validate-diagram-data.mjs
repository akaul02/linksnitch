#!/usr/bin/env node
/**
 * Standalone validator — same structural rules as Rasoi src/validateDiagramData.ts
 *
 * Usage: node visualization-json-generator/validate-diagram-data.mjs path/to/diagram-data.json
 */

import fs from "node:fs";

function isRecord(v) {
  return typeof v === "object" && v !== null && !Array.isArray(v);
}

export function validateDiagramDataJson(parsed) {
  const errors = [];
  if (!isRecord(parsed)) {
    return ["Root must be a JSON object"];
  }
  if (typeof parsed.version !== "number") {
    errors.push('Field "version" must be a number');
  }

  const comp = parsed.components;
  if (!isRecord(comp)) {
    errors.push('Field "components" must be an object');
  }
  const containerIds = comp && isRecord(comp) ? Object.keys(comp) : [];

  if (containerIds.length === 0) {
    errors.push("components must include at least one container scope (non-empty object)");
  }

  if (!isRecord(parsed.scopeLabels)) {
    errors.push('Field "scopeLabels" must be an object');
  } else if (containerIds.length > 0) {
    for (const k of containerIds) {
      if (typeof parsed.scopeLabels[k] !== "string") {
        errors.push(`scopeLabels.${k} must be a string (one entry per components key)`);
      }
    }
  }

  const ctx = parsed.context;
  if (!isRecord(ctx)) {
    errors.push('Field "context" must be an object');
  } else {
    if (!isRecord(ctx.nodes)) errors.push("context.nodes must be an object");
    if (!Array.isArray(ctx.edges)) errors.push("context.edges must be an array");
    if (!isRecord(ctx.layouts)) errors.push("context.layouts must be an object");
    let sysCount = 0;
    if (isRecord(ctx.nodes)) {
      for (const n of Object.values(ctx.nodes)) {
        if (isRecord(n) && n.kind === "software-system") sysCount += 1;
      }
    }
    if (sysCount !== 1) {
      errors.push(
        `context.nodes must contain exactly one software-system node (found ${sysCount})`,
      );
    }
  }

  const cont = parsed.containers;
  if (!isRecord(cont)) {
    errors.push('Field "containers" must be an object');
  } else {
    if (!isRecord(cont.nodes)) errors.push("containers.nodes must be an object");
    if (!Array.isArray(cont.edges)) errors.push("containers.edges must be an array");
    if (!isRecord(cont.layouts)) errors.push("containers.layouts must be an object");
    for (const k of containerIds) {
      const node = isRecord(cont.nodes) ? cont.nodes[k] : undefined;
      if (!isRecord(node)) {
        errors.push(`containers.nodes must include "${k}"`);
      } else if (node.kind !== "container") {
        errors.push(`containers.nodes["${k}"].kind must be "container"`);
      }
      if (!isRecord(cont.layouts) || !cont.layouts[k]) {
        errors.push(`containers.layouts must include "${k}"`);
      }
    }
  }

  if (isRecord(comp)) {
    for (const k of containerIds) {
      const block = comp[k];
      if (!isRecord(block)) {
        errors.push(`components.${k} must be an object`);
        continue;
      }
      if (!isRecord(block.nodes)) errors.push(`components.${k}.nodes must be an object`);
      if (!Array.isArray(block.edges)) errors.push(`components.${k}.edges must be an array`);
      if (!isRecord(block.layouts)) errors.push(`components.${k}.layouts must be an object`);
    }
  }

  if (!isRecord(parsed.codeViews)) {
    errors.push('Field "codeViews" must be an object (use {} if none)');
  }

  return errors;
}

function main() {
  const path = process.argv[2];
  if (!path) {
    console.error("Usage: node validate-diagram-data.mjs <file.json>");
    process.exit(1);
  }
  let parsed;
  try {
    parsed = JSON.parse(fs.readFileSync(path, "utf8"));
  } catch (e) {
    console.error(e instanceof Error ? e.message : String(e));
    process.exit(1);
  }
  const err = validateDiagramDataJson(parsed);
  if (err.length > 0) {
    console.error(err.join("\n"));
    process.exit(1);
  }
  console.log("OK — matches Rasoi DiagramData viewer structural checks.");
}

main();
