package com.hoxuanthai.be.lastdance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiUsageStats {

    private Long requestsPerMinute;

    private Double averageResponseTimeMs;

    private Double successRatePercentage;

}
