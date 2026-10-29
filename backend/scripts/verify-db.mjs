import "dotenv/config";
import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();
const expectedTables = ["users", "tests", "questions", "submissions", "vocabularies", "refresh_tokens"];
const expectedForeignKeys = [
  ["questions", "test_id", "tests"],
  ["submissions", "user_id", "users"],
  ["submissions", "test_id", "tests"],
  ["vocabularies", "user_id", "users"],
  ["refresh_tokens", "user_id", "users"],
];

try {
  const tables = await prisma.$queryRaw`
    SELECT tablename AS name FROM pg_tables WHERE schemaname = 'public'
  `;
  const actualTables = new Set(tables.map(({ name }) => name));
  for (const name of expectedTables) {
    if (!actualTables.has(name)) throw new Error(`Missing table: ${name}`);
  }

  const foreignKeys = await prisma.$queryRaw`
    SELECT
      child.relname AS table_name,
      child_column.attname AS column_name,
      parent.relname AS referenced_table
    FROM pg_constraint AS fk
    JOIN pg_class AS child ON child.oid = fk.conrelid
    JOIN pg_class AS parent ON parent.oid = fk.confrelid
    JOIN pg_namespace AS namespace ON namespace.oid = child.relnamespace
    JOIN LATERAL unnest(fk.conkey, fk.confkey)
      AS keys(child_key, parent_key) ON true
    JOIN pg_attribute AS child_column
      ON child_column.attrelid = child.oid AND child_column.attnum = keys.child_key
    WHERE fk.contype = 'f' AND namespace.nspname = 'public'
  `;
  const actualKeys = new Set(
    foreignKeys.map(({ table_name, column_name, referenced_table }) =>
      `${table_name}.${column_name}->${referenced_table}`,
    ),
  );
  for (const [table, column, target] of expectedForeignKeys) {
    const key = `${table}.${column}->${target}`;
    if (!actualKeys.has(key)) throw new Error(`Missing foreign key: ${key}`);
  }
  console.log(`PASS: ${expectedTables.length} tables and ${expectedForeignKeys.length} foreign keys exist in PostgreSQL`);
} catch (error) {
  console.error("FAIL:", error instanceof Error ? error.message : error);
  process.exitCode = 1;
} finally {
  await prisma.$disconnect();
}
