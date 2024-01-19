package msa.userserver.vo;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class RequestLogin {
    private String email;
    private String password;
}
