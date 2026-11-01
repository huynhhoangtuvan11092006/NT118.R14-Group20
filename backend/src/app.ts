import cors from "cors";
import express, { type ErrorRequestHandler } from "express";
import helmet from "helmet";
import swaggerUi from "swagger-ui-express";
import { ZodError } from "zod";
import { prisma } from "./config/database.js";
import { env } from "./config/env.js";
import { swaggerDocument } from "./config/swagger.js";
import { HttpError } from "./lib/http-error.js";
import { authRouter } from "./routes/auth.routes.js";
import { userRouter } from "./routes/user.routes.js";
import { testRouter, submissionRouter } from "./routes/reading.routes.js";

export const app = express();
app.disable("x-powered-by");

// Cấu hình Helmet cho phép Swagger UI hiển thị giao diện
app.use(
  helmet({
    contentSecurityPolicy: false,
  })
);
app.use(express.json({ limit: "1mb" }));

if (env.CORS_ORIGIN) app.use(cors({ origin: env.CORS_ORIGIN }));

// --- TÀI LIỆU SWAGGER UI (GIỐNG FASTAPI /docs) ---
app.use("/docs", swaggerUi.serve, swaggerUi.setup(swaggerDocument));
app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerDocument));

app.get("/", (_req, res) => {
  res.json({ name: "bandup-backend", status: "running", docs: "/docs" });
});

app.get("/health", async (_req, res) => {
  try {
    await prisma.$queryRaw`SELECT 1`;
    res.status(200).json({ status: "ok", database: "connected" });
  } catch {
    res.status(503).json({ status: "error", database: "unavailable" });
  }
});

// Định tuyến API (Hỗ trợ cả /auth và /api/v1/auth)
app.use("/auth", authRouter);
app.use("/users", userRouter);
app.use("/tests", testRouter);
app.use("/submissions", submissionRouter);

app.use("/api/v1/auth", authRouter);
app.use("/api/v1/users", userRouter);
app.use("/api/v1/tests", testRouter);
app.use("/api/v1/submissions", submissionRouter);

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
