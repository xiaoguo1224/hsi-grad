package org.example.server.service;

import org.example.server.domain.AlgorithmPerformance;
import org.example.server.domain.DashboardStats;
import org.example.server.domain.DetectionTask;
import org.example.server.domain.SystemResources;

import java.util.List;

public interface IDashboardService {
    DashboardStats getDashboardStats();
    SystemResources getSystemResources();
    AlgorithmPerformance getAlgorithmPerformance();
    List<DetectionTask> getRecentTasks();
}
