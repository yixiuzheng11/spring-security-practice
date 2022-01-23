package org.yixz.common.util;

import org.yixz.vo.CustomUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import java.util.Date;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年12月16日 18:09
 */
public class JwtTokenUtil {
    /**
     * 生成jwttoken
     * @author YIXIUZHENG741
     * @date 2021/12/16 17:50
     * @param authentication
     * @return java.lang.String
     */
    public static String generateJwtToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return "Bearer " + Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000))
                .signWith(SignatureAlgorithm.HS512, SysConstants.TOKEN_SALT)
                .compact();
    }

    /**
     * 解析jwttoken
     * @author YIXIUZHENG741
     * @date 2021/12/16 17:50
     * @param jwtToken
     * @return java.lang.String
     */
    public static String desJwtToken(String jwtToken) {
        // parse the token.
        return Jwts.parser()
                .setSigningKey(SysConstants.TOKEN_SALT)
                .parseClaimsJws(jwtToken.replace("Bearer ", ""))
                .getBody()
                .getSubject();
    }
}
