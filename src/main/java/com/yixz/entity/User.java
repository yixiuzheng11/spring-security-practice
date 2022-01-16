package com.yixz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author yixz
 * @since 2021-11-26
 */
@Data
@TableName("t_user")
public class User {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户名
     */
    private String fullName;

    /**
     * 密码
     */
    private String password;

    /**
     * 逻辑状态
     */
    private Integer voidFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedDate;

    /**
     * 创建人员
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 更新人员
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName=" + userName +
                ", password=" + password +
                ", voidFlag=" + voidFlag +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                "}";
    }
}
