package com.kstt.articles.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kstt.articles.dto.JournalArticleCount;
import com.kstt.articles.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章Mapper接口
 * 
 * @author kstt
 * @date 2025-01-01
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    
    /**
     * 查询文章列表
     * 
     * @param article 文章信息
     * @return 文章集合
     */
    List<Article> selectArticleList(Article article);
    
    /**
     * 根据文章ID查询文章信息
     * 
     * @param paperId 文章ID
     * @return 文章信息
     */
    Article selectArticleByPaperId(@Param("paperId") Long paperId);
    
    /**
     * 根据稿件编号查询文章信息
     * 
     * @param manuscriptId 稿件编号
     * @return 文章信息
     */
    Article selectArticleByManuscriptId(@Param("manuscriptId") String manuscriptId);
    
    /**
     * 新增文章
     * 
     * @param article 文章信息
     * @return 结果
     */
    int insertArticle(Article article);
    
    /**
     * 修改文章
     * 
     * @param article 文章信息
     * @return 结果
     */
    int updateArticle(Article article);
    
    /**
     * 删除文章
     * 
     * @param paperId 文章ID
     * @return 结果
     */
    int deleteArticleById(@Param("paperId") Long paperId);
    
    /**
     * 批量删除文章
     * 
     * @param paperIds 需要删除的文章ID
     * @return 结果
     */
    int deleteArticleByIds(@Param("paperIds") Long[] paperIds);

    /**
     * 统计期刊下文章数量
     */
    List<JournalArticleCount> countByJournalIds(@Param("journalIds") List<Long> journalIds);

    /**
     * 查询最新文章列表
     *
     * @param limits 查询数量
     * @return 文章集合
     */
    List<Article> selectArticleLastList(@Param("limits") Integer limits);

    /**
     * 根据稿件编号模式查询文章
     *
     * @param pattern 模式（如：JST-2025-%）
     * @return 文章集合
     */
    List<Article> selectByManuscriptIdPattern(@Param("pattern") String pattern);
}
