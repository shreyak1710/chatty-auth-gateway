
package com.chatty.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branding {
    private String logoUrl;
    private String brandName;
    private String greetingMessage;
    private String fallBackResponse;
}
