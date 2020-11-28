package com.example.demo.web.controller;

import com.example.demo.dto.UserLoginDTO;
import com.example.demo.utils.TokenUtil;
import com.example.demo.web.interceptor.LoginLimit;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录controller
 *
 * @author : ssk
 * @date : 2020/3/3
 */
@RestController
public class LoginController {

  /**
   * 登录
   *
   * @param userLoginDTO 用户登录DTO
   * @param response http响应
   * @return 登录信息
   */
  @PostMapping(value = "/login")
  public String login(
      @Valid @RequestBody final UserLoginDTO userLoginDTO, HttpServletResponse response) {
    // 校验非空
    if (StringUtils.isBlank(userLoginDTO.getUserName()) || StringUtils
        .isBlank(userLoginDTO.getPassword()) || userLoginDTO.getUserId() == null) {
      throw new RuntimeException("用户名,密码或用户ID为空");
    } else {

      // 验证用户名与密码

      // 生成Token
      String token = TokenUtil.generateToken(userLoginDTO, 9000);
      System.out.println("该用户生成的Token=========" + token);

      // cookie存储
      Cookie cookie = new Cookie("login_token", token);
      response.addCookie(cookie);

      return "ok";
    }
  }

  /**
   * 注销
   *
   * @param response http响应
   * @return 注销信息
   */
  @LoginLimit
  @PostMapping(value = "/logout")
  public String logout(HttpServletResponse response) {
    // 删除cookie
    removeCookie(response);

    return "ok";
  }

  /**
   * 删除指定cookie
   */
  private void removeCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("login_token", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
  }

}
