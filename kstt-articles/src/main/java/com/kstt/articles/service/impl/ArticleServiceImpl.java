package com.kstt.articles.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kstt.articles.dto.JournalArticleCount;
import com.kstt.articles.entity.*;
import com.kstt.articles.mapper.*;
import com.kstt.articles.service.ArticleService;
import com.kstt.journal.entity.Journal;
import com.kstt.journal.mapper.JournalMapper;
import com.kstt.sys.entity.SysUser;
import com.kstt.sys.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文章Service实现类
 * 
 * @author kstt
 * @date 2025-01-01
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private JournalMapper journalMapper;
    
    @Autowired
    private ArticleStatusMapper articleStatusMapper;
    
    @Autowired
    private ArticleManuscriptTypeMapper articleManuscriptTypeMapper;
    
    @Autowired
    private ArticleSubmissionTypeMapper articleSubmissionTypeMapper;
    
    @Autowired
    private ArticleAuthorMapper articleAuthorMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    private void enrichArticles(List<Article> articles, boolean loadAuthors) {
        if (articles == null || articles.isEmpty()) {
            return;
        }

        Set<Long> journalIds = articles.stream()
                .map(Article::getArticleJournalId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Journal> journalMap = journalIds.isEmpty() ? Collections.emptyMap()
                : journalMapper.selectBatchIds(journalIds).stream()
                .collect(Collectors.toMap(Journal::getJournalId, Function.identity()));

        Set<Integer> statusIds = articles.stream()
                .map(Article::getArticleStatusId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, ArticleStatus> statusMap = statusIds.isEmpty() ? Collections.emptyMap()
                : articleStatusMapper.selectBatchIds(statusIds).stream()
                .collect(Collectors.toMap(ArticleStatus::getStatusId, Function.identity()));

        Set<Integer> manuscriptTypeIds = articles.stream()
                .map(Article::getArticleManuscriptTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, ArticleManuscriptType> manuscriptTypeMap = manuscriptTypeIds.isEmpty() ? Collections.emptyMap()
                : articleManuscriptTypeMapper.selectBatchIds(manuscriptTypeIds).stream()
                .collect(Collectors.toMap(ArticleManuscriptType::getTypeId, Function.identity()));

        Set<Integer> submissionTypeIds = articles.stream()
                .map(Article::getArticleSubmissionTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, ArticleSubmissionType> submissionTypeMap = submissionTypeIds.isEmpty() ? Collections.emptyMap()
                : articleSubmissionTypeMapper.selectBatchIds(submissionTypeIds).stream()
                .collect(Collectors.toMap(ArticleSubmissionType::getTypeId, Function.identity()));

        Map<Long, List<ArticleAuthor>> articleAuthorMap = Collections.emptyMap();
        Map<Long, SysUser> userMap = Collections.emptyMap();

        if (loadAuthors) {
            List<Long> articleIds = articles.stream()
                    .map(Article::getArticleId)
                    .filter(Objects::nonNull)
                    .toList();

            if (!articleIds.isEmpty()) {
                List<ArticleAuthor> authors = articleAuthorMapper.selectAuthorsByArticleIds(articleIds);
                articleAuthorMap = authors.stream()
                        .collect(Collectors.groupingBy(ArticleAuthor::getAuthorArticleId));

                Set<Long> userIds = authors.stream()
                        .map(ArticleAuthor::getAuthorUserId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                if (!userIds.isEmpty()) {
                    userMap = sysUserMapper.selectBatchIds(userIds).stream()
                            .collect(Collectors.toMap(SysUser::getUserId, Function.identity()));
                }

                for (ArticleAuthor author : authors) {
                    if (author.getAuthorUserId() != null) {
                        author.setAuthorUser(userMap.get(author.getAuthorUserId()));
                    }
                }
            }
        }

        for (Article article : articles) {
            if (article == null) {
                continue;
            }
            if (article.getArticleJournalId() != null) {
                article.setJournal(journalMap.get(article.getArticleJournalId()));
            }
            if (article.getArticleStatusId() != null) {
                article.setArticleStatus(statusMap.get(article.getArticleStatusId()));
            }
            if (article.getArticleManuscriptTypeId() != null) {
                article.setArticleManuscriptType(manuscriptTypeMap.get(article.getArticleManuscriptTypeId()));
            }
            if (article.getArticleSubmissionTypeId() != null) {
                article.setArticleSubmissionType(submissionTypeMap.get(article.getArticleSubmissionTypeId()));
            }

            if (loadAuthors) {
                List<ArticleAuthor> authors = articleAuthorMap.getOrDefault(article.getArticleId(), Collections.emptyList());
                authors.forEach(author -> author.setAuthorArticle(article));
                article.setArticleAuthors(authors);
            }
        }
    }
    
    @Override
    public List<Article> selectArticleList(Article article) {
        List<Article> list = articleMapper.selectArticleList(article);
        enrichArticles(list, true);
        return list;
    }
    
    @Override
    public Article selectArticleByPaperId(Long paperId) {
        Article article = articleMapper.selectArticleByPaperId(paperId);
        enrichArticles(article == null ? Collections.emptyList() : List.of(article), true);
        return article;
    }
    
    @Override
    public Article selectArticleByManuscriptId(String manuscriptId) {
        Article article = articleMapper.selectArticleByManuscriptId(manuscriptId);
        enrichArticles(article == null ? Collections.emptyList() : List.of(article), true);
        return article;
    }
    
    @Override
    public int insertArticle(Article article) {
        // Generate manuscript ID if not provided
        if (article.getArticleManuscriptId() == null || article.getArticleManuscriptId().isEmpty()) {
            String manuscriptId = generateManuscriptId();
            article.setArticleManuscriptId(manuscriptId);
        }
        return articleMapper.insertArticle(article);
    }
    
    /**
     * Generate manuscript ID in format: JST-YYYY-NNNN
     * e.g., JST-2025-0001, JST-2025-0002, etc.
     */
    private String generateManuscriptId() {
        int currentYear = LocalDate.now().getYear();
        
        // Find the max sequence number for the current year
        String pattern = "JST-" + currentYear + "-%";
        List<Article> articles = articleMapper.selectByManuscriptIdPattern(pattern);
        
        int maxSeq = 0;
        for (Article article : articles) {
            String manuscriptId = article.getArticleManuscriptId();
            if (manuscriptId != null && manuscriptId.startsWith("JST-" + currentYear + "-")) {
                try {
                    // Extract sequence number: "JST-2025-" has length 9
                    String seqStr = manuscriptId.substring(9);
                    int seq = Integer.parseInt(seqStr);
                    maxSeq = Math.max(maxSeq, seq);
                } catch (NumberFormatException e) {
                    // Skip invalid format
                }
            }
        }
        
        // Generate next sequence number (4 digits)
        int nextSeq = maxSeq + 1;
        String seqStr = String.format("%04d", nextSeq);
        
        return "JST-" + currentYear + "-" + seqStr;
    }
    
    @Override
    public int updateArticle(Article article) {
        return articleMapper.updateArticle(article);
    }
    
    @Override
    public int deleteArticleByIds(Long[] paperIds) {
        return articleMapper.deleteArticleByIds(paperIds);
    }
    
    @Override
    public int deleteArticleById(Long paperId) {
        return articleMapper.deleteArticleById(paperId);
    }

    @Override
    public Map<Long, Long> countByJournalIds(List<Long> journalIds) {
        if (CollectionUtils.isEmpty(journalIds)) {
            return Collections.emptyMap();
        }
        List<JournalArticleCount> counts = articleMapper.countByJournalIds(journalIds);
        return counts.stream()
                .collect(Collectors.toMap(JournalArticleCount::getJournalId, JournalArticleCount::getArticleCount));
    }

    @Override
    public List<Article> selectArticleLastList(Integer limits) {
        List<Article> list = articleMapper.selectArticleLastList(limits);
        enrichArticles(list, true);
        return list;
    }
}