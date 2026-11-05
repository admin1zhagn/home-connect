package com.homeconnect.service;

import com.homeconnect.common.BizException;
import com.homeconnect.dto.AuthResponse;
import com.homeconnect.entity.SysUser;
import com.homeconnect.mapper.SysUserMapper;
import com.homeconnect.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(String username, String password) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        AuthResponse resp = new AuthResponse();
        resp.setToken(jwtUtil.generateToken(user.getUsername()));
        resp.setUsername(user.getUsername());
        return resp;
    }
}
