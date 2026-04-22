package org.example.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.server.common.core.domain.AjaxResult;
import org.example.server.domain.SpectralLibrary;
import org.example.server.service.ISpectralLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spectral")
public class SpectralLibraryController {

    @Autowired
    private ISpectralLibraryService spectralLibraryService;

    @GetMapping("/list")
    public AjaxResult getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<SpectralLibrary> page = new Page<>(pageNum, pageSize);
        IPage<SpectralLibrary> result = spectralLibraryService.getSpectralPage(page, keyword);
        return AjaxResult.success("获取光谱列表成功", result);
    }

    @GetMapping("/all")
    public AjaxResult getAll() {
        List<SpectralLibrary> list = spectralLibraryService.getAllSpectra();
        return AjaxResult.success("获取所有光谱成功", list);
    }

    @GetMapping("/{id}")
    public AjaxResult getDetail(@PathVariable Long id) {
        SpectralLibrary spectral = spectralLibraryService.getSpectralById(id);
        if (spectral != null) {
            return AjaxResult.success("获取光谱详情成功", spectral);
        } else {
            return AjaxResult.error("光谱不存在");
        }
    }

    @PostMapping
    public AjaxResult save(@RequestBody SpectralLibrary spectral) {
        spectralLibraryService.saveSpectral(spectral);
        return AjaxResult.success("保存成功");
    }

    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        spectralLibraryService.deleteSpectral(id);
        return AjaxResult.success("删除成功");
    }

    @GetMapping("/similarity/{id}")
    public AjaxResult getSimilarity(@PathVariable Long id) {
        List<Map<String, Object>> similarity = List.of(
                Map.of("name", "标准植被库", "score", 0.98),
                Map.of("name", "金属-铝", "score", 0.45),
                Map.of("name", "伪装漆-A型", "score", 0.32),
                Map.of("name", "土壤-干燥", "score", 0.15),
                Map.of("name", "未知目标", "score", 0.05)
        );
        return AjaxResult.success("获取相似度成功", similarity);
    }

    @GetMapping("/radar/{id}")
    public AjaxResult getRadarData(@PathVariable Long id) {
        Map<String, Object> radar = new HashMap<>();
        radar.put("peakIntensity", 0.7);
        radar.put("absorptionDepth", 0.6);
        radar.put("waveformEntropy", 0.864);
        radar.put("redEdgeSlope", 0.5);
        radar.put("meanValue", 0.4);
        radar.put("variance", 0.03);
        return AjaxResult.success("获取雷达数据成功", radar);
    }
}
