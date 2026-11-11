import "dotenv/config";
import { z } from "zod";

const envSchema = z.object({
  NODE_ENV: z.enum(["development", "test", "production"]).default("development"),
  PORT: z.coerce.number().int().min(1).max(65535).default(3000),
  DATABASE_URL: z.string().regex(/^postgres(?:ql)?:\/\//, "Expected a PostgreSQL connection URL"),
  CORS_ORIGIN: z.string().optional(),
  JWT_ACCESS_SECRET: z.string().min(32),

  JWT_REFRESH_SECRET: z.string().min(32),
  SMTP_HOST: z.string().default("smtp.gmail.com"),
  SMTP_PORT: z.coerce.number().int().default(587),
  SMTP_SECURE: z.coerce.boolean().default(false),
  SMTP_USER: z.string().optional(),
  SMTP_PASS: z.string().optional(),
  EMAIL_FROM: z.string().default("IELTS Prep App <no-reply@ieltsapp.com>"),
  GOOGLE_CLIENT_ID: z.string().optional(),
  GEMINI_API_KEY: z.string().optional(),
  GEMINI_MODEL: z.string().default("gemini-3.5-flash"),
  GEMINI_TIMEOUT_MS: z.coerce.number().int().min(1000).max(120000).default(45000),

  // 9Router / Custom AI Proxy Support
  AI_BASE_URL: z.string().optional(),
  AI_API_KEY: z.string().optional(),
  AI_MODEL: z.string().optional(),

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

export const env = {
  ...parsed.data,
  AI_BASE_URL: parsed.data.AI_BASE_URL || process.env.BASE_URL || process.env.OPENAI_BASE_URL || process.env.ROUTER_BASE_URL,
  AI_API_KEY: parsed.data.AI_API_KEY || process.env.API_KEY || process.env.OPENAI_API_KEY || process.env.ROUTER_API_KEY || parsed.data.GEMINI_API_KEY,
  AI_MODEL: parsed.data.AI_MODEL || process.env.MODEL || process.env.OPENAI_MODEL,
};
