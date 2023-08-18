package com.pointers.ices4hu.responses;

import com.pointers.ices4hu.models.User;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class UserResponseProfilePicture extends UserResponse {
    private String base64;

    @Override
    public void buildFrom(User user) {
        super.buildFrom(user);

        this.base64 = user.getProfilePhoto();
        if (!StringUtils.hasText(base64))
            this.base64 = null;
    }

}
