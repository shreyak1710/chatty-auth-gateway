
package com.chatty.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankingDetails {
    private String bankAccountNumber;
    private String bankName;
    private String ifscCode;
    private String cancelledChequeOrBankStatement;
}
