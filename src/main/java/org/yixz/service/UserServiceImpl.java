package org.yixz.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yixz.common.util.SysConstants;
import org.yixz.dto.UserDto;
import org.yixz.enums.MenuTypeEnum;
import org.yixz.entity.User;
import org.yixz.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.yixz.vo.CustomUserDetails;
import org.yixz.vo.PermVo;
import org.yixz.vo.UserRolePermVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.yixz.vo.UserVo;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author yixz
 * @since 2021-11-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserDetailsService {
    @Resource
    private RedisOptService redisOptService;

    public User getUserByUserName(String userName) {
        List<User> list = baseMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getUserName, userName));
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }


    /**
     * 登录时，spring security根据这个方法查询用户信息，用来校验密码
     * @author YIXIUZHENG741
     * @date 2022/1/10 14:31
     * @param userName
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = this.getUserByUserName(userName);
        if(user==null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        //这里要把密码查询出来，后面用于校验密码
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(user.getId());
        userDetails.setUserName(user.getUserName());
        userDetails.setFullName(user.getFullName());
        userDetails.setPassword(user.getPassword());
        return userDetails;
    }

    /**
     * 查询用户以及角色权限信息，用来鉴权
     * @author YIXIUZHENG741
     * @date 2022/1/10 14:31
     * @param userName
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    public CustomUserDetails getUserPermByUserName(String userName) {
        List<UserRolePermVo> list = this.baseMapper.getUserPermByUserName(userName, MenuTypeEnum.BTN_TYPE.getCode());
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(list.get(0).getUserId());
        userDetails.setUserName(list.get(0).getUserName());
        userDetails.setPassword(list.get(0).getPassword());
        userDetails.setAuthorities(new HashSet<>());
        Set<PermVo> roleSet = new HashSet<>();
        Set<PermVo> permSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(item->{
                if(StringUtils.isNotEmpty(item.getRoleCode())) {
                    PermVo role = new PermVo();
                    role.setPerms(item.getRoleCode());
                    roleSet.add(role);
                }
                if(StringUtils.isNotEmpty(item.getPerms())) {
                    PermVo perm = new PermVo();
                    perm.setPerms(item.getPerms());
                    permSet.add(perm);
                }
            });
            userDetails.getAuthorities().addAll(roleSet);
            userDetails.getAuthorities().addAll(permSet);
        }
        return userDetails;
    }

    /**
     * 获取当前登录用户信息
     * @author YIXIUZHENG741
     * @date 2022/1/10 14:31
     */
    public UserVo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || authentication.getPrincipal()==null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = this.getUserByUserName(userDetails.getUserName());
        UserVo userVo = new UserVo();
        userVo.setId(user.getId());
        userVo.setUserName(user.getUserName());
        userVo.setFullName(user.getFullName());
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    public Page<User> getPage(UserDto dto) {
        Page page = new Page(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.select("id, user_name, full_name, created_date, created_by");
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getUserName()), "user_name", dto.getUserName());
        queryWrapper.like(StringUtils.isNotEmpty(dto.getFullName()), "full_name", dto.getFullName());
        return baseMapper.selectPage(page, queryWrapper);
    }

    public Integer add(UserDto dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        baseMapper.insert(user);
        return user.getId();
    }

    public void update(UserDto dto) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getUserName, dto.getUserName());
        updateWrapper.set(User::getFullName, dto.getFullName());
        updateWrapper.eq(User::getId, dto.getId());
        //这里传个new出来的user对象，确保FieldFill.INSERT_UPDATE属性在更新时自动填充值
        baseMapper.update(new User(), updateWrapper);
    }

    public void delete(Integer id) {
        baseMapper.deleteById(id);
    }

}
