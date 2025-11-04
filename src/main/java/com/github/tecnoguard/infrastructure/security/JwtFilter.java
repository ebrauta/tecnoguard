package com.github.tecnoguard.infrastructure.security;

import com.github.tecnoguard.infrastructure.service.UserDetailsServiceImpl;
import com.github.tecnoguard.infrastructure.service.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsServiceImpl userService;

    public JwtFilter(TokenService tokenService, UserDetailsServiceImpl userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }


    private String recoverToken(HttpServletRequest request){
        var authorization = request.getHeader("Authorization");
        if(authorization == null) return null;
        return authorization.replace("Bearer ", "");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recoverToken(request);
        if(token != null){
            var login = tokenService.validateToken(token);
            var user = userService.loadUserByUsername(login);
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

}
