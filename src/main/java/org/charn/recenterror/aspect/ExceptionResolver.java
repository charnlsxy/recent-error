package org.charn.recenterror.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@RestControllerAdvice
@Slf4j
public class ExceptionResolver {

    @ExceptionHandler(Exception.class)
    public Object handleServerException(Exception ex, HttpServletResponse response){
        response.setStatus(400);
        log.error("err: ", ex);
        return Collections.singletonMap("err", ex.getMessage());
    }

}
