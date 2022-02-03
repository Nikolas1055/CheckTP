package ru.esk.checktp.component;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        String redirectUrl = request.getContextPath();
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            redirectUrl = "admin";
        } else if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CHIEF"))) {
            redirectUrl = "chief";
        } else if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MASTER"))) {
            redirectUrl = "master";
        }
        response.sendRedirect(redirectUrl);
    }
}
