package msa.userserver.dto;

import lombok.Builder;
import lombok.Data;
import msa.userserver.domain.UserEntity;
import msa.userserver.vo.ResponseOrder;
import msa.userserver.vo.ResponseUser;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private String email;
    private String name;
    private String userId;
    private Date createdAt;
    private String password;
    private List<ResponseOrder> orders;

    public UserDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    @Builder
    public UserDto(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static UserDto from(UserEntity userEntity) {
        return UserDto.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .userId(userId)
                .name(name)
                .email(email)
                .password(password)
                .build();
    }

}
