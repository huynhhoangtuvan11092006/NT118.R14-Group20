import { Router } from "express";
import {
  getReadingTestDetail,
  getReadingTests,
  getSubmissionDetail,
  submitReading,
} from "../controllers/reading.controller.js";
import { optionalAuth } from "../middlewares/auth.middleware.js";

export const testRouter = Router();

// GET /api/v1/tests?type=READING hoặc GET /api/v1/tests/reading
testRouter.get("/", optionalAuth, getReadingTests);
testRouter.get("/reading", optionalAuth, getReadingTests);

// GET /api/v1/tests/reading/:id hoặc GET /api/v1/tests/:id
testRouter.get("/reading/:id", optionalAuth, getReadingTestDetail);
testRouter.get("/:id", optionalAuth, getReadingTestDetail);

export const submissionRouter = Router();

// POST /api/v1/submissions/reading
submissionRouter.post("/reading", optionalAuth, submitReading);

// GET /api/v1/submissions/:id
submissionRouter.get("/:id", optionalAuth, getSubmissionDetail);
