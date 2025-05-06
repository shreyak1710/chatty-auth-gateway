
package com.chatty.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiConfiguration {
    private int estimatedMonthlyRequests;
    private int requestsPerMinute;
    private String peakUsageHours;
    private String botPurpose;
    private List<String> complianceStandards;
}
