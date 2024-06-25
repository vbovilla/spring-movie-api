package in.dminc.auth.utils;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthResponse {
    private String accessToken;
    private String refreshToken;

}
