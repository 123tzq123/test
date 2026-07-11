package com.trade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trade.constant.AttributeConst;
import com.trade.domain.Report;
import com.trade.dto.ReportDTO;
import com.trade.service.ReportService;
import com.trade.vo.ResultVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Resource
    private ReportService reportService;

    @PostMapping("/add")
    public ResultVO<Void> addReport(@Validated @RequestBody ReportDTO dto, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        System.out.println("获取到的userId = " + userId);
        reportService.addReport(dto,userId);
        return ResultVO.success(null);
    }
    //管理员查看举报列表
    @GetMapping("/list")
    public ResultVO<Page<Report>> getReportList(Integer pageNum,Integer pageSize){
        Page<Report> page = reportService.getReportList(pageNum,pageSize);
        return ResultVO.success(page);
    }
    @PostMapping("/handle/{reportId}")
    public ResultVO<Void> handle(@PathVariable  Long reportId){
        reportService.handleReport(reportId);
        return ResultVO.success(null);
    }
}
