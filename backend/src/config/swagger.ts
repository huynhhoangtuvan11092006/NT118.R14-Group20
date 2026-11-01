export const swaggerDocument = {
  openapi: "3.0.0",
  info: {
    title: "BandUp IELTS Prep API Documentation",
    version: "1.0.0",
    description: "Tài liệu Swagger API cho hệ thống BandUp IELTS Prep (Phase 1 Auth & Profile)",
    contact: {
      name: "NT118.R14-Group20",
    },
  },
  servers: [
    {
      url: "http://localhost:3000",
      description: "Local Development Server",
    },
  ],
  tags: [
    { name: "Authentication", description: "Các API xác thực và tài khoản" },
    { name: "Users", description: "Các API quản lý người dùng và mục tiêu học tập" },
    { name: "System", description: "Kiểm tra hệ thống" },
  ],
  components: {
    securitySchemes: {
      bearerAuth: {
        type: "http",
        scheme: "bearer",
        bearerFormat: "JWT",
        description: "Nhập token JWT nhận được sau khi đăng ký hoặc đăng nhập (chỉ cần nhập chuỗi token)",
      },
    },
    schemas: {
      RegisterRequest: {
        type: "object",
        required: ["fullName", "email", "password"],
        properties: {
          fullName: { type: "string", example: "Huỳnh Hoàng Tuấn", minLength: 2, maxLength: 150 },
          email: { type: "string", format: "email", example: "tuvan1109@gmail.com" },
          password: { type: "string", format: "password", example: "matkhau123", minLength: 8 },
        },
      },
      LoginRequest: {
        type: "object",
        required: ["email", "password"],
        properties: {
          email: { type: "string", format: "email", example: "tuvan1109@gmail.com" },
          password: { type: "string", format: "password", example: "matkhau123" },
        },
      },
      UpdateTargetBandRequest: {
        type: "object",
        required: ["targetBand"],
        properties: {
          targetBand: { type: "number", example: 7.5, minimum: 0, maximum: 9, description: "Điểm Target Band từ 0 đến 9 (bước nhảy 0.5)" },
        },
      },
      UpdateProfileRequest: {
        type: "object",
        properties: {
          fullName: { type: "string", example: "Huỳnh Hoàng Tuấn" },
          avatarUrl: { type: "string", example: "https://example.com/avatar.png" },
          currentBand: { type: "number", example: 6.5 },
          expectedExamDate: { type: "string", format: "date", example: "2026-12-31" },
          dailyStudyMinutes: { type: "integer", example: 30 },
        },
      },
      UserResponse: {
        type: "object",
        properties: {
          id: { type: "string", format: "uuid" },
          email: { type: "string" },
          fullName: { type: "string" },
          avatarUrl: { type: "string", nullable: true },
          targetBand: { type: "number", nullable: true },
          currentBand: { type: "number", nullable: true },
          expectedExamDate: { type: "string", nullable: true },
          dailyStudyMinutes: { type: "integer", nullable: true },
          createdAt: { type: "string", format: "date-time" },
        },
      },
      AuthResponse: {
        type: "object",
        properties: {
          user: { $ref: "#/components/schemas/UserResponse" },
          accessToken: { type: "string" },
          refreshToken: { type: "string" },
          tokenType: { type: "string", example: "Bearer" },
          expiresIn: { type: "integer", example: 900 },
        },
      },
      ErrorResponse: {
        type: "object",
        properties: {
          error: { type: "string", example: "VALIDATION_ERROR" },
          message: { type: "string", example: "Chi tiết lỗi" },
        },
      },
    },
  },
  paths: {
    "/health": {
      get: {
        tags: ["System"],
        summary: "Kiểm tra sức khỏe Server & Database",
        responses: {
          200: {
            description: "Hệ thống hoạt động bình thường",
            content: { "application/json": { schema: { type: "object", properties: { status: { type: "string", example: "ok" }, database: { type: "string", example: "connected" } } } } },
          },
        },
      },
    },
    "/auth/register": {
      post: {
        tags: ["Authentication"],
        summary: "Đăng ký tài khoản mới",
        description: "Tạo tài khoản học viên mới với email và mật khẩu (tối thiểu 8 ký tự).",
        requestBody: {
          required: true,
          content: { "application/json": { schema: { $ref: "#/components/schemas/RegisterRequest" } } },
        },
        responses: {
          201: {
            description: "Đăng ký thành công",
            content: { "application/json": { schema: { $ref: "#/components/schemas/AuthResponse" } } },
          },
          409: {
            description: "Email đã tồn tại trong hệ thống",
            content: { "application/json": { schema: { $ref: "#/components/schemas/ErrorResponse" } } },
          },
        },
      },
    },
    "/auth/login": {
      post: {
        tags: ["Authentication"],
        summary: "Đăng nhập tài khoản",
        description: "Xác thực email và mật khẩu, trả về cặp JWT accessToken và refreshToken.",
        requestBody: {
          required: true,
          content: { "application/json": { schema: { $ref: "#/components/schemas/LoginRequest" } } },
        },
        responses: {
          200: {
            description: "Đăng nhập thành công",
            content: { "application/json": { schema: { $ref: "#/components/schemas/AuthResponse" } } },
          },
          401: {
            description: "Email hoặc mật khẩu không chính xác",
            content: { "application/json": { schema: { $ref: "#/components/schemas/ErrorResponse" } } },
          },
        },
      },
    },
    "/users/target-band": {
      put: {
        tags: ["Users"],
        summary: "Cập nhật điểm Target Band",
        security: [{ bearerAuth: [] }],
        description: "Lưu điểm IELTS mục tiêu của học viên (từ 0 đến 9, bước nhảy 0.5).",
        requestBody: {
          required: true,
          content: { "application/json": { schema: { $ref: "#/components/schemas/UpdateTargetBandRequest" } } },
        },
        responses: {
          200: {
            description: "Cập nhật thành công",
            content: { "application/json": { schema: { $ref: "#/components/schemas/UserResponse" } } },
          },
          401: { description: "Chưa đăng nhập hoặc Token hết hạn" },
        },
      },
    },
    "/users/me": {
      get: {
        tags: ["Users"],
        summary: "Lấy thông tin tài khoản hiện tại",
        security: [{ bearerAuth: [] }],
        description: "Lấy hồ sơ cá nhân của học viên đang đăng nhập.",
        responses: {
          200: {
            description: "Thành công",
            content: { "application/json": { schema: { $ref: "#/components/schemas/UserResponse" } } },
          },
          401: { description: "Chưa đăng nhập hoặc Token không hợp lệ" },
        },
      },
      put: {
        tags: ["Users"],
        summary: "Cập nhật thông tin hồ sơ (Ngày thi, số phút học)",
        security: [{ bearerAuth: [] }],
        description: "Cập nhật ngày thi dự kiến, số phút học mỗi ngày, kỹ năng.",
        requestBody: {
          required: true,
          content: { "application/json": { schema: { $ref: "#/components/schemas/UpdateProfileRequest" } } },
        },
        responses: {
          200: {
            description: "Cập nhật hồ sơ thành công",
            content: { "application/json": { schema: { $ref: "#/components/schemas/UserResponse" } } },
          },
          401: { description: "Chưa đăng nhập" },
        },
      },
    },
    "/auth/logout": {
      post: {
        tags: ["Authentication"],
        summary: "Đăng xuất tài khoản",
        requestBody: {
          required: true,
          content: { "application/json": { schema: { type: "object", properties: { refreshToken: { type: "string" } } } } },
        },
        responses: {
          204: { description: "Đăng xuất thành công" },
        },
      },
    },
  },
};
