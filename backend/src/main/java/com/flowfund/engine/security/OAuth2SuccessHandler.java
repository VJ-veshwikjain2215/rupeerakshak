package com.flowfund.engine.security;

import com.flowfund.engine.entity.User;
import com.flowfund.engine.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigDecimal;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProvider("GOOGLE");
            newUser.setRent(new BigDecimal("1500"));
            newUser.setGroceries(new BigDecimal("600"));
            newUser.setUtilities(new BigDecimal("200"));
            return userRepository.save(newUser);
        });

        String token = jwtUtil.generateToken(user.getEmail());
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/auth?token=" + token);
    }
}
