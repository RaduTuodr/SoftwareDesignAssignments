package com.andrei.demo.controller.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IsRoleAspect {

    @Around("@annotation(isRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, IsRole isRole) throws Throwable {
        String role = isRole.value();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role))) {
            return joinPoint.proceed();
        } else {
            throw new AccessDeniedException("Access denied");
        }
    }
}
