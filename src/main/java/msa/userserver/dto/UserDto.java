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
    private String pwd;
    private String userId;
    private Date createdAt;
    private String encryptedPwd;
    private List<ResponseOrder> orders;

    public UserDto(String name, String email, String pwd) {
        this.name = name;
        this.email = email;
        this.pwd = pwd;
    }
    @Builder
    public UserDto(String userId, String name, String email, String pwd) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.pwd = pwd;
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
                .encryptedPwd(encryptedPwd)
                .build();
    }

    public void initEncryptedPassword(String encryptedPassword) {
        this.encryptedPwd = encryptedPwd;
    }
}
