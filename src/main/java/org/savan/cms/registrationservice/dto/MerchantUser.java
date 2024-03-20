package org.savan.cms.registrationservice.dto;

/**
 * Represents merchant user on the application level.
 * <p>
 * author: savan1508 on 16.3.24.
 */
public record MerchantUser(
        String uuid,
        String username,
        String firstName,
        String lastName,
        String email

) {
}
