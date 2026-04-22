package org.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.server.domain.*;
import org.example.server.mapper.DetectionTaskMapper;
import org.example.server.mapper.HsiFileMapper;
import org.example.server.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private DetectionTaskMapper detectionTaskMapper;

    @Autowired
    private HsiFileMapper hsiFileMapper;

    private final Random random = new Random();

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        Long cubeCount = hsiFileMapper.selectCount(
            new LambdaQueryWrapper<HsiFile>().eq(HsiFile::getFileType, "mat")
        );
        stats.setTotalCubes(cubeCount != null ? cubeCount : 0L);

        List<DetectionTask> completedTasks = detectionTaskMapper.selectList(
            new LambdaQueryWrapper<DetectionTask>().eq(DetectionTask::getStatus, 2)
        );

        if (!completedTasks.isEmpty()) {
            double avgF1 = completedTasks.stream()
                .filter(t -> t.getF1() != null)
                .mapToDouble(DetectionTask::getF1)
                .average()
                .orElse(0.72);
            stats.setF1Score(Math.round(avgF1 * 100.0) / 100.0);

            double avgAuc = completedTasks.stream()
                .filter(t -> t.getAuc() != null)
                .mapToDouble(DetectionTask::getAuc)
                .average()
                .orElse(0.785);
            stats.setMapAt05(Math.round(avgAuc * 1000.0) / 10.0);
        } else {
            stats.setF1Score(0.72);
            stats.setMapAt05(78.5);
        }

        stats.setAvgResponseTime(2.8);

        return stats;
    }

    @Override
    public SystemResources getSystemResources() {
        SystemResources resources = new SystemResources();
        resources.setGpuUsage(40 + random.nextInt(20));
        resources.setVram("5.4GB");
        resources.setRamUsage(30 + random.nextInt(10));
        resources.setRam("10.2GB");
        resources.setLatency(10 + random.nextInt(5));
        return resources;
    }

    @Override
    public AlgorithmPerformance getAlgorithmPerformance() {
        AlgorithmPerformance perf = new AlgorithmPerformance();
        perf.setAlgorithms(Arrays.asList("RX (基线)", "3D-CNN", "ACEN (蒸馏)", "YOLOv8s-Spe"));
        perf.setMapValues(Arrays.asList(0.55, 0.72, 0.79, 0.76));
        perf.setLatencyValues(Arrays.asList(0.5, 4.2, 2.8, 1.2));
        return perf;
    }

    @Override
    public List<DetectionTask> getRecentTasks() {
        List<DetectionTask> tasks = detectionTaskMapper.selectList(
            new LambdaQueryWrapper<DetectionTask>()
                .orderByDesc(DetectionTask::getCreateTime)
                .last("LIMIT 10")
        );

        for (DetectionTask task : tasks) {
            task.setMatFile(hsiFileMapper.selectById(task.getMatFileId()));
            task.setJpgFile(hsiFileMapper.selectById(task.getJpgFileId()));
        }

        return tasks;
    }
}
