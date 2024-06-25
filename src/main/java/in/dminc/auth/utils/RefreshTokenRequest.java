package in.dminc.auth.utils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RefreshTokenRequest {
    private String refreshToken;
}
