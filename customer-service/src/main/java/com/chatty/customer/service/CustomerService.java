
package com.chatty.customer.service;

import com.chatty.customer.dto.CustomerRegistrationRequest;
import com.chatty.customer.dto.CustomerRegistrationResponse;
import com.chatty.customer.exception.CustomerAlreadyExistsException;
import com.chatty.customer.exception.CustomerNotFoundException;
import com.chatty.customer.model.*;
import com.chatty.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request) {
        // Check if customer already exists
        if (customerRepository.existsByCustomerEmail(request.getCustomerProfile().getCustomerEmail())) {
            throw new CustomerAlreadyExistsException("Customer with email " + 
                request.getCustomerProfile().getCustomerEmail() + " already exists.");
        }

        // Map request to entity
        CustomerProfile customerProfile = mapRequestToEntity(request);
        
        // Generate customerId
        customerProfile.setCustomerId(UUID.randomUUID().toString());
        customerProfile.setCreatedAt(LocalDateTime.now());
        customerProfile.setUpdatedAt(LocalDateTime.now());
        customerProfile.setVerified(false);
        customerProfile.setActive(false);
        
        // Save customer profile
        CustomerProfile savedProfile = customerRepository.save(customerProfile);
        
        log.info("Customer registered successfully: {}", savedProfile.getCustomerId());
        
        // Return response
        return CustomerRegistrationResponse.builder()
                .customerId(savedProfile.getCustomerId())
                .customerName(savedProfile.getCustomerName())
                .customerEmail(savedProfile.getCustomerEmail())
                .message("Customer registered successfully. Please verify your email.")
                .registrationSuccess(true)
                .build();
    }
    
    public CustomerProfile getCustomerByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
    }
    
    public CustomerProfile getCustomerByEmail(String email) {
        return customerRepository.findByCustomerEmail(email)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));
    }
    
    public CustomerProfile verifyCustomer(String customerId) {
        CustomerProfile customerProfile = getCustomerByCustomerId(customerId);
        customerProfile.setVerified(true);
        customerProfile.setUpdatedAt(LocalDateTime.now());
        return customerRepository.save(customerProfile);
    }
    
    public CustomerProfile activateCustomer(String customerId) {
        CustomerProfile customerProfile = getCustomerByCustomerId(customerId);
        customerProfile.setActive(true);
        customerProfile.setUpdatedAt(LocalDateTime.now());
        return customerRepository.save(customerProfile);
    }
    
    private CustomerProfile mapRequestToEntity(CustomerRegistrationRequest request) {
        CustomerRegistrationRequest.CustomerProfileDto profileDto = request.getCustomerProfile();
        
        Address address = null;
        if (profileDto.getRegisteredAddress() != null) {
            address = Address.builder()
                .street(profileDto.getRegisteredAddress().getStreet())
                .city(profileDto.getRegisteredAddress().getCity())
                .state(profileDto.getRegisteredAddress().getState())
                .pinCode(profileDto.getRegisteredAddress().getPinCode())
                .build();
        }

        LegalAndTaxCompliance legalAndTaxCompliance = null;
        if (request.getLegalAndTaxCompliance() != null) {
            legalAndTaxCompliance = LegalAndTaxCompliance.builder()
                .gstNumber(request.getLegalAndTaxCompliance().getGstNumber())
                .panCard(request.getLegalAndTaxCompliance().getPanCard())
                .fssaiLicense(request.getLegalAndTaxCompliance().getFssaiLicense())
                .shopsAndEstablishmentLicense(request.getLegalAndTaxCompliance().getShopsAndEstablishmentLicense())
                .tradeLicense(request.getLegalAndTaxCompliance().getTradeLicense())
                .build();
        }

        BankingDetails bankingDetails = null;
        if (request.getBankingDetails() != null) {
            bankingDetails = BankingDetails.builder()
                .bankAccountNumber(request.getBankingDetails().getBankAccountNumber())
                .bankName(request.getBankingDetails().getBankName())
                .ifscCode(request.getBankingDetails().getIfscCode())
                .cancelledChequeOrBankStatement(request.getBankingDetails().getCancelledChequeOrBankStatement())
                .build();
        }
        
        AdminUser primaryAdmin = null;
        AdminUser technicalAdmin = null;
        if (request.getAdminDetails() != null) {
            if (request.getAdminDetails().getPrimary() != null) {
                primaryAdmin = AdminUser.builder()
                    .name(request.getAdminDetails().getPrimary().getName())
                    .email(request.getAdminDetails().getPrimary().getEmail())
                    .phone(request.getAdminDetails().getPrimary().getPhone())
                    .address(request.getAdminDetails().getPrimary().getAddress())
                    .timeZone(request.getAdminDetails().getPrimary().getTimeZone())
                    .build();
            }
            
            if (request.getAdminDetails().getTechnical() != null) {
                technicalAdmin = AdminUser.builder()
                    .name(request.getAdminDetails().getTechnical().getName())
                    .email(request.getAdminDetails().getTechnical().getEmail())
                    .phone(request.getAdminDetails().getTechnical().getPhone())
                    .build();
            }
        }

        AdminDetails adminDetails = AdminDetails.builder()
            .primary(primaryAdmin)
            .technical(technicalAdmin)
            .build();
        
        Operations operations = null;
        if (request.getOperations() != null) {
            OperatingHours operatingHours = null;
            if (request.getOperations().getOperatingHours() != null) {
                operatingHours = OperatingHours.builder()
                    .openingTime(request.getOperations().getOperatingHours().getOpeningTime())
                    .closingTime(request.getOperations().getOperatingHours().getClosingTime())
                    .build();
            }
            
            Staff staff = null;
            if (request.getOperations().getStaff() != null) {
                staff = Staff.builder()
                    .kitchenStaff(request.getOperations().getStaff().getKitchenStaff())
                    .deliveryStaff(request.getOperations().getStaff().getDeliveryStaff())
                    .build();
            }
            
            operations = Operations.builder()
                .operatingHours(operatingHours)
                .cuisineType(request.getOperations().getCuisineType())
                .deliveryRadiusPreference(request.getOperations().getDeliveryRadiusPreference())
                .staff(staff)
                .websiteOrSocialMediaLinks(request.getOperations().getWebsiteOrSocialMediaLinks())
                .build();
        }
        
        ApiConfiguration apiConfiguration = null;
        if (request.getApiConfiguration() != null) {
            apiConfiguration = ApiConfiguration.builder()
                .estimatedMonthlyRequests(request.getApiConfiguration().getEstimatedMonthlyRequests())
                .requestsPerMinute(request.getApiConfiguration().getRequestsPerMinute())
                .peakUsageHours(request.getApiConfiguration().getPeakUsageHours())
                .botPurpose(request.getApiConfiguration().getBotPurpose())
                .complianceStandards(request.getApiConfiguration().getComplianceStandards())
                .build();
        }
        
        Branding branding = null;
        if (request.getBranding() != null) {
            branding = Branding.builder()
                .logoUrl(request.getBranding().getLogoUrl())
                .brandName(request.getBranding().getBrandName())
                .greetingMessage(request.getBranding().getGreetingMessage())
                .fallBackResponse(request.getBranding().getFallBackResponse())
                .build();
        }
        
        ChatbotConfig chatbotConfig = null;
        if (request.getChatbotConfig() != null) {
            Theme theme = null;
            if (request.getChatbotConfig().getTheme() != null) {
                theme = Theme.builder()
                    .primaryColor(request.getChatbotConfig().getTheme().getPrimaryColor())
                    .secondaryColor(request.getChatbotConfig().getTheme().getSecondaryColor())
                    .fontStyle(request.getChatbotConfig().getTheme().getFontStyle())
                    .build();
            }
            
            chatbotConfig = ChatbotConfig.builder()
                .theme(theme)
                .supportedLanguages(request.getChatbotConfig().getSupportedLanguages())
                .chatWidgetPosition(request.getChatbotConfig().getChatWidgetPosition())
                .build();
        }
        
        Agreements agreements = null;
        if (request.getAgreements() != null) {
            agreements = Agreements.builder()
                .termsAccepted(request.getAgreements().getTermsAccepted())
                .privacyPolicyAccepted(request.getAgreements().getPrivacyPolicyAccepted())
                .build();
        }
        
        return CustomerProfile.builder()
            .customerName(profileDto.getCustomerName())
            .industryType(profileDto.getIndustryType())
            .businessCategory(profileDto.getBusinessCategory())
            .registeredAddress(address)
            .country(profileDto.getCountry())
            .gstNumber(profileDto.getGstNumber())
            .customerWebsite(profileDto.getCustomerWebsite())
            .customerRegistrationNumber(profileDto.getCustomerRegistrationNumber())
            .ownerName(profileDto.getOwnerName())
            .contactNumber(profileDto.getContactNumber())
            .customerEmail(profileDto.getCustomerEmail())
            .legalAndTaxCompliance(legalAndTaxCompliance)
            .bankingDetails(bankingDetails)
            .adminDetails(adminDetails)
            .metaData(request.getMetaData())
            .operations(operations)
            .apiConfiguration(apiConfiguration)
            .branding(branding)
            .chatbotConfig(chatbotConfig)
            .agreements(agreements)
            .build();
    }
}
