package org.example.server.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("spectral_library")
public class SpectralLibrary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String spectralId;
    private String name;
    private String category;
    private String typeTag;
    private String peak;
    private String updateTime;
    private String spectralData;
    private String sampleEnvironment;
    private Double spectralEntropy;
    private LocalDateTime createTime;
    private LocalDateTime updateAt;
}
