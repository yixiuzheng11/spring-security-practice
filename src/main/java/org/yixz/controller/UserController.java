package org.yixz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.yixz.dto.UserDto;
import org.yixz.entity.User;
import org.yixz.service.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yixz.vo.UserVo;

import javax.annotation.Resource;

/**
 * 描述
 *
 * @author YIXIUZHENG741
 * @date 2021年11月25日 19:05
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/sys/user")
public class UserController {
    @Resource
    private UserServiceImpl userService;

    @ApiOperation("用户查询")
    @GetMapping("/getCurrentUser")
    public UserVo getCurrentUser() {
        return userService.getCurrentUser();
    }

    @ApiOperation("用户查询")
    @PreAuthorize("hasAuthority('/system/user/select')")
    @PostMapping("/getPage")
    public Page<User> getPage(@RequestBody UserDto userDto) {
        return userService.getPage(userDto);
    }

    @ApiOperation("用户新增")
    @PreAuthorize("hasAuthority('/system/user/add')")
    @PostMapping("/add")
    public Integer add(@RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @ApiOperation("用户修改")
    @PreAuthorize("hasAuthority('/system/user/update')")
    @PostMapping("/update")
    public void update(@RequestBody UserDto userDto) {
        userService.update(userDto);
    }

    @ApiOperation("用户删除")
    @PreAuthorize("hasAuthority('/system/user/delete')")
    @PostMapping("/delete")
    public void delete(@RequestBody UserDto userDto) {
        userService.delete(userDto.getId());
    }
}
