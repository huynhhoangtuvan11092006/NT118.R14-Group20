import "dotenv/config";
import { z } from "zod";

const envSchema = z.object({
  NODE_ENV: z.enum(["development", "test", "production"]).default("development"),
  PORT: z.coerce.number().int().min(1).max(65535).default(3000),
  DATABASE_URL: z.string().regex(/^postgres(?:ql)?:\/\//, "Expected a PostgreSQL connection URL"),
  CORS_ORIGIN: z.string().optional(),
  JWT_ACCESS_SECRET: z.string().min(32),
  JWT_REFRESH_SECRET: z.string().min(32),
}).superRefine((value, ctx) => {
  for (const field of ["JWT_ACCESS_SECRET", "JWT_REFRESH_SECRET"] as const) {
    if (value[field].startsWith("CHANGE_ME")) {
      ctx.addIssue({ code: "custom", path: [field], message: "Generate a secret before running the server" });
    }
  }
  if (value.JWT_ACCESS_SECRET === value.JWT_REFRESH_SECRET) {
    ctx.addIssue({ code: "custom", path: ["JWT_REFRESH_SECRET"], message: "Refresh secret must differ from access secret" });
  }
});

const parsed = envSchema.safeParse(process.env);
if (!parsed.success) {
  const names = parsed.error.issues.map((issue) => issue.path.join(".")).join(", ");
  throw new Error(`Invalid environment variables: ${names}. Check backend/.env`);
}

export const env = parsed.data;
