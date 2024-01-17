package msa.userserver.vo;

import com.sun.istack.NotNull;
import lombok.Data;;
import msa.userserver.dto.UserDto;
import org.hibernate.annotations.BatchSize;

@Data
public class RequestUser {

//    @NotNull(message= "not email")
//    @BatchSize(min=2,message="적어")
    private String email;
    private String name;
    private String pwd;

    public UserDto toDto() {
        return UserDto.builder()
                .email(email)
                .pwd(pwd)
                .name(name)
                .build();
    }
}
