package com.hoxuanthai.be.lastdance.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {

    private List<ChartDataPoint> chartData;

    private Double average;

    private Double total;

    private Double max;

    private Double min;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataPoint {
        private Double value;
        private String label;
    }
}
