package com.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.Report;
import com.trade.dto.ReportDTO;
import com.trade.mapper.ReportMapper;
import com.trade.service.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {
    @Override
    public void addReport(ReportDTO dto, Long reportUserId) {
        Report report = new Report();
        report.setGoodsId(dto.getGoodsId());
        report.setReportUserId(reportUserId);
        report.setReason(dto.getReason());
        report.setHandleStatus(0);
        this.save(report);
    }

    @Override
    public Page<Report> getReportList(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Report::getCreateTime);
        Page<Report> page = new Page<>(pageNum,pageSize);
        return this.page(page,wrapper);
    }

    @Override
    public void handleReport(Long reportId) {
        Report report = this.getById(reportId);
        report.setHandleStatus(1);
        this.updateById(report);
    }
}