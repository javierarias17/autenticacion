package co.com.pragma.api;

import co.com.pragma.api.config.UserPath;
import co.com.pragma.api.dto.LogInDTO;
import co.com.pragma.api.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final UserPath userPath;
    private final Handler handler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    method = {RequestMethod.POST},
                    operation = @Operation(
                            operationId = "listenSaveUser",
                            summary = "Registrar un usuario",
                            tags = { "Registro usuario" },
                            requestBody = @RequestBody(
                                    description = "Objeto JSON con los campos del usuario.",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserDTO.class),
                                            examples = {
                                                    @ExampleObject(
                                                            value = """
                                                    {
                                                      "firstName": "Javier",
                                                      "lastName": "Arias",
                                                      "email": "javierarias17.dll@gmail.com",
                                                      "birthDate": "1995-08-25",
                                                      "address": "Calle 123",
                                                      "identityDocument": "1061758338",
                                                      "phone": "3001234567",
                                                      "roleId": 2,
                                                      "baseSalary": 2500000.00,
                                                      "password": "vacaRosada123*"
                                                    }
                                                    """
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = UserDTO.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = """
                                                            {
                                                              "id": 1,
                                                              "firstName": "Javier",
                                                              "lastName": "Arias",
                                                              "email": "javierarias17.dll@gmail.com",
                                                              "birthDate": "1995-08-25",
                                                              "address": "Calle 123",
                                                              "identityDocument": "1061758338",
                                                              "phone": "3001234567",
                                                              "roleId": 2,
                                                              "baseSalary": 2500000.00,
                                                              "password": "abc123$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Icz9Q60BbWjeMBr3HyCFa"
                                                            }
                                                            """
                                                            )
                                                    }
                                            )),
                                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(
                                            mediaType = "application/json",
                                            examples = {
                                                    @ExampleObject(
                                                            value = """
                                                    {
                                                      "fields": {
                                                            "firstName": "First name is required and cannot be empty",
                                                            "lastName": "Last name is required and cannot be empty",
                                                            "email": "Email is required and cannot be empty",
                                                            "identityDocument": "Identity document must be numeric",
                                                            "baseSalary": "Base salary must be greater than or equal to 0",
                                                            "password": "Password is required"
                                                      }
                                                    }
                                                    """
                                                    )
                                            })),
                                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                                            mediaType = "application/json",
                                            examples = {
                                                    @ExampleObject(
                                                            value = """
                                                    {
                                                      "message": "An unexpected error occurred. Please contact the administrator."
                                                    }
                                                    """
                                                    )
                                            }
                                    ))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = {RequestMethod.POST},
                    operation = @Operation(
                            operationId = "listenLogIn",
                            summary = "Login",
                            tags = { "Autenticacion" },
                            requestBody = @RequestBody(
                                    description = "Objeto JSON con los campos requeridos del login.",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = LogInDTO.class),
                                            examples = {
                                                    @ExampleObject(
                                                            value = """
                                                    {
                                                    "email": "javierarias17@gmail.com",
                                                    "password": "12345678"
                                                    }
                                                    """
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Login exitoso",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = LogInDTO.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = """
                                                            {
                                                            "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDYxNzY5OTY5Iiwicm9sZXMiOlt7ImF1dGhvcml0eSI6IjEifV0sImlhdCI6MTc1NjQzODMyMCwiZXhwIjoxNzU2NDQxOTIwfQ.MB8L2aGu6Sam_mVgrVuClvVrNAeqmiTf6GMAZDlJCgUpLG1A25Ov86IrZ_8OjgQS7MO4UK5vw5O9sxip8Zl_EQ"
                                                            }
                                                            """
                                                            )
                                                    }
                                            )),
                                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(
                                            mediaType = "application/json",
                                            examples = {
                                                    @ExampleObject(
                                                            value = """
                                                    {
                                                      "message": "Validation errors",
                                                        "fields": {
                                                            "password": "Password is required and cannot be empty",
                                                            "email": "Email is required and cannot be empty"
                                                        }
                                                    }
                                                    """
                                                    )
                                            })),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                                            mediaType = "text/plain",
                                            examples = {
                                                    @ExampleObject(
                                                            value = "Invalid credentials"
                                                    )
                                            }
                                    )),
                                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(
                                            mediaType = "text/plain",
                                            examples = {
                                                    @ExampleObject(
                                                            value = "Access Denied"
                                                    )
                                            }
                                    ))
                            }
                    )
            ),
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(userPath.getUsers()), handler::listenRegisterUser)
                .andRoute(POST(userPath.getUsersByIdentityDocuments()), handler::listenGetUsersByIdentityDocuments)
                .andRoute(POST("/api/v1/login"), handler::listenLogIn);
    }
}
