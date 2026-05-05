package me.yan.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import me.yan.constant.WebConst;
import me.yan.controller.BaseController;
import me.yan.pojo.UserDomain;
import me.yan.utils.APIResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AuthController extends BaseController {
    private static final Logger LOGGER=log;

    @Operation(summary = "跳转登录页")
    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("commons",commons);
        return "admin/login";
    }

    @Operation(summary = "登录")
    @PostMapping(value = "/login")
    @ResponseBody
    public APIResponse toLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(name = "username", description = "用户名", required = true)
            @RequestParam(name = "username", required = true)
            String username,
            @Parameter(name = "password", description = "密码", required = true)
            @RequestParam(name = "password", required = true)
            String password,
            @Parameter(name = "remeber_me", description = "记住我", required = false)
            @RequestParam(name = "remeber_me", required = false)
            String remeber_me
    ){
        try {
            UserDomain userInfo = userService.login(username, password);
            HttpSession session = request.getSession();
            // 1. 设置Session过期时间（可选，单位：秒）
            session.setMaxInactiveInterval(60 * 60 * 24 * 7); // 7天
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, userInfo);
            if (StringUtils.isNotBlank(remeber_me)) {
                Cookie cookie = new Cookie("uid", userInfo.getUid() + "");
                cookie.setMaxAge(60 * 60 * 24 * 7);
                response.addCookie(cookie);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return APIResponse.fail(e.getMessage());
        }

        return APIResponse.success();

    }

    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie("uid", "");
        cookie.setMaxAge(0);// 立即销毁cookie
        response.addCookie(cookie);
        try {
            response.sendRedirect("/admin/login");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }
    }
}
