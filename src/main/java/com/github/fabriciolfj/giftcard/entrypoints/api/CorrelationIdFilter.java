package com.github.fabriciolfj.giftcard.entrypoints.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.github.fabriciolfj.giftcard.util.ConstantsUtil.CORRELATION_ID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        final var correlation = Optional.ofNullable(request.getHeader(CORRELATION_ID))
                .filter(s -> !s.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());

        MDC.put(CORRELATION_ID, correlation);

        try {
            response.setHeader(CORRELATION_ID, correlation);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
        
    }
}
