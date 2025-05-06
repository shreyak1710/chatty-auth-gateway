
package com.chatty.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalAndTaxCompliance {
    private String gstNumber;
    private String panCard;
    private String fssaiLicense;
    private String shopsAndEstablishmentLicense;
    private String tradeLicense;
}
