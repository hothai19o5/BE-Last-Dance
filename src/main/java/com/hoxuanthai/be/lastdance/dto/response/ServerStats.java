package com.hoxuanthai.be.lastdance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerStats {

    private Double uptime;

    private Double cpuUsage;

    private Double memoryUsage;

    private Double diskUsage;

    private Double totalMemoryGB;

    private Double totalDiskGB;

}
