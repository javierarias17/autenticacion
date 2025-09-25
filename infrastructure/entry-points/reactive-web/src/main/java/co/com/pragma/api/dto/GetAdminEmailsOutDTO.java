package co.com.pragma.api.dto;

import java.util.List;

public record GetAdminEmailsOutDTO(
        List<String> lstAdminEmails
) {}
