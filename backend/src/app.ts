import cors from "cors";
import express from "express";
import helmet from "helmet";
import { prisma } from "./config/database.js";
import { env } from "./config/env.js";

export const app = express();
app.disable("x-powered-by");
app.use(helmet());
app.use(express.json({ limit: "1mb" }));

if (env.CORS_ORIGIN) {
  app.use(cors({ origin: env.CORS_ORIGIN }));
}

app.get("/", (_req, res) => {
  res.json({ name: "bandup-backend", status: "running" });
});

// Live readiness probe: returns 503 if PostgreSQL is unreachable.
app.get("/health", async (_req, res) => {
  try {
    await prisma.$queryRaw`SELECT 1`;
    res.status(200).json({ status: "ok", database: "connected" });
  } catch {
    res.status(503).json({ status: "error", database: "unavailable" });
  }
});

app.use((_req, res) => {
  res.status(404).json({ error: "Not found" });
});
