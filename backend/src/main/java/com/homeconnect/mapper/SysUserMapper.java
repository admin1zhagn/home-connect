package com.homeconnect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homeconnect.entity.SysUser;
import org.apache.ibatis.annotations.Select;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND enabled = 1")
    SysUser selectByUsername(String username);
}
