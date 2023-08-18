package com.pointers.ices4hu.security.jwt;

import com.pointers.ices4hu.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = jwtUtils.fetchJwtTokenFromRequest(request);
            if (StringUtils.hasText(jwtToken) && jwtTokenGenerator.isTokenValid(jwtToken)) {
                String loginID = jwtTokenGenerator.extractLoginIDFromJwtToken(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(loginID);

                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken token =
                            new UsernamePasswordAuthenticationToken(userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    token.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(token);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            /* TODO it seems like terminating the execution of the method at this point
             *   causes the backend to send an empty response with status code 200
             */
            // return;
        }

        filterChain.doFilter(request, response);

    }
}
