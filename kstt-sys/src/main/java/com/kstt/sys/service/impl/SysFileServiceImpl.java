package com.kstt.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kstt.common.core.domain.FileUtils;
import com.kstt.sys.mapper.SysFileMapper;
import com.kstt.sys.service.SysFileService;
import org.springframework.stereotype.Service;

/**
 * 文件Service实现类
 * 
 * @author kstt
 * @date 2025-01-01
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, FileUtils> implements SysFileService {
}

