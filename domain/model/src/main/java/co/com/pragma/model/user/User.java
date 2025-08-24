package co.com.pragma.model.user;
import lombok.*;
//import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String identityDocument;
    private String phone;
    private Long roleId;
    private Double baseSalary;
}
