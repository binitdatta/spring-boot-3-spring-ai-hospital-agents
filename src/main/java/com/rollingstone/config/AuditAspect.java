package com.rollingstone.config;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    Logger logger = LoggerFactory.getLogger("AuditAspect");

    @Pointcut("execution(* com.rollingstone.tools..*(..))")
    public void toolCall(){}

    @Before("toolCall()")
    public void beforeTool(JoinPoint jp){
        logger.info("[AUDIT] "+ Instant.now() +" TOOL " + jp.getSignature().getName()
                + " args=" + Arrays.toString(jp.getArgs()));
    }
}

