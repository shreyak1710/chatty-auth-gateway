
package com.chatty.customer.dto;

import com.chatty.customer.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationRequest {
    @NotNull(message = "Customer profile is required")
    private CustomerProfileDto customerProfile;
    private LegalAndTaxComplianceDto legalAndTaxCompliance;
    private BankingDetailsDto bankingDetails;
    @NotNull(message = "Admin details are required")
    private AdminDetailsDto adminDetails;
    private Map<String, Object> metaData;
    private OperationsDto operations;
    private ApiConfigurationDto apiConfiguration;
    private BrandingDto branding;
    private ChatbotConfigDto chatbotConfig;
    @NotNull(message = "Agreements are required")
    private AgreementsDto agreements;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerProfileDto {
        @NotBlank(message = "Customer name is required")
        private String customerName;
        private String industryType;
        private String businessCategory;
        private AddressDto registeredAddress;
        private String country;
        private String gstNumber;
        private String customerWebsite;
        private String customerRegistrationNumber;
        private String ownerName;
        private String contactNumber;
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String customerEmail;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private String street;
        private String city;
        private String state;
        private String pinCode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LegalAndTaxComplianceDto {
        private String gstNumber;
        private String panCard;
        private String fssaiLicense;
        private String shopsAndEstablishmentLicense;
        private String tradeLicense;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankingDetailsDto {
        private String bankAccountNumber;
        private String bankName;
        private String ifscCode;
        private String cancelledChequeOrBankStatement;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminDetailsDto {
        @NotNull(message = "Primary admin is required")
        private AdminUserDto primary;
        private AdminUserDto technical;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminUserDto {
        @NotBlank(message = "Name is required")
        private String name;
        private String email;
        private String phone;
        private String address;
        private String timeZone;
        private String password;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationsDto {
        private OperatingHoursDto operatingHours;
        private List<String> cuisineType;
        private String deliveryRadiusPreference;
        private StaffDto staff;
        private List<String> websiteOrSocialMediaLinks;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatingHoursDto {
        private String openingTime;
        private String closingTime;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffDto {
        private int kitchenStaff;
        private int deliveryStaff;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiConfigurationDto {
        private int estimatedMonthlyRequests;
        private int requestsPerMinute;
        private String peakUsageHours;
        private String botPurpose;
        private List<String> complianceStandards;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandingDto {
        private String logoUrl;
        private String brandName;
        private String greetingMessage;
        private String fallBackResponse;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatbotConfigDto {
        private ThemeDto theme;
        private List<String> supportedLanguages;
        private String chatWidgetPosition;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThemeDto {
        private String primaryColor;
        private String secondaryColor;
        private String fontStyle;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgreementsDto {
        @NotNull(message = "Terms acceptance is required")
        private Boolean termsAccepted;
        @NotNull(message = "Privacy policy acceptance is required")
        private Boolean privacyPolicyAccepted;
    }
}
