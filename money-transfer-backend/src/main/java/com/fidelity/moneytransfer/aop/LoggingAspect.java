package com.fidelity.moneytransfer.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.fidelity.moneytransfer.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // Log method signature and arguments
        System.out.println("Executing: " + joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            System.out.print("Arguments: ");
            for (Object arg : args) {
                System.out.print(arg + " ");
            }
            System.out.println();
        }

        Object result = joinPoint.proceed(); // execute the method

        long duration = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " executed in " + duration + "ms");

        // Optionally log result
        System.out.println("Returned: " + result);

        return result;
    }
}
