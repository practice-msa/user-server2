package msa.userserver.vo;

import lombok.Builder;
import lombok.Data;
import msa.userserver.dto.UserDto;

@Data
public class ResponseUser {
    private String email;
    private String name;
    private String userId;

    @Builder
    private ResponseUser(String email, String name, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public static ResponseUser from(UserDto userDto) {
        return ResponseUser.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .build();
    }
}
