package org.yixz.vo;

import org.yixz.entity.User;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Set;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年12月15日 19:11
 */
@Data
public class CustomUserDetails extends User implements UserDetails {
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;
    private Set<PermVo> authorities;

    @Override
    public String getPassword() {
        return this.getPassword();
    }

    @Override
    public String getUsername() {
        return this.getUserName();
    }
}
