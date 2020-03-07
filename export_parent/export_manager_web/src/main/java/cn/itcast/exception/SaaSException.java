package cn.itcast.exception;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SaaSException implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {


//        出现异常后，跳转一个error.jsp页面中
        ModelAndView mv = new ModelAndView();
        if (ex instanceof UnauthorizedException){
            mv.setViewName("forward:/unauthorized.jsp");
        }
        mv.setViewName("error");
        mv.addObject("errorMsg","项目异常，请联系工程师，把以下内容告知"+ex.getMessage());
        return mv;
    }
}
