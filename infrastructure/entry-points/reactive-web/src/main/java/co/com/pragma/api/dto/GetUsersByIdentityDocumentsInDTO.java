package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GetUsersByIdentityDocumentsInDTO(
        @NotEmpty(message = "The list of identity documents cannot be empty")
        List<String> lstIdentityDocument
) {}
