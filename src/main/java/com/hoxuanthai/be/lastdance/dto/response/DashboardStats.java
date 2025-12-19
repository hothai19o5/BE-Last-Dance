package com.hoxuanthai.be.lastdance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardStats {

    private ApiUsageStats apiUsageStats;

    private UsersStats usersStats;

    private DevicesStats devicesStats;

    private DatabaseStats databaseStats;

    private ServerStats serverStats;

}
