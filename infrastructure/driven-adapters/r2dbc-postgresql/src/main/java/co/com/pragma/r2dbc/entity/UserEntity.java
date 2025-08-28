package co.com.pragma.r2dbc.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @Column("user_id")
    private Long id;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    private String email;
    @Column("birth_date")
    private LocalDate birthDate;
    private String address;
    @Column("identity_document")
    private String identityDocument;
    private String phone;
    @Column("role_id")
    private Long roleId;
    @Column("base_salary")
    private BigDecimal baseSalary;
    private String password;
}