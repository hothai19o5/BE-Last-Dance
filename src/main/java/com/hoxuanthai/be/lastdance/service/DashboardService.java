package com.hoxuanthai.be.lastdance.service;

import com.hoxuanthai.be.lastdance.dto.response.*;

public interface DashboardService {

    UsersStats getUsersStats();

    DevicesStats getDevicesStats();

    ServerStats getServerStats();

    DatabaseStats getDatabaseStats();

    ApiUsageStats getApiUsageStats();

    DashboardStats getDashboardStats();
}
