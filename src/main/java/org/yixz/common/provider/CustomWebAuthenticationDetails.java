package org.yixz.common.provider;

import org.yixz.common.util.SysConstants;
import lombok.Data;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import javax.servlet.http.HttpServletRequest;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年12月20日 17:40
 */
@Data
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    //验证码
    private String verifyCode;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verifyCode = request.getParameter(SysConstants.VERIFY_CODE_PARAM);
    }
}
