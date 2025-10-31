package com.kstt.admin.service.mail;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param text    邮件内容
     */
    void sendSimpleEmail(String to, String subject, String text);

    /**
     * 发送HTML邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param html    邮件HTML内容
     */
    void sendHtmlEmail(String to, String subject, String html);

    /**
     * 发送HTML邮件（支持抄送）
     *
     * @param to      收件人邮箱
     * @param cc      抄送邮箱（多个用逗号分隔）
     * @param subject 邮件主题
     * @param html    邮件HTML内容
     */
    void sendHtmlEmail(String to, String cc, String subject, String html);

    /**
     * 发送投稿确认邮件
     *
     * @param articleTitle   文章标题
     * @param manuscriptId   稿件编号
     * @param submitterName  投稿人姓名
     * @param submitterEmail 投稿人邮箱
     * @param submitTime     投稿时间
     */
    void sendArticleSubmissionEmail(String articleTitle, String manuscriptId,
                                     String submitterName, String submitterEmail,
                                     String submitTime);
}

