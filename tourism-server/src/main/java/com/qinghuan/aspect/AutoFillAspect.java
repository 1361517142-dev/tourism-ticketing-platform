package com.qinghuan.aspect;

import com.qinghuan.annotation.RefreshCreateTimeOrUpdateTime;
import com.qinghuan.common.constant.AutoFillConstant;
import com.qinghuan.pojo.enums.OperationType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.qinghuan.pojo.enums.OperationType.UPDATE;

/*
 * 自动填充字段切面
 */
@Component
@Aspect
public class AutoFillAspect {

    /*
     * 自动填充创建时间或修改时间
     */
    @Before("@annotation(com.qinghuan.annotation.OperationType)")
    public void autoFillCreateTimeOrUpdateTime(ProceedingJoinPoint joinPoint, OperationType operationType) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        RefreshCreateTimeOrUpdateTime refreshCreateTimeOrUpdateTime = methodSignature.getMethod().getAnnotation(RefreshCreateTimeOrUpdateTime.class);
        OperationType methodOperationType = refreshCreateTimeOrUpdateTime.value();

        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || args[0] == null) {
            return;
        }

        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();

        switch (methodOperationType) {
            case INSERT:
                try {
                    entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_AT, LocalDateTime.class).invoke(entity, now);
                    entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_AT, LocalDateTime.class).invoke(entity, now);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case UPDATE:
                try {
                    entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_AT, LocalDateTime.class).invoke(entity, now);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
    }
}
