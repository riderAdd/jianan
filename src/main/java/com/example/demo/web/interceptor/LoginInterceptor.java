package com.example.demo.web.interceptor;

import com.example.demo.utils.TokenUtil;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author : ssk
 * @date : 2020/3/3
 *
 * 登录拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // 拦截，如果方法上有注解@LoginLimit则开启拦截逻辑
    if (handler instanceof HandlerMethod) {
      if (null != ((HandlerMethod) handler).getMethodAnnotation(LoginLimit.class)) {
        // 获取验证token标识
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
          Map<String, String> cookieMap = Arrays.stream(cookies)
              .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
          String token = cookieMap.get("login_token");

          // 验证token
          if (StringUtils.isNoneBlank(token)) {
            String tokenValidFlag = TokenUtil.validateToken(token);
            // 验证通过，放行
            if ("ok".equals(tokenValidFlag)) {
              System.out.println("==============验证通过，放行");
              return true;
            } else {
              System.out.println("==============请登录");
              return false;
            }
          } else {
            System.out.println("==============请登录");
            return false;
          }
        } else {
          System.out.println("==============请登录");
          return false;
        }
      }

    }
    // 无@LoginLimit直接放行
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {

  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {

  }

}
