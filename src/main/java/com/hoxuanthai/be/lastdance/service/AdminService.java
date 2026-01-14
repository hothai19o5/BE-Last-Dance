package com.hoxuanthai.be.lastdance.service;

import com.hoxuanthai.be.lastdance.dto.UserOverviewDto;

public interface AdminService {

    /**
     * Get aggregated user overview for admin dashboard
     * Returns only statistical summaries, no raw health data
     */
    UserOverviewDto getUserOverview(Long userId);
}
