package com.flowfund.engine.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSeriesData {
    private List<Double> values;
    private List<String> dates;
}
