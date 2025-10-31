package com.kstt.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kstt.common.core.domain.FileUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件Mapper接口
 * 
 * @author kstt
 * @date 2025-01-01
 */
@Mapper
public interface SysFileMapper extends BaseMapper<FileUtils> {
}

