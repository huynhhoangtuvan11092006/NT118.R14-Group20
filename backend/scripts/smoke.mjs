const endpoint = process.env.HEALTH_URL ?? "http://localhost:3000/health";
try {
  const response = await fetch(endpoint, { signal: AbortSignal.timeout(5000) });
  const data = await response.json();
  if (!response.ok || data.status !== "ok" || data.database !== "connected") {
    throw new Error(`Unexpected response (${response.status}): ${JSON.stringify(data)}`);
  }
  console.log("PASS: /health returned 200 and PostgreSQL is connected");
} catch (error) {
  console.error("FAIL:", error instanceof Error ? error.message : error);
  process.exitCode = 1;
}
