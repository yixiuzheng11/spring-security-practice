package org.yixz.vo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年12月15日 19:11
 */
@Data
public class PermVo implements GrantedAuthority {
    /**
     * 权限标识
     */
    private String perms;

    @Override
    public String getAuthority() {
        return this.perms;
    }
}
