package org.example.server.controller;

import org.example.server.common.core.domain.AjaxResult;
import org.example.server.domain.AlgorithmPerformance;
import org.example.server.domain.DashboardStats;
import org.example.server.domain.DetectionTask;
import org.example.server.domain.SystemResources;
import org.example.server.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    @GetMapping("/stats")
    public AjaxResult getStats() {
        DashboardStats stats = dashboardService.getDashboardStats();
        return AjaxResult.success("获取统计数据成功", stats);
    }

    @GetMapping("/resources")
    public AjaxResult getResources() {
        SystemResources resources = dashboardService.getSystemResources();
        return AjaxResult.success("获取资源数据成功", resources);
    }

    @GetMapping("/performance")
    public AjaxResult getPerformance() {
        AlgorithmPerformance perf = dashboardService.getAlgorithmPerformance();
        return AjaxResult.success("获取性能数据成功", perf);
    }

    @GetMapping("/tasks")
    public AjaxResult getRecentTasks() {
        List<DetectionTask> tasks = dashboardService.getRecentTasks();
        return AjaxResult.success("获取任务列表成功", tasks);
    }

    @GetMapping("/overview")
    public AjaxResult getOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("stats", dashboardService.getDashboardStats());
        overview.put("resources", dashboardService.getSystemResources());
        overview.put("performance", dashboardService.getAlgorithmPerformance());
        overview.put("tasks", dashboardService.getRecentTasks());
        return AjaxResult.success("获取概览数据成功", overview);
    }
}
