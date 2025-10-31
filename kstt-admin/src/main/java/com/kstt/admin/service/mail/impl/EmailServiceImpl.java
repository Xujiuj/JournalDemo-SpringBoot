package com.kstt.admin.service.mail.impl;

import com.kstt.admin.config.properties.AppProperties;
import com.kstt.admin.service.mail.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!appProperties.getMail().isEnabled()) {
            log.debug("邮件服务已禁用，跳过发送邮件到: {}", to);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("简单邮件发送成功 -> 收件人: {}, 主题: {}", to, subject);
        } catch (Exception e) {
            log.error("发送简单邮件失败 -> 收件人: {}, 主题: {}", to, subject, e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String html) {
        sendHtmlEmail(to, null, subject, html);
    }

    @Override
    public void sendHtmlEmail(String to, String cc, String subject, String html) {
        if (!appProperties.getMail().isEnabled()) {
            log.debug("邮件服务已禁用，跳过发送邮件到: {}", to);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            if (StringUtils.hasText(cc)) {
                helper.setCc(cc.split(","));
            }
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("HTML邮件发送成功 -> 收件人: {}, 抄送: {}, 主题: {}", to, cc, subject);
        } catch (MessagingException e) {
            log.error("发送HTML邮件失败 -> 收件人: {}, 主题: {}", to, subject, e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendArticleSubmissionEmail(String articleTitle, String manuscriptId,
                                            String submitterName, String submitterEmail,
                                            String submitTime) {
        AppProperties.MailProperties.ArticleSubmissionProperties config = 
                appProperties.getMail().getArticleSubmission();

        if (!config.isEnabled()) {
            log.debug("投稿确认邮件已禁用，跳过发送");
            return;
        }

        String to = config.getTo();
        if (!StringUtils.hasText(to)) {
            log.warn("投稿确认邮件收件人未配置，跳过发送");
            return;
        }

        String html = buildArticleSubmissionEmailHtml(
                articleTitle, manuscriptId, submitterName, submitterEmail, submitTime);

        sendHtmlEmail(to, config.getCc(), config.getSubject(), html);
        log.info("投稿确认邮件已发送 -> 文章: {}, 稿件编号: {}, 投稿人: {}", 
                articleTitle, manuscriptId, submitterEmail);
    }

    /**
     * 构建投稿确认邮件的HTML内容
     */
    private String buildArticleSubmissionEmailHtml(String articleTitle, String manuscriptId,
                                                    String submitterName, String submitterEmail,
                                                    String submitTime) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: 'Microsoft YaHei', Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #f5f5f5;
                        }
                        .container {
                            background-color: #ffffff;
                            border-radius: 8px;
                            padding: 30px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        }
                        .header {
                            border-bottom: 2px solid #4CAF50;
                            padding-bottom: 15px;
                            margin-bottom: 20px;
                        }
                        .header h1 {
                            color: #4CAF50;
                            margin: 0;
                            font-size: 24px;
                        }
                        .content {
                            margin: 20px 0;
                        }
                        .info-box {
                            background-color: #f9f9f9;
                            border-left: 4px solid #4CAF50;
                            padding: 15px;
                            margin: 20px 0;
                        }
                        .info-item {
                            margin: 10px 0;
                            padding: 8px 0;
                        }
                        .info-label {
                            font-weight: bold;
                            color: #666;
                            display: inline-block;
                            width: 120px;
                        }
                        .info-value {
                            color: #333;
                        }
                        .footer {
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #e0e0e0;
                            color: #999;
                            font-size: 12px;
                            text-align: center;
                        }
                        .highlight {
                            color: #4CAF50;
                            font-weight: bold;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>新投稿通知</h1>
                        </div>
                        <div class="content">
                            <p>编辑部您好，</p>
                            <p>本期刊收到一篇新的投稿，相关信息如下：</p>
                            
                            <div class="info-box">
                                <div class="info-item">
                                    <span class="info-label">文章标题：</span>
                                    <span class="info-value highlight">%s</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">稿件编号：</span>
                                    <span class="info-value highlight">%s</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">投稿人：</span>
                                    <span class="info-value">%s</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">投稿邮箱：</span>
                                    <span class="info-value">%s</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">投稿时间：</span>
                                    <span class="info-value">%s</span>
                                </div>
                            </div>
                            
                            <p>请尽快处理该稿件，稿件编号：<strong class="highlight">%s</strong></p>
                            
                            <p>此致</p>
                            <p>Journal of Science and Technology</p>
                        </div>
                        <div class="footer">
                            <p>此邮件由系统自动发送，请勿直接回复。</p>
                            <p>@copyright Journal of Science and Technology</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                        escapeHtml(articleTitle),
                        escapeHtml(manuscriptId),
                        escapeHtml(submitterName),
                        escapeHtml(submitterEmail),
                        escapeHtml(submitTime),
                        escapeHtml(manuscriptId)
                );
    }

    /**
     * HTML转义
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

