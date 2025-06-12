package org.example.template.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.template.common.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class GlobalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 解析 token 并获取用户信息
        String token = request.getHeader("Authorization");
        String user = parseToken(token); // 假设有一个方法 parseToken 解析 token 并返回用户信息
        UserContext.setUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }

    private String parseToken(String token) {
        // 实现 token 解析逻辑
        return "parsedUser"; // 示例返回值
    }
}