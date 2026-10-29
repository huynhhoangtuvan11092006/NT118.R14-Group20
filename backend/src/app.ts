import cors from "cors";
import express, { type ErrorRequestHandler } from "express";
import helmet from "helmet";
import { ZodError } from "zod";
import { prisma } from "./config/database.js";
import { env } from "./config/env.js";
import { HttpError } from "./lib/http-error.js";
import { authRouter } from "./routes/auth.routes.js";
import { userRouter } from "./routes/user.routes.js";

export const app = express();
app.disable("x-powered-by");
app.use(helmet());
app.use(express.json({ limit: "1mb" }));

if (env.CORS_ORIGIN) app.use(cors({ origin: env.CORS_ORIGIN }));

app.get("/", (_req, res) => {
  res.json({ name: "bandup-backend", status: "running" });
});

app.get("/health", async (_req, res) => {
  try {
    await prisma.$queryRaw`SELECT 1`;
    res.status(200).json({ status: "ok", database: "connected" });
  } catch {
    res.status(503).json({ status: "error", database: "unavailable" });
  }
});

app.use("/auth", authRouter);
app.use("/users", userRouter);
app.use((_req, res) => res.status(404).json({ error: "NOT_FOUND", message: "Route not found" }));

const errorHandler: ErrorRequestHandler = (error: unknown, _req, res, _next) => {
  void _req;
  void _next;
  if (error instanceof ZodError) {
    res.status(400).json({
      error: "VALIDATION_ERROR",
      details: error.issues.map((issue) => ({ path: issue.path.join("."), message: issue.message })),
    });
    return;
  }
  if (error instanceof HttpError) {
    res.status(error.status).json({ error: error.code, message: error.message });
    return;
  }
  if (error instanceof SyntaxError && "body" in error) {
    res.status(400).json({ error: "INVALID_JSON", message: "Malformed JSON body" });
    return;
  }
  console.error("Unhandled request error:", error instanceof Error ? error.message : "Unknown error");
  res.status(500).json({ error: "INTERNAL_ERROR", message: "Internal server error" });
};
app.use(errorHandler);
