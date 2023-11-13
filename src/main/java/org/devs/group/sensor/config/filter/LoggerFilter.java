package org.devs.group.sensor.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LoggerFilter extends HttpFilter {

    @Override
    public void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        MDC.put("userID", request.getHeader("UserId"));
        MDC.put("sessionID", request.getHeader("SessionId"));

        log.info("Added userID and sessionID to logs");
        chain.doFilter(request, response);
    }
}
