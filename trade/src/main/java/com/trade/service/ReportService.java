package com.trade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.Report;
import com.trade.dto.ReportDTO;

public interface ReportService extends IService<Report> {
    void addReport(ReportDTO dto, Long reportUserId);
    Page<Report> getReportList(Integer pageNum,Integer pageSize);
    void handleReport(Long reportId);
}