package cn.itcast.controller.log;

import cn.itcast.domain.aop.SysLog;
import cn.itcast.domain.system.User;
import cn.itcast.service.aspect.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

@Component
@Aspect
public class LogAspect {
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Around("execution(* cn.itcast.controller.*.*.*(..))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        SysLog sysLog = new SysLog();
        sysLog.setId(UUID.randomUUID().toString());
        sysLog.setTime(new Date());  //当前时间

        User user = (User) session.getAttribute("loginUser");
        if(user!=null){
            sysLog.setCompanyId(user.getCompanyId());
            sysLog.setCompanyName(user.getCompanyName());
            sysLog.setUserName(user.getUserName());
        }
        sysLog.setIp(request.getRemoteAddr());  //访问者的ip

        MethodSignature signature =(MethodSignature) pjp.getSignature();//获取方法签名 = 方法对象+注解对象
        Method method = signature.getMethod(); //获取方法对象
        String name = method.getName();  //获取方法名
        sysLog.setMethod(name);  //方法  controller中的方法名

//        获取方法上的RequestMapping
//        先判断方法上是由有RequestMapping注解
        if( method.isAnnotationPresent(RequestMapping.class)){
//           获取RequestMapping对象
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            String action = requestMapping.name();
            sysLog.setAction(action); //方法的中文注解
        }
        sysLogService.save(sysLog);
        return pjp.proceed(); //执行原方法
    }
}
