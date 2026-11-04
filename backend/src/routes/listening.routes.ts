import { Router } from "express";
import {
  getListeningTestDetail,
  getListeningTests,
  getListeningSubmissionDetail,
  submitListening,
} from "../controllers/listening.controller.js";
import { optionalAuth } from "../middlewares/auth.middleware.js";

export const listeningTestRouter = Router();

// GET /api/v1/tests/listening
listeningTestRouter.get("/", optionalAuth, getListeningTests);
listeningTestRouter.get("/:id", optionalAuth, getListeningTestDetail);

export const listeningSubmissionRouter = Router();

// POST /api/v1/submissions/listening
listeningSubmissionRouter.post("/", optionalAuth, submitListening);
listeningSubmissionRouter.get("/:id", optionalAuth, getListeningSubmissionDetail);
