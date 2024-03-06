package msa.userserver.vo;

import lombok.Data;
import msa.userserver.dto.UserDto;
import org.hibernate.annotations.BatchSize;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message = "not email")
    @Size(min = 2, message = "적어")
    private String email;

    @NotNull
    private String name;

    @NotNull
    private String password;

    public UserDto toDto() {
        return UserDto.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }
}
