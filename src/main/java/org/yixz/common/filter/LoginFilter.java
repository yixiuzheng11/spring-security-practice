package org.yixz.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yixz.common.response.ResponseResult;
import org.yixz.common.util.JwtTokenUtil;
import org.yixz.common.util.SysConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 自定义JSON格式登录
 *
 * @author YIXIUZHENG741
 * @date 2021年11月25日 17:01
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    public LoginFilter() {
        //登录成功生成token
        this.setAuthenticationSuccessHandler((request, response, authentication) -> {
            String token = JwtTokenUtil.generateJwtToken(authentication);
            response.addHeader(SysConstants.AUTHORIZATION_HEADER, token);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            ResponseResult ok = ResponseResult.success("登录成功!");
            String s = new ObjectMapper().writeValueAsString(ok);
            out.write(s);
            out.flush();
            out.close();
        });
        //登录失败处理逻辑
        this.setAuthenticationFailureHandler((request, response, exception) -> {
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            ResponseResult respBean = ResponseResult.error("0000", exception.getMessage());
            if (exception instanceof LockedException) {
                respBean.setMsg("账户被锁定，请联系管理员!");
            } else if (exception instanceof CredentialsExpiredException) {
                respBean.setMsg("密码过期，请联系管理员!");
            } else if (exception instanceof AccountExpiredException) {
                respBean.setMsg("账户过期，请联系管理员!");
            } else if (exception instanceof DisabledException) {
                respBean.setMsg("账户被禁用，请联系管理员!");
            } else if (exception instanceof BadCredentialsException) {
                respBean.setMsg("用户名或者密码输入错误，请重新输入!");
            }
            out.write(new ObjectMapper().writeValueAsString(respBean));
            out.flush();
            out.close();
        });
    }

    /**
     * 接收并解析用户凭证
     * @author YIXIUZHENG741
     * @date 2021/12/16 10:54
     * @param request
     * @param response
     * @return org.springframework.security.core.Authentication
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)  {
        String rawVerifyCode = request.getParameter(SysConstants.VERIFY_CODE_PARAM);
        String verifyCode = (String)request.getSession().getAttribute(SysConstants.VERIFY_CODE_PARAM);
        if(StringUtils.isEmpty(rawVerifyCode) || StringUtils.isEmpty(verifyCode) || !rawVerifyCode.equals(verifyCode)) {
            throw new AuthenticationServiceException("验证码错误");
        }
        return super.attemptAuthentication(request, response);
    }
}
