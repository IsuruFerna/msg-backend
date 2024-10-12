package isuru.msg_backend.security;

import isuru.msg_backend.entities.User;
import isuru.msg_backend.exceptions.UnauthorizedException;
import isuru.msg_backend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Please add the token in Authorization header!");
        } else {
            String accessToken = authHeader.substring(7);
            String id = jwtTools.extractIdFromToken(accessToken);
            User user = userService.findByUserId(id);

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String path = request.getServletPath();

        return pathMatcher.match("/auth/**", request.getServletPath())
                || pathMatcher.match("/chat/**", request.getServletPath())
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }

}
