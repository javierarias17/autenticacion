package co.com.pragma.api.dto;

import java.util.List;

public record GetUsersByIdentityDocumentsOutDTO(
        List<UserDTO> lstUserDTO
) {}
