
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
public class Operations {
    private OperatingHours operatingHours;
    private List<String> cuisineType;
    private String deliveryRadiusPreference;
    private Staff staff;
    private List<String> websiteOrSocialMediaLinks;
}
