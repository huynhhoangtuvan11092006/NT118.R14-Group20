import { app } from "./app.js";
import { connectDatabase, disconnectDatabase } from "./config/database.js";
import { env } from "./config/env.js";

async function main(): Promise<void> {
  await connectDatabase();
  const server = app.listen(env.PORT, () => {
    console.info(`Backend listening on http://localhost:${env.PORT}`);
  });

  let shuttingDown = false;
  const shutdown = (signal: string): void => {
    if (shuttingDown) return;
    shuttingDown = true;
    console.info(`Received ${signal}; shutting down`);
    server.close(() => {
      void disconnectDatabase().finally(() => {
        process.exit(0);
      });
    });
  };

  process.on("SIGINT", () => shutdown("SIGINT"));
  process.on("SIGTERM", () => shutdown("SIGTERM"));
}

main().catch(async (error: unknown) => {
  console.error("Backend startup failed:", error instanceof Error ? error.message : error);
  await disconnectDatabase();
  process.exitCode = 1;
});
