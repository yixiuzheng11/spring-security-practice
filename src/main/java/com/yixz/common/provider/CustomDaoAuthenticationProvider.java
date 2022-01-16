package com.yixz.common.provider;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 在authenticate方法中是先调用retrieveUser方法查询用户，
 * 然后在调用additionalAuthenticationChecks方法校验密码，
 * 所以验证码应该要在retrieveUser方法之前调用
 * @author YIXIUZHENG741
 * @date 2021年12月20日 17:08
 */
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);
        //校验密码是否过期等
    }
}
