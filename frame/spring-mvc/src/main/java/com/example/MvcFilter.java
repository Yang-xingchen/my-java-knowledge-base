package com.example;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MvcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("[{}:{}]{}", request.getRemoteAddr(), request.getRemotePort(), ((RequestFacade) request).getServletPath());
        chain.doFilter(request, response);
    }

}
