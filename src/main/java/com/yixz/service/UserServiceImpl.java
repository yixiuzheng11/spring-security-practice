package com.yixz.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixz.common.util.SysConstants;
import com.yixz.dto.UserDto;
import com.yixz.enums.MenuTypeEnum;
import com.yixz.entity.User;
import com.yixz.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixz.vo.CustomUserDetails;
import com.yixz.vo.PermVo;
import com.yixz.vo.UserRolePermVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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

    /**
     * 查询用户信息
     * @author YIXIUZHENG741
     * @date 2022/1/10 14:31
     * @param userName
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //这里要把密码查询出来，后面用于校验密码
        CustomUserDetails userDetails = getUserPermByUserName(userName);
        if(userDetails==null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        setUserPermsToRedis(userDetails);
        return userDetails;
    }

    /**
     * 查询用户以及角色权限信息
     * @author YIXIUZHENG741
     * @date 2022/1/10 14:31
     * @param userName
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    public CustomUserDetails getUserPermByUserName(String userName) {
        List<UserRolePermVo> list = this.baseMapper.getUserPermByUserName(userName, MenuTypeEnum.BTN_TYPE.getCode());
        if (CollectionUtils.isEmpty(list)) {
            throw new AccessDeniedException("无权限");
        }
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(list.get(0).getUserId());
        userDetails.setUserName(list.get(0).getUserName());
        userDetails.setPassword(list.get(0).getPassword());
        userDetails.setAuthorities(new HashSet<>());
        Set<PermVo> roleSet = new HashSet<>();
        Set<PermVo> permSet = new HashSet<>();
        list.stream().forEach(item->{
            if(StringUtils.isNotEmpty(item.getRoleCode())) {
                PermVo role = new PermVo();
                role.setPerm(item.getRoleCode());
                roleSet.add(role);
            }
            if(StringUtils.isNotEmpty(item.getPerm())) {
                PermVo perm = new PermVo();
                perm.setPerm(item.getPerm());
                permSet.add(perm);
            }
        });
        userDetails.getAuthorities().addAll(roleSet);
        userDetails.getAuthorities().addAll(permSet);
        return userDetails;
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

    public CustomUserDetails getUserPermsFromRedis(String userName) {
        Object obj = redisOptService.get(SysConstants.USER_PERMS_KEY_PREFIX + userName);
        CustomUserDetails userDetails = null;
        if(obj!=null) {
            userDetails = JSON.parseObject(obj.toString(), CustomUserDetails.class);
        }
        return userDetails;
    }

    public void setUserPermsToRedis(CustomUserDetails userDetails) {
        redisOptService.set(SysConstants.USER_PERMS_KEY_PREFIX + userDetails.getUsername(), JSON.toJSONString(userDetails), 60*60*6);
    }
}
