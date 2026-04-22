package org.example.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Long totalCubes;
    private Double mapAt05;
    private Double f1Score;
    private Double avgResponseTime;
}
