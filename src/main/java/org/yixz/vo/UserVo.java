package org.yixz.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年12月15日 19:11
 */
@Data
@ApiModel(value = "用户信息")
public class UserVo {
    @ApiModelProperty(value = "用户id")
    private Integer id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String fullName;
}
