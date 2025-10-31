package com.kstt.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kstt.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统文件实体类（所有文件上传共用）
 *
 * @author kstt
 * @date 2025/10/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class FileUtils extends BaseEntity {

    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径（相对路径）
     */
    private String filePath;

    /**
     * 文件类型（扩展名，如pdf, doc等）
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

}
