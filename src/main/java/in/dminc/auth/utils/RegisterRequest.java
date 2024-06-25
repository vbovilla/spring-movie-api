package in.dminc.auth.utils;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RegisterRequest {
    private String name;
    private String email;
    private String username;
    private String password;
}
