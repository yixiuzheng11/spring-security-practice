package org.yixz.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.yixz.vo.CustomUserDetails;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年12月17日 16:27
 */
public class MybatisObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.getPrincipal()!=null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            setFieldValByName("createdBy", userDetails.getUsername(), metaObject);
            setFieldValByName("updatedBy", userDetails.getUsername(), metaObject);
        }
        setFieldValByName("createdDate", LocalDateTime.now(), metaObject);
        setFieldValByName("updatedDate",LocalDateTime.now(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.getPrincipal()!=null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            setFieldValByName("updatedBy", userDetails.getUsername(), metaObject);
        }
        setFieldValByName("updatedDate",LocalDateTime.now(),metaObject);
    }
}
