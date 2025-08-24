package co.com.pragma.model.role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    private Long id;
    private String name;
    private String description;
}
