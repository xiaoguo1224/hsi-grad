package org.example.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.server.domain.SpectralLibrary;

import java.util.List;

public interface ISpectralLibraryService {
    List<SpectralLibrary> getAllSpectra();
    SpectralLibrary getSpectralById(Long id);
    IPage<SpectralLibrary> getSpectralPage(Page<SpectralLibrary> page, String keyword);
    void saveSpectral(SpectralLibrary spectral);
    void deleteSpectral(Long id);
}
