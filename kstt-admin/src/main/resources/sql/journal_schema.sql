DROP DATABASE IF EXISTS journal;
CREATE DATABASE IF NOT EXISTS journal;
USE journal;

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 基础表
-- ============================================

-- 1.1 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    role_id    INT         NOT NULL COMMENT '角色id' PRIMARY KEY,
    role_name  VARCHAR(15) NOT NULL COMMENT '角色名称',
    role_sort  INT         NOT NULL COMMENT '排序规则',
    role_scope INT         NOT NULL COMMENT '角色权限范围(1.投稿、2.审稿、3.最终决定、0.管理员)',
    role_status TINYINT(1) DEFAULT 1 NULL COMMENT '状态(0：禁用，1：启用)',
    create_by  VARCHAR(63) NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by  VARCHAR(63) NULL,
    update_time DATETIME NULL,
    remark     VARCHAR(255) NULL
) COMMENT '用户角色表';

-- 1.2 学科领域表
CREATE TABLE IF NOT EXISTS sys_subject (
    subject_id   INT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(63)                        NOT NULL,
    create_by    VARCHAR(31)                        NULL,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by    VARCHAR(31)                        NULL,
    update_time  DATETIME                           NULL,
    CONSTRAINT sys_subject_pk UNIQUE (subject_id),
    CONSTRAINT sys_subject_name UNIQUE (subject_name)
);

-- 1.3 学科领域关联表
CREATE TABLE IF NOT EXISTS sys_area (
    area_id         INT           NOT NULL PRIMARY KEY,
    area_name       VARCHAR(127)   NOT NULL,
    area_subject_id INT           NULL,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    create_by       VARCHAR(31)    NULL,
    update_time     DATETIME       NULL,
    update_by       VARCHAR(31)    NULL,
    CONSTRAINT sys_area_pk UNIQUE (area_name),
    CONSTRAINT sys_area_sys_subject_subject_id_fk
        FOREIGN KEY (area_subject_id) REFERENCES sys_subject (subject_id)
) COMMENT '学科领域';

-- 1.4 文章状态表
CREATE TABLE IF NOT EXISTS article_status (
    status_id   INT         NOT NULL PRIMARY KEY COMMENT '状态ID',
    status_name VARCHAR(31) NOT NULL COMMENT '状态名称',
    status_desc VARCHAR(127) NULL COMMENT '状态描述',
    sort_order  INT         NOT NULL DEFAULT 0 COMMENT '排序'
) COMMENT '文章状态表';

-- 1.5 稿件类型表
CREATE TABLE IF NOT EXISTS article_manuscript_type (
    type_id     INT         NOT NULL PRIMARY KEY COMMENT '类型ID',
    type_name   VARCHAR(31) NOT NULL COMMENT '类型名称',
    type_desc   VARCHAR(127) NULL COMMENT '类型描述',
    sort_order  INT         NOT NULL DEFAULT 0 COMMENT '排序'
) COMMENT '稿件类型表';

-- 1.6 提交类型表
CREATE TABLE IF NOT EXISTS article_submission_type (
    type_id     INT         NOT NULL PRIMARY KEY COMMENT '类型ID',
    type_name   VARCHAR(31) NOT NULL COMMENT '类型名称',
    type_desc   VARCHAR(127) NULL COMMENT '类型描述',
    sort_order  INT         NOT NULL DEFAULT 0 COMMENT '排序'
) COMMENT '提交类型表';

-- 1.7 文件类型表
CREATE TABLE IF NOT EXISTS article_file_type (
    type_id     INT         NOT NULL PRIMARY KEY COMMENT '类型ID',
    type_name   VARCHAR(31) NOT NULL COMMENT '类型名称',
    type_desc   VARCHAR(127) NULL COMMENT '类型描述',
    sort_order  INT         NOT NULL DEFAULT 0 COMMENT '排序'
) COMMENT '文件类型表';

-- 1.8 系统文件表
CREATE TABLE IF NOT EXISTS sys_file (
    file_id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    file_name   VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path   VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type   VARCHAR(50)  NULL COMMENT '文件类型',
    file_size   BIGINT       NULL COMMENT '文件大小(字节)',
    create_by   VARCHAR(63)  NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by   VARCHAR(63)  NULL,
    update_time DATETIME     NULL,
    remark      VARCHAR(255) NULL
) COMMENT '系统文件表';

-- 1.9 学术学位表
CREATE TABLE IF NOT EXISTS sys_user_academic_degree (
    degree_id   INT         NOT NULL PRIMARY KEY COMMENT '学位ID',
    degree_name VARCHAR(31) NOT NULL COMMENT '学位名称',
    degree_level VARCHAR(31) NOT NULL COMMENT '学位级别',
    sort_order  INT         NOT NULL DEFAULT 0 COMMENT '排序'
) COMMENT '学术学位表';

-- ============================================
-- 2. 核心业务表
-- ============================================

-- 2.1 期刊基本信息表
CREATE TABLE IF NOT EXISTS sys_info (
    info_id            INT AUTO_INCREMENT COMMENT '期刊信息编号' PRIMARY KEY,
    info_issn          VARCHAR(31)                        NULL COMMENT 'ISSN编码',
    info_name          VARCHAR(63)                        NOT NULL COMMENT '期刊名',
    info_simple_name   VARCHAR(31)                        NULL COMMENT '期刊简称',
    info_comment       VARCHAR(127)                       NOT NULL COMMENT '期刊简述',
    info_mission       TEXT                               NULL COMMENT '创刊目标',
    info_impact_factor FLOAT                              NULL COMMENT '影响因子',
    info_founded_year  INT                                NULL COMMENT '创刊年份',
    info_publisher     VARCHAR(255)                       NULL COMMENT '期刊出版商',
    info_website       VARCHAR(255)                       NULL COMMENT '官网地址',
    info_email         VARCHAR(63)                        NULL COMMENT '联系邮箱',
    info_phone         VARCHAR(31)                        NULL COMMENT '联系电话',
    info_address       VARCHAR(127)                       NULL COMMENT '联系地址',
    info_logo          VARCHAR(255)                       NULL COMMENT '期刊Logo',
    info_publisher_logo VARCHAR(255)                       NULL COMMENT '出版商Logo',
    create_by          VARCHAR(63)                        NULL,
    create_time        DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by          VARCHAR(63)                        NULL,
    update_time        DATETIME                           NULL,
    remark             VARCHAR(255)                       NULL COMMENT '备注',
    CONSTRAINT sys_info_pk UNIQUE (info_issn, info_name)
) COMMENT '期刊信息';

-- 2.2 期刊卷期信息表
CREATE TABLE IF NOT EXISTS journal (
    journal_id           INT AUTO_INCREMENT PRIMARY KEY,
    journal_volume       INT                                NOT NULL COMMENT '期刊卷次',
    journal_issue        INT                                NOT NULL COMMENT '期号',
    journal_publish_date DATE                               NOT NULL COMMENT '出版日期',
    journal_status       TINYINT(1) DEFAULT 0                NULL COMMENT '状态(0：待发布，1：已发布)',
    journal_theme        VARCHAR(127)                       NULL COMMENT '本期主题',
    journal_description  TEXT                               NULL COMMENT '本期简介',
    journal_cover        VARCHAR(255)                       NULL COMMENT '封面图片',
    journal_pdf          VARCHAR(255)                       NULL COMMENT '整期PDF文件',
    create_by            VARCHAR(63)                        NULL,
    create_time          DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by            VARCHAR(63)                        NULL,
    update_time          DATETIME                           NULL,
    remark               VARCHAR(255)                       NULL COMMENT '注释',
    CONSTRAINT journal_pk UNIQUE (journal_id),
    CONSTRAINT journal_volume UNIQUE (journal_volume)
) COMMENT '期刊卷期信息';

-- 2.3 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    user_id          INT AUTO_INCREMENT PRIMARY KEY,
    user_name        VARCHAR(63)                        NOT NULL COMMENT '用户名',
    user_email       VARCHAR(127)                       NOT NULL COMMENT '邮箱',
    user_password    VARCHAR(255)                       NOT NULL COMMENT '密码',
    user_real_name   VARCHAR(127)                       NOT NULL COMMENT '真实姓名',
    user_affiliation VARCHAR(255)                       NOT NULL COMMENT '所属机构',
    user_department  VARCHAR(127)                       NULL COMMENT '所属学院/研究所',
    user_title       VARCHAR(63)                        NULL COMMENT '职称',
    user_position    VARCHAR(63)                        NULL COMMENT '职位',
    user_phone       VARCHAR(31)                        NULL COMMENT '联系电话',
    user_address     VARCHAR(255)                       NULL COMMENT '通讯地址',
    user_country     VARCHAR(63)                        NOT NULL COMMENT '国家',
    user_state       VARCHAR(63)                        NULL COMMENT '州/省',
    user_city        VARCHAR(63)                        NULL COMMENT '城市',
    user_postal_code VARCHAR(15)                        NULL COMMENT '邮政编码',
    user_role_id     INT                                NOT NULL COMMENT '角色ID',
    user_avatar      VARCHAR(255) DEFAULT '/images/default-avatar.png' COMMENT '头像',
    user_status      TINYINT(1) DEFAULT 1                NULL COMMENT '状态(0：禁用，1：启用)',
    user_email_verified TINYINT(1) DEFAULT 0              NULL COMMENT '邮箱验证状态(0：未验证，1：已验证)',
    user_last_login  DATETIME                           NULL COMMENT '最后登录时间',
    user_orcid       VARCHAR(63)                        NULL COMMENT 'ORCID标识符',
    user_biography   TEXT                               NULL COMMENT '个人简介',
    user_classification VARCHAR(127)                       NULL COMMENT '研究领域',
    user_keywords VARCHAR(255)                          NOT NULL COMMENT '个人研究方向关键词',
    user_detail VARCHAR(255)                       NULL COMMENT '用户描述',
    create_by        VARCHAR(63)                        NULL,
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by        VARCHAR(63)                        NULL,
    update_time      DATETIME                           NULL,
    remark           VARCHAR(255)                       NULL COMMENT '备注',
    CONSTRAINT sys_user_name UNIQUE (user_name),
    CONSTRAINT sys_user_email UNIQUE (user_email),
    CONSTRAINT fk_user_role FOREIGN KEY (user_role_id) REFERENCES sys_role(role_id)
) COMMENT '用户表';

-- 2.4 论文表
CREATE TABLE IF NOT EXISTS article (
    article_id                INT AUTO_INCREMENT COMMENT '论文id' PRIMARY KEY,
    article_manuscript_id     VARCHAR(63)                        NULL COMMENT '稿件编号（格式：JST-YYYY-NNNN）',
    article_title             VARCHAR(255)                       NOT NULL COMMENT '论文标题',
    article_abstract          TEXT                               NULL COMMENT '摘要',
    article_keywords          VARCHAR(511)                       NULL COMMENT '关键词（多个用分号分隔）',
    article_manuscript_type_id INT                              NULL COMMENT '稿件类型ID',
    article_journal_id        INT                                NULL COMMENT '关联的卷次信息',
    article_pages             VARCHAR(31)                        NULL COMMENT '页码',
    article_word_count        INT                                NULL COMMENT '字数',
    article_figure_count      INT                                NULL COMMENT '图片数量',
    article_table_count       INT                                NULL COMMENT '表格数量',
    article_submit_time       DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '首次提交时间',
    article_accept_time       DATETIME                           NULL COMMENT '论文接收时间',
    article_publish_time      DATETIME                           NULL COMMENT '论文发表时间',
    article_online_time       DATETIME                           NULL COMMENT '在线发表时间',
    article_doi               VARCHAR(63)                        NULL COMMENT 'DOI号',
    article_pdf               VARCHAR(255)                       NULL COMMENT '论文PDF文件',
    article_status_id         INT                                DEFAULT 0 COMMENT '状态ID',
    article_submission_type_id INT                              NULL COMMENT '提交类型ID',
    article_cover_letter      TEXT                               NULL COMMENT '投稿信',
    article_type_id           INT                                NULL COMMENT '文章类型ID',
    article_is_open_access    TINYINT(1) DEFAULT 0               NULL COMMENT '是否开放获取(0：否，1：是)',
    article_subject_areas     VARCHAR(255)                       NULL COMMENT '学科领域IDs（多个用逗号分隔）',
    create_by                 VARCHAR(63)                        NULL,
    create_time               DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by                 VARCHAR(63)                        NULL,
    update_time               DATETIME                           NULL,
    remark                    VARCHAR(255)                       NULL,
    CONSTRAINT journal_article_pk UNIQUE (article_id),
    CONSTRAINT journal_article_manuscript UNIQUE (article_manuscript_id),
    CONSTRAINT fk_article_journal FOREIGN KEY (article_journal_id) REFERENCES journal(journal_id),
    CONSTRAINT fk_article_manuscript_type FOREIGN KEY (article_manuscript_type_id) REFERENCES article_manuscript_type(type_id),
    CONSTRAINT fk_article_status FOREIGN KEY (article_status_id) REFERENCES article_status(status_id),
    CONSTRAINT fk_article_submission_type FOREIGN KEY (article_submission_type_id) REFERENCES article_submission_type(type_id)
) COMMENT '论文表';

-- 2.5 论文作者关联表（只保留论文相关信息）
CREATE TABLE IF NOT EXISTS article_author (
    author_id                INT AUTO_INCREMENT PRIMARY KEY,
    author_article_id        INT                                NOT NULL COMMENT '关联论文ID',
    author_user_id           INT                                NOT NULL COMMENT '关联用户ID',
    author_order             INT                                NOT NULL COMMENT '作者顺序（1为第一作者）',
    author_is_corresponding  TINYINT(1) DEFAULT 0               NOT NULL COMMENT '是否为通讯作者(0：否，1：是)',
    author_contribution      TEXT                               NULL COMMENT '作者贡献描述',
    create_by                VARCHAR(63)                        NULL,
    create_time              DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by                VARCHAR(63)                        NULL,
    update_time              DATETIME                           NULL,
    remark                   VARCHAR(255)                       NULL,
    CONSTRAINT fk_author_article FOREIGN KEY (author_article_id) REFERENCES article(article_id) ON DELETE CASCADE,
    CONSTRAINT fk_author_user FOREIGN KEY (author_user_id) REFERENCES sys_user(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_author_article_user UNIQUE (author_article_id, author_user_id)
) COMMENT '论文作者关联表';

-- 2.7 论文处理流程表
CREATE TABLE IF NOT EXISTS article_process (
    process_id             INT AUTO_INCREMENT PRIMARY KEY,
    process_article_id      INT                                NOT NULL COMMENT '关联的article_id',
    process_current_status_id INT                            NOT NULL COMMENT '当前状态ID',
    process_previous_status_id INT                           NULL COMMENT '前一状态ID',
    process_editor_id      INT                                NULL COMMENT '负责编辑ID',
    process_assign_time    DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '分配时间',
    process_due_date       DATETIME                           NULL COMMENT '截止日期',
    process_priority       TINYINT(1) DEFAULT 1               NULL COMMENT '优先级(1：低，2：中，3：高)',
    process_notes           TEXT                               NULL COMMENT '处理备注',
    process_decision        VARCHAR(63)                        NULL COMMENT '最终决定(accept, reject, revise)',
    process_decision_reason TEXT                               NULL COMMENT '决定原因',
    process_decision_time   DATETIME                           NULL COMMENT '决定时间',
    create_by               VARCHAR(63)                        NULL,
    create_time             DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by               VARCHAR(63)                        NULL,
    update_time             DATETIME                           NULL,
    remark                  VARCHAR(255)                       NULL,
    CONSTRAINT journal_process_pk UNIQUE (process_id),
    CONSTRAINT fk_process_article FOREIGN KEY (process_article_id) REFERENCES article(article_id) ON DELETE CASCADE,
    CONSTRAINT fk_process_editor FOREIGN KEY (process_editor_id) REFERENCES sys_user(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_process_current_status FOREIGN KEY (process_current_status_id) REFERENCES article_status(status_id),
    CONSTRAINT fk_process_previous_status FOREIGN KEY (process_previous_status_id) REFERENCES article_status(status_id)
) COMMENT '论文处理流程表';

-- 2.7 编辑团队表
CREATE TABLE IF NOT EXISTS about_editorial_team (
    team_id        INT AUTO_INCREMENT PRIMARY KEY,
    team_user_id   INT                                NOT NULL COMMENT '关联用户ID',
    team_title     VARCHAR(63)                        NULL COMMENT '编辑团队中的职位',
    team_order     INT DEFAULT 0                      NULL COMMENT '排序',
    team_status    TINYINT(1) DEFAULT 1                NULL COMMENT '状态(0：禁用，1：启用)',
    create_by      VARCHAR(63)                        NULL,
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by      VARCHAR(63)                        NULL,
    update_time    DATETIME                           NULL,
    remark         VARCHAR(255)                       NULL,
    CONSTRAINT fk_team_user FOREIGN KEY (team_user_id) REFERENCES sys_user(user_id)
) COMMENT '编辑团队表';

-- 2.8 评审邀请表
CREATE TABLE IF NOT EXISTS article_review_invitation (
    invitation_id       INT AUTO_INCREMENT PRIMARY KEY,
    invitation_process_id INT                             NOT NULL COMMENT '关联流程ID',
    invitation_reviewer_id INT                            NOT NULL COMMENT '评审人ID',
    invitation_round    INT DEFAULT 1                     NOT NULL COMMENT '评审轮次',
    invitation_status   VARCHAR(31) DEFAULT 'pending'     NULL COMMENT '邀请状态(pending, accepted, declined, expired)',
    invitation_sent_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '发送时间',
    invitation_response_time DATETIME                     NULL COMMENT '响应时间',
    invitation_due_date DATETIME                          NULL COMMENT '评审截止日期',
    invitation_notes    TEXT                              NULL COMMENT '邀请备注',
    create_by           VARCHAR(63)                        NULL,
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by           VARCHAR(63)                       NULL,
    update_time         DATETIME                          NULL,
    remark              VARCHAR(255)                      NULL,
    CONSTRAINT fk_invitation_process FOREIGN KEY (invitation_process_id) REFERENCES article_process(process_id) ON DELETE CASCADE,
    CONSTRAINT fk_invitation_reviewer FOREIGN KEY (invitation_reviewer_id) REFERENCES sys_user(user_id) ON DELETE CASCADE
) COMMENT '评审邀请表';

-- 2.9 论文评审表
CREATE TABLE IF NOT EXISTS article_review (
    review_id              INT AUTO_INCREMENT PRIMARY KEY,
    review_invitation_id   INT                                NOT NULL COMMENT '关联邀请ID',
    review_rating          INT                                NULL COMMENT '评分(1-5)',
    review_recommendation  VARCHAR(31)                        NULL COMMENT '推荐意见(accept, reject, minor_revision, major_revision)',
    review_comments_editor TEXT                               NULL COMMENT '给编辑的意见',
    review_comments_author TEXT                               NULL COMMENT '给作者的意见',
    review_submit_time     DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '提交时间',
    review_status          TINYINT(1) DEFAULT 0               NULL COMMENT '状态(0：待评审，1：已评审)',
    review_quality_score   INT                                NULL COMMENT '论文质量评分(1-10)',
    review_innovation_score INT                               NULL COMMENT '创新性评分(1-10)',
    review_clarity_score   INT                                NULL COMMENT '清晰度评分(1-10)',
    review_significance_score INT                             NULL COMMENT '重要性评分(1-10)',
    create_by              VARCHAR(63)                        NULL,
    create_time            DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by              VARCHAR(63)                        NULL,
    update_time            DATETIME                           NULL,
    remark                 VARCHAR(255)                       NULL,
    CONSTRAINT fk_review_invitation FOREIGN KEY (review_invitation_id) REFERENCES article_review_invitation(invitation_id) ON DELETE CASCADE
) COMMENT '论文评审表';

-- ============================================
-- 3. 扩展功能表
-- ============================================

-- 3.1 提交文件表
CREATE TABLE IF NOT EXISTS article_submission_file (
    file_id           INT AUTO_INCREMENT PRIMARY KEY,
    file_article_id   INT                                NOT NULL COMMENT '关联论文ID',
    file_name         VARCHAR(255)                       NOT NULL COMMENT '文件名',
    file_original_name VARCHAR(255)                      NOT NULL COMMENT '原始文件名',
    file_path         VARCHAR(500)                       NOT NULL COMMENT '文件路径',
    file_size         BIGINT                             NOT NULL COMMENT '文件大小(字节)',
    file_type_id      INT                                NOT NULL COMMENT '文件类型ID',
    file_mime_type    VARCHAR(63)                        NULL COMMENT 'MIME类型',
    file_description  VARCHAR(255)                       NULL COMMENT '文件描述',
    file_upload_time  DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '上传时间',
    file_version      INT DEFAULT 1                      NULL COMMENT '文件版本',
    file_is_primary   TINYINT(1) DEFAULT 0               NULL COMMENT '是否为主文件(0：否，1：是)',
    create_by         VARCHAR(63)                        NULL,
    create_time       DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by         VARCHAR(63)                        NULL,
    update_time       DATETIME                           NULL,
    remark            VARCHAR(255)                       NULL,
    CONSTRAINT fk_submission_file_article FOREIGN KEY (file_article_id) REFERENCES article(article_id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_file_type FOREIGN KEY (file_type_id) REFERENCES article_file_type(type_id)
) COMMENT '提交文件表';

-- 3.2 建议审稿人表（直接关联用户表）
CREATE TABLE IF NOT EXISTS article_suggested_reviewer (
    suggested_id                INT AUTO_INCREMENT PRIMARY KEY,
    suggested_article_id        INT                                NOT NULL COMMENT '关联论文ID',
    suggested_user_id  INT                                NOT NULL COMMENT '建议的审稿人用户ID',
    suggested_reason            TEXT                               NULL COMMENT '推荐理由',
    suggested_status            TINYINT(1) DEFAULT 0                NULL COMMENT '状态(0：待处理，1：已邀请，2：已拒绝，3：已接受)',
    created_by        VARCHAR(63)                        NULL,
    created_time      DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    updated_by        VARCHAR(63)                        NULL,
    updated_time      DATETIME                           NULL,
    remark            VARCHAR(255)                       NULL,
    CONSTRAINT fk_suggested_reviewer_article FOREIGN KEY (suggested_article_id) REFERENCES article(article_id) ON DELETE CASCADE,
    CONSTRAINT fk_suggested_reviewer_user FOREIGN KEY (suggested_user_id) REFERENCES sys_user(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_suggested_reviewer UNIQUE (suggested_article_id, suggested_user_id)
) COMMENT '建议审稿人表';

-- 3.3 避免审稿人表（直接关联用户表）
CREATE TABLE IF NOT EXISTS article_opposed_reviewer (
    opposed_id                INT AUTO_INCREMENT PRIMARY KEY,
    opposed_article_id        INT                                NOT NULL COMMENT '关联论文ID',
    opposed_user_id  INT                                NOT NULL COMMENT '避免的审稿人用户ID',
    opposed_reason            TEXT                               NULL COMMENT '避免理由',
    created_by        VARCHAR(63)                        NULL,
    created_time      DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    updated_by        VARCHAR(63)                        NULL,
    updated_time      DATETIME                           NULL,
    remark            VARCHAR(255)                       NULL,
    CONSTRAINT fk_opposed_reviewer_article FOREIGN KEY (opposed_article_id) REFERENCES article(article_id) ON DELETE CASCADE,
    CONSTRAINT fk_opposed_reviewer_user FOREIGN KEY (opposed_user_id) REFERENCES sys_user(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_opposed_reviewer UNIQUE (opposed_id, opposed_user_id)
) COMMENT '避免审稿人表';

-- 3.4 文章关键词表
CREATE TABLE IF NOT EXISTS article_keywords (
    keyword_id        INT AUTO_INCREMENT PRIMARY KEY,
    keyword_article_id        INT                                NOT NULL COMMENT '关联论文ID',
    keyword           VARCHAR(100)                       NOT NULL COMMENT '关键词',
    keyword_sort_order        INT                                NOT NULL DEFAULT 0 COMMENT '排序',
    created_by        VARCHAR(63)                        NULL,
    created_time      DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT fk_article_keywords FOREIGN KEY (keyword_article_id) REFERENCES article(article_id) ON DELETE CASCADE,
    CONSTRAINT uk_article_keyword UNIQUE (keyword_article_id, keyword)
) COMMENT '文章关键词表';

-- 4.1 通知公告表
CREATE TABLE IF NOT EXISTS notice (
    notice_id      INT AUTO_INCREMENT PRIMARY KEY,
    notice_title   VARCHAR(255)                       NOT NULL COMMENT '通知标题',
    notice_content TEXT                               NULL COMMENT '通知内容',
    notice_type    VARCHAR(15)                        NULL COMMENT '通知类型',
    notice_status  TINYINT(1) DEFAULT 1               NULL COMMENT '状态(0：禁用，1：启用)',
    notice_top     TINYINT(1) DEFAULT 0               NULL COMMENT '是否置顶(0：否，1：是)',
    notice_order   INT DEFAULT 0                      NULL COMMENT '排序',
    create_by      VARCHAR(63)                        NULL,
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by      VARCHAR(63)                        NULL,
    update_time    DATETIME                           NULL,
    remark         VARCHAR(255)                       NULL
) COMMENT '通知公告表';

-- 4.2 常见问题表
CREATE TABLE IF NOT EXISTS contact_faq (
    faq_id        INT AUTO_INCREMENT PRIMARY KEY,
    faq_question  VARCHAR(255)                       NOT NULL COMMENT '问题',
    faq_answer    TEXT                               NULL COMMENT '答案',
    faq_order     INT DEFAULT 0                      NULL COMMENT '排序',
    faq_status    TINYINT(1) DEFAULT 1                NULL COMMENT '状态(0：禁用，1：启用)',
    create_by     VARCHAR(63)                        NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by     VARCHAR(63)                        NULL,
    update_time   DATETIME                           NULL,
    remark        VARCHAR(255)                       NULL
) COMMENT '常见问题表';

-- 4.3 联系消息表
CREATE TABLE IF NOT EXISTS contact_message (
    message_id      INT AUTO_INCREMENT PRIMARY KEY,
    message_name    VARCHAR(63)                        NOT NULL COMMENT '姓名',
    message_email   VARCHAR(127)                       NOT NULL COMMENT '邮箱',
    message_subject VARCHAR(255)                       NULL COMMENT '主题',
    message_content TEXT                               NOT NULL COMMENT '内容',
    message_status  TINYINT(1) DEFAULT 0                NULL COMMENT '状态(0：未处理，1：已处理)',
    message_reply   TEXT                               NULL COMMENT '回复内容',
    create_by       VARCHAR(63)                        NULL,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
    update_by       VARCHAR(63)                        NULL,
    update_time     DATETIME                           NULL,
    remark          VARCHAR(255)                       NULL
) COMMENT '联系消息表';

-- 5.1 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    menu_id        INT                                   NOT NULL PRIMARY KEY,
    menu_name      VARCHAR(31)                           NOT NULL COMMENT '导航栏信息',
    menu_parent_id INT                                   NOT NULL COMMENT '父页面编号',
    menu_order_num INT                                   NOT NULL COMMENT '排序规则',
    menu_path      VARCHAR(31)                           NOT NULL COMMENT '组件地址',
    menu_component VARCHAR(31)                           NOT NULL COMMENT '路由地址',
    menu_type      VARCHAR(1) DEFAULT 'M'                NOT NULL COMMENT '菜单类型(M：目录，C：菜单，F：按钮)',
    menu_visible   TINYINT(1) DEFAULT 0                  NOT NULL COMMENT '是否显示(0：隐藏，1：显示)',
    menu_status    TINYINT(1) DEFAULT 1                  NOT NULL COMMENT '状态(0：禁用，1：启用)',
    menu_perms     VARCHAR(63)                           NULL COMMENT '权限标识',
    menu_icon      VARCHAR(31) DEFAULT '#'               NULL COMMENT '菜单图标',
    create_by      VARCHAR(63)                           NULL,
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP    NULL,
    update_by      VARCHAR(63)                           NULL,
    update_time    DATETIME                              NULL,
    remark         VARCHAR(255)                          NULL
) COMMENT '导航栏';

-- ============================================
-- 6. 创建索引
-- ============================================

-- 论文相关索引
CREATE INDEX idx_article_journal_id ON article(article_journal_id);
CREATE INDEX idx_article_status_id ON article(article_status_id);
CREATE INDEX idx_article_submit_time ON article(article_submit_time);
CREATE INDEX idx_article_manuscript_id ON article(article_manuscript_id);
CREATE INDEX idx_article_doi ON article(article_doi);
CREATE INDEX idx_article_manuscript_type_id ON article(article_manuscript_type_id);
CREATE INDEX idx_article_submission_type_id ON article(article_submission_type_id);

-- 作者相关索引
CREATE INDEX idx_author_article_id ON article_author(author_article_id);
CREATE INDEX idx_author_user_id ON article_author(author_user_id);
CREATE INDEX idx_author_order ON article_author(author_order);

-- 流程相关索引
CREATE INDEX idx_process_article_id ON article_process(process_article_id);
CREATE INDEX idx_process_current_status_id ON article_process(process_current_status_id);
CREATE INDEX idx_process_editor_id ON article_process(process_editor_id);
CREATE INDEX idx_process_assign_time ON article_process(process_assign_time);

-- 评审邀请索引
CREATE INDEX idx_invitation_process_id ON article_review_invitation(invitation_process_id);
CREATE INDEX idx_invitation_reviewer_id ON article_review_invitation(invitation_reviewer_id);
CREATE INDEX idx_invitation_status ON article_review_invitation(invitation_status);
CREATE INDEX idx_invitation_due_date ON article_review_invitation(invitation_due_date);

-- 评审相关索引
CREATE INDEX idx_review_invitation_id ON article_review(review_invitation_id);
CREATE INDEX idx_review_status ON article_review(review_status);
CREATE INDEX idx_review_submit_time ON article_review(review_submit_time);

-- 用户相关索引
CREATE INDEX idx_user_role ON sys_user(user_role_id);
CREATE INDEX idx_user_email ON sys_user(user_email);
CREATE INDEX idx_user_status ON sys_user(user_status);
CREATE INDEX idx_user_last_login ON sys_user(user_last_login);

-- 其他索引
CREATE INDEX idx_notice_status ON notice(notice_status);
CREATE INDEX idx_notice_top ON notice(notice_top);
CREATE INDEX idx_team_user_id ON about_editorial_team(team_user_id);
CREATE INDEX idx_team_status ON about_editorial_team(team_status);
CREATE INDEX idx_message_status ON contact_message(message_status);
CREATE INDEX idx_faq_status ON contact_faq(faq_status);
CREATE INDEX idx_faq_order ON contact_faq(faq_order);

-- 新增表索引
CREATE INDEX idx_submission_file_article_id ON article_submission_file(file_article_id);
CREATE INDEX idx_submission_file_type_id ON article_submission_file(file_type_id);
CREATE INDEX idx_submission_file_upload_time ON article_submission_file(file_upload_time);
CREATE INDEX idx_sys_file_type ON sys_file(file_type);
CREATE INDEX idx_suggested_reviewer_article_id ON article_suggested_reviewer(suggested_id);
CREATE INDEX idx_suggested_reviewer_user_id ON article_suggested_reviewer(suggested_user_id);
CREATE INDEX idx_suggested_reviewer_status ON article_suggested_reviewer(suggested_status);
CREATE INDEX idx_opposed_reviewer_article_id ON article_opposed_reviewer(opposed_article_id);
CREATE INDEX idx_opposed_reviewer_user_id ON article_opposed_reviewer(opposed_user_id);
CREATE INDEX idx_article_keywords_article_id ON article_keywords(keyword_article_id);
CREATE INDEX idx_article_keywords_keyword ON article_keywords(keyword);

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;
