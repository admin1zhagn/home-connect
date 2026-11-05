package com.homeconnect.common;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public R<Void> handleBiz(BizException e) {
        return R.error(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public R<Void> handleAuth(AuthenticationException e, HttpServletResponse resp) {
        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        return R.error(401, "登录失效，请重新登录");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public R<Void> handleDenied(AccessDeniedException e, HttpServletResponse resp) {
        resp.setStatus(HttpStatus.FORBIDDEN.value());
        return R.error(403, "无权限");
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleOther(Exception e) {
        return R.error(500, e.getMessage());
    }
}
