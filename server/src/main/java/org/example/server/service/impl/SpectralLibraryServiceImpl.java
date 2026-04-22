package org.example.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.server.domain.SpectralLibrary;
import org.example.server.mapper.SpectralLibraryMapper;
import org.example.server.service.ISpectralLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SpectralLibraryServiceImpl implements ISpectralLibraryService {

    @Autowired
    private SpectralLibraryMapper spectralLibraryMapper;

    private final Random random = new Random();

    @Override
    public List<SpectralLibrary> getAllSpectra() {
        List<SpectralLibrary> list = spectralLibraryMapper.selectList(null);
        if (list.isEmpty()) {
            list = initSampleData();
        }
        return list;
    }

    @Override
    public SpectralLibrary getSpectralById(Long id) {
        SpectralLibrary spectral = spectralLibraryMapper.selectById(id);
        if (spectral == null) {
            List<SpectralLibrary> samples = initSampleData();
            if (!samples.isEmpty()) {
                spectral = samples.get(0);
            }
        }
        return spectral;
    }

    @Override
    public IPage<SpectralLibrary> getSpectralPage(Page<SpectralLibrary> page, String keyword) {
        LambdaQueryWrapper<SpectralLibrary> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SpectralLibrary::getName, keyword)
                    .or().like(SpectralLibrary::getSpectralId, keyword)
                    .or().like(SpectralLibrary::getCategory, keyword));
        }
        wrapper.orderByDesc(SpectralLibrary::getCreateTime);
        IPage<SpectralLibrary> result = spectralLibraryMapper.selectPage(page, wrapper);
        if (result.getRecords().isEmpty()) {
            List<SpectralLibrary> samples = initSampleData();
            result.setRecords(samples);
            result.setTotal(samples.size());
        }
        return result;
    }

    @Override
    public void saveSpectral(SpectralLibrary spectral) {
        if (spectral.getId() == null) {
            spectral.setCreateTime(LocalDateTime.now());
            spectral.setUpdateAt(LocalDateTime.now());
            spectralLibraryMapper.insert(spectral);
        } else {
            spectral.setUpdateAt(LocalDateTime.now());
            spectralLibraryMapper.updateById(spectral);
        }
    }

    @Override
    public void deleteSpectral(Long id) {
        spectralLibraryMapper.deleteById(id);
    }

    private List<SpectralLibrary> initSampleData() {
        List<SpectralLibrary> samples = new ArrayList<>();
        String[] names = {"绿色植被 (草地)", "金属板 (涂层)", "伪装网 (丛林)", "枯黄落叶", "混凝土路面"};
        String[] categories = {"自然背景", "人造目标", "干扰物", "自然背景", "人造目标"};
        String[] typeTags = {"success", "danger", "warning", "info", "info"};
        String[] peaks = {"780", "N/A", "650", "600", "N/A"};

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 5; i++) {
            SpectralLibrary s = new SpectralLibrary();
            s.setSpectralId("S00" + (i + 1));
            s.setName(names[i]);
            s.setCategory(categories[i]);
            s.setTypeTag(typeTags[i]);
            s.setPeak(peaks[i]);
            s.setUpdateTime(LocalDateTime.now().format(formatter));
            s.setSampleEnvironment("自然光 11:00 AM");
            s.setSpectralEntropy(0.8 + random.nextDouble() * 0.1);
            s.setSpectralData(generateSpectralData(i));
            s.setCreateTime(LocalDateTime.now().minusDays(5 - i));
            s.setUpdateAt(LocalDateTime.now());
            samples.add(s);
        }
        return samples;
    }

    private String generateSpectralData(int typeIndex) {
        List<Double> data = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 200; i++) {
            double val = 0;
            if (typeIndex == 0 || typeIndex == 3) {
                if (i < 50) val = 0.05 + r.nextDouble() * 0.02;
                else if (i < 80) val = 0.05 + (i - 50) * 0.015;
                else val = 0.5 + r.nextDouble() * 0.05 - (i - 80) * 0.001;
            } else if (typeIndex == 1 || typeIndex == 4) {
                val = 0.15 + (i * 0.001) + r.nextDouble() * 0.03;
            } else if (typeIndex == 2) {
                if (i < 50) val = 0.08 + r.nextDouble() * 0.02;
                else if (i < 80) val = 0.08 + (i - 50) * 0.01;
                else val = 0.35 + Math.sin(i / 20) * 0.05;
            }
            if (typeIndex == 3) val *= 0.8;
            if (typeIndex == 4) val *= 1.5;
            data.add(Math.max(0, Math.min(1, val)));
        }
        return JSON.toJSONString(data);
    }
}
