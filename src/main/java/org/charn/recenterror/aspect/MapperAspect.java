package org.charn.recenterror.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.charn.recenterror.model.db.Table;
import org.charn.recenterror.utl.IdProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class MapperAspect {

    @Autowired
    IdProducer producer;

    @Pointcut("execution(* org.charn.recenterror.dao.SqlMapper.save*(..))")
    public void insert(){}

    @Before("insert()")
    public void beforeInsert(JoinPoint point){

        Object[] args = point.getArgs();
        if (args.length > 0){
            Object arg = args[0];
            checkObj(arg);
            if (arg instanceof List){
                for (Object o : (List) arg) {
                    checkObj(o);
                }
            }
        }
    }

    private void checkObj(Object arg) {
        if (arg instanceof Table){
            ((Table) arg).setId(producer.newId());
        }
    }

}
