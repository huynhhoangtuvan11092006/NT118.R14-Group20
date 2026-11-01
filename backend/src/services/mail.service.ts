import nodemailer, { type Transporter } from "nodemailer";
import { env } from "../config/env.js";

let transporter: Transporter | null = null;

function getTransporter(): Transporter | null {
  if (transporter) return transporter;
  if (env.SMTP_USER && env.SMTP_PASS) {
    if (env.SMTP_HOST.includes("gmail.com") || env.SMTP_USER.includes("@gmail.com")) {
      transporter = nodemailer.createTransport({
        service: "gmail",
        auth: {
          user: env.SMTP_USER,
          pass: env.SMTP_PASS,
        },
      });
    } else {
      transporter = nodemailer.createTransport({
        host: env.SMTP_HOST,
        port: env.SMTP_PORT,
        secure: env.SMTP_PORT === 465,
        auth: {
          user: env.SMTP_USER,
          pass: env.SMTP_PASS,
        },
      });
    }
  }
  return transporter;
}


export async function sendOtpEmail(to: string, otp: string): Promise<boolean> {
  console.log(`\n========================================`);
  console.log(`[MAIL SERVICE] OTP Code for ${to}: [ ${otp} ]`);
  console.log(`========================================\n`);

  const mailTransporter = getTransporter();
  if (!mailTransporter) {
    console.warn(`[MAIL SERVICE] SMTP credentials (SMTP_USER / SMTP_PASS) not set in .env. Skipping real email dispatch.`);
    return false;
  }

  const htmlContent = `
    <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 540px; margin: 0 auto; padding: 32px 24px; border: 1px solid #E5E7EB; border-radius: 16px; background-color: #FFFFFF;">
      <div style="margin-bottom: 24px; text-align: center;">
        <h2 style="color: #4F46E5; margin: 0; font-size: 26px; font-weight: 800; letter-spacing: -0.5px;">IELTSPRO</h2>
        <p style="color: #6B7280; font-size: 14px; margin-top: 4px;">Khôi phục mật khẩu tài khoản</p>
      </div>
      
      <p style="color: #374151; font-size: 15px; line-height: 1.6; margin-bottom: 20px;">
        Xin chào,<br/>
        Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản <strong>${to}</strong>.
      </p>

      <div style="background: #EEF2FF; border: 1px dashed #6366F1; border-radius: 12px; padding: 20px; text-align: center; margin: 28px 0;">
        <span style="font-size: 13px; text-transform: uppercase; font-weight: 700; color: #4338CA; letter-spacing: 1px; display: block; margin-bottom: 8px;">Mã xác nhận OTP</span>
        <span style="font-size: 36px; font-weight: 800; letter-spacing: 8px; color: #312E81; font-family: monospace;">${otp}</span>
      </div>

      <p style="color: #6B7280; font-size: 13px; line-height: 1.6;">
        Mã xác nhận này có hiệu lực trong vòng <strong>10 phút</strong>. Vì lý do bảo mật, vui lòng không chia sẻ mã này với bất kỳ ai.<br/>
        Nếu bạn không thực hiện yêu cầu này, bạn có thể an tâm bỏ qua email này.
      </p>

      <hr style="border: none; border-top: 1px solid #F3F4F6; margin: 28px 0 20px;" />
      <p style="color: #9CA3AF; font-size: 12px; text-align: center; margin: 0;">
        © 2026 IELTS Pro Team. All rights reserved.
      </p>
    </div>
  `;

  try {
    await mailTransporter.sendMail({
      from: env.EMAIL_FROM,
      to,
      subject: `[IELTSPRO] Mã xác nhận đặt lại mật khẩu`,
      html: htmlContent,
      text: `Mã xác nhận đặt lại mật khẩu của bạn là: ${otp}. Mã có hiệu lực trong 10 phút.`,
    });

    console.log(`[MAIL SERVICE] Successfully sent OTP email to: ${to}`);
    return true;
  } catch (error) {
    console.error(`[MAIL SERVICE] Failed to send email to ${to}:`, error);
    return false;
  }
}
