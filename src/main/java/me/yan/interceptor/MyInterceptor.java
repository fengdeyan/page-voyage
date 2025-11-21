package me.yan.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import me.yan.constant.WebConst;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("uid")) {
                return true;
            }
        }
        // 如果没有找到 uid  cookie，检查 session 中是否有登录信息
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(WebConst.LOGIN_SESSION_KEY);
        if (attribute != null) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendRedirect(request.getContextPath() + "/admin/login"); // 重定向到登录页
        return false;
    }
}
