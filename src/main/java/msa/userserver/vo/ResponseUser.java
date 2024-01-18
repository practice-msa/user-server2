package msa.userserver.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import msa.userserver.domain.UserEntity;
import msa.userserver.dto.UserDto;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {
    private String email;
    private String name;
    private String userId;
    private List<ResponseOrder> orders;

    @Builder
    private ResponseUser(String email, String name, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public static ResponseUser from(UserDto userDto) {
        ResponseUser responseUser = new ResponseUser(userDto.getEmail(), userDto.getName(), userDto.getUserId());
        responseUser.setOrders(userDto.getOrders());
        return responseUser;
    }

    public static ResponseUser from(UserEntity userEntity) {
        return new ResponseUser(userEntity.getEmail(), userEntity.getName(), userEntity.getUserId());
    }
}
