package isuru.msg_backend.config;

import isuru.msg_backend.entities.User;
import isuru.msg_backend.security.JWTTools;
import isuru.msg_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UserService userService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // chat client will use this to connect to the server
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("http://localhost:3000").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic/");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                log.info("Headers: {}", accessor);

                assert accessor != null;
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                        log.error("no token");
//                        throw new UnauthorizedException("Please add the token in Authorization header");
                    } else {
                        try {
//                            assert authHeader != null;
                            String token = authHeader.substring(7);

                            String id = jwtTools.extractIdFromToken(token);
                            User user = userService.findByUserId(id);

                            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            accessor.setUser(authentication);
                        } catch (Exception ex) {
                            log.error("Authentication error: {}", ex.getMessage());
//                            throw new UnauthorizedException("Please insert a valid token!");
                        }

                    }
                }
                return message;
            }

        });
    }

}
