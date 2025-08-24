package co.com.pragma.r2dbc.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleEntity {

    @Id
    @Column("role_id")
    private Long id;
    private String name;
    private String description;
}