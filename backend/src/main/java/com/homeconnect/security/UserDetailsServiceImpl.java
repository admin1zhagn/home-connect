package com.homeconnect.security;

import com.homeconnect.entity.SysUser;
import com.homeconnect.mapper.SysUserMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    public UserDetailsServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser u = sysUserMapper.selectByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}
