package msa.userserver.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {
    @NotNull
    @Size(min = 2, message = "Email not be less than two character")
    @Email
    private String email;

    @NotNull
    private String password;
}
