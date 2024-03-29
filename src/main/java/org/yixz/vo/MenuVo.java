package org.yixz.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年12月23日 9:45
 */
@Data
public class MenuVo {
    @ApiModelProperty(value = "菜单id")
    private Integer id;

    @ApiModelProperty(value = "父id")
    private Integer parentId;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "菜单路径")
    private String url;

    @ApiModelProperty(value = "授权")
    private String perms;

    @ApiModelProperty(value = "菜单类型，0-目录，1-菜单，2-按钮")
    private Integer type;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "子菜单")
    private List<MenuVo> children;

    @ApiModelProperty(value = "按钮权限")
    private List<MenuVo> permList;
}
