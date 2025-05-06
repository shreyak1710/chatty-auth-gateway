
package com.chatty.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyPermissions {
    private boolean readAccess;
    private boolean writeAccess;
    private boolean adminAccess;
    private int rateLimitPerMinute;
}
