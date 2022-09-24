package org.yixz.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author yixz
 * @since 2021-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_menu")
public class Menu{
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父id
     */
    private Integer parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单名称
     */
    private String menuCode;

    /**
     * 菜单路径
     */
    private String url;

    /**
     * 授权
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单类型，0-目录，1-菜单，2-按钮
     */
    private Integer type;

    /**
     * 排序
     */
    private Integer sort;

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
}
