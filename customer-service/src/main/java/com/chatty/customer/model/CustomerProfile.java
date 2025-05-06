
package com.chatty.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_profiles")
public class CustomerProfile {
    @Id
    private String id;
    private String customerId;
    private String customerName;
    private String industryType;
    private String businessCategory;
    private Address registeredAddress;
    private String country;
    private String gstNumber;
    private String customerWebsite;
    private String customerRegistrationNumber;
    private String ownerName;
    private String contactNumber;
    private String customerEmail;
    private boolean isVerified;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LegalAndTaxCompliance legalAndTaxCompliance;
    private BankingDetails bankingDetails;
    private AdminDetails adminDetails;
    private Map<String, Object> metaData;
    private Operations operations;
    private ApiConfiguration apiConfiguration;
    private Branding branding;
    private ChatbotConfig chatbotConfig;
    private Agreements agreements;
}
