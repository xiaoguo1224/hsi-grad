package org.example.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmPerformance {
    private List<String> algorithms;
    private List<Double> mapValues;
    private List<Double> latencyValues;
}
