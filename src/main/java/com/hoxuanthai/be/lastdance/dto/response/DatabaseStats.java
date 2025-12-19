package com.hoxuanthai.be.lastdance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatabaseStats {

    private boolean isConnected;

    private Double responseTimeMs;

    private Long connectionPoolSize;

    private Long activeConnections;

    private Double databaseSizeGB;

    private String lastBackupTime;

}
