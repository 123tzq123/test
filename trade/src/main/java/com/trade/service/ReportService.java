package com.trade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.Report;
import com.trade.dto.ReportDTO;

public interface ReportService extends IService<Report> {
    //提交举报
    void addReport(ReportDTO dto, Long reportUserId);
    //管理员分页查看举报列表
    Page<Report> getReportList(Integer pageNum,Integer pageSize);
    //处理举报
    void handleReport(Long reportId);
}