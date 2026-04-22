package org.example.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemResources {
    private Integer gpuUsage;
    private String vram;
    private Integer ramUsage;
    private String ram;
    private Integer latency;
}
