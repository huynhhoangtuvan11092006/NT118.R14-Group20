import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { test } from "node:test";

const schema = readFileSync(new URL("../prisma/schema.prisma", import.meta.url), "utf8");
const sql = readFileSync(new URL("../prisma/migrations/20261029000000_init/migration.sql", import.meta.url), "utf8");
const requiredTables = ["users", "tests", "questions", "submissions", "vocabularies"];

for (const table of requiredTables) {
  test(`Initial migration creates ${table}`, () => {
    assert.match(sql, new RegExp(`CREATE TABLE "${table}" \\(`));
    assert.match(schema, new RegExp(`@@map\\("${table}"\\)`));
  });
}

for (const [table, column, target] of [
  ["questions", "test_id", "tests"],
  ["submissions", "user_id", "users"],
  ["submissions", "test_id", "tests"],
  ["vocabularies", "user_id", "users"],
]) {
  test(`Foreign key ${table}.${column} → ${target}.id`, () => {
    assert.ok(sql.includes(`FOREIGN KEY ("${column}") REFERENCES "${target}"("id")`));
  });
}

test("Migration lock targets PostgreSQL", () => {
  const lock = readFileSync(new URL("../prisma/migrations/migration_lock.toml", import.meta.url), "utf8");
  assert.match(lock, /provider = "postgresql"/);
});
