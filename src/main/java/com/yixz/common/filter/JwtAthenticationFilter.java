package com.yixz.common.filter;

import com.yixz.common.util.JwtTokenUtil;
import com.yixz.common.util.SysConstants;
import com.yixz.service.UserServiceImpl;
import com.yixz.vo.CustomUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年12月16日 15:39
 */
public class JwtAthenticationFilter extends BasicAuthenticationFilter {
    private UserServiceImpl userService;

    public JwtAthenticationFilter(AuthenticationManager authenticationManager, UserServiceImpl userService) {
        super(authenticationManager);
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(SysConstants.AUTHORIZATION_HEADER);
        if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SysConstants.AUTHORIZATION_HEADER);
        if(StringUtils.isBlank(token)) {
            throw new AccessDeniedException("无权限");
        }
        String userName = JwtTokenUtil.desJwtToken(token);
        if (StringUtils.isBlank(userName)) {
            throw new AccessDeniedException("无权限");
        }
        CustomUserDetails userDetails = userService.getUserPermsFromRedis(userName);
        if(userDetails==null) {
            throw new AccessDeniedException("无权限");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
