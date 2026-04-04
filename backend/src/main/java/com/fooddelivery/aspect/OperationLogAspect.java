package com.fooddelivery.aspect;

import cn.hutool.json.JSONUtil;
import com.fooddelivery.annotation.OperationLogger;
import com.fooddelivery.entity.OperationLog;
import com.fooddelivery.mapper.OperationLogMapper;
import com.fooddelivery.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    
    private final OperationLogMapper operationLogMapper;
    
    @Around("@annotation(operationLogger)")
    public Object around(ProceedingJoinPoint point, OperationLogger operationLogger) throws Throwable {
        OperationLog operationLog = new OperationLog();
        operationLog.setModule(operationLogger.module());
        operationLog.setOperation(operationLogger.operation());
        
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            operationLog.setUserId(userDetails.getId());
            operationLog.setUsername(userDetails.getUsername());
        }
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operationLog.setMethod(request.getMethod() + " " + request.getRequestURI());
            operationLog.setIp(getClientIp(request));
        }
        
        // 获取方法参数
        MethodSignature signature = (MethodSignature) point.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        try {
            operationLog.setParams(JSONUtil.toJsonStr(buildParams(paramNames, args)));
        } catch (Exception e) {
            operationLog.setParams("参数序列化失败");
        }
        
        Object result;
        try {
            result = point.proceed();
            operationLog.setStatus(1);
        } catch (Exception e) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
        
        return result;
    }
    
    private Object buildParams(String[] paramNames, Object[] args) {
        if (paramNames == null || args == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < paramNames.length; i++) {
            if (args[i] != null && !args[i].getClass().getName().contains("HttpServlet")) {
                sb.append("\"").append(paramNames[i]).append("\":\"").append(args[i]).append("\",");
            }
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
