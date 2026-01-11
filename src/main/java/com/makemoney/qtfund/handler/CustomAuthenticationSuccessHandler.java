package com.makemoney.qtfund.handler;

import com.makemoney.qtfund.entity.LoginLog;
import com.makemoney.qtfund.entity.User;
import com.makemoney.qtfund.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            // Check expiration first (double check, though UserDetailsService should have handled it)
            if (user.getExpirationDate() != null && user.getExpirationDate().before(new Date())) {
                // Should not happen if UserDetailsService is correct, but just in case
                request.getSession().invalidate();
                response.sendRedirect("/login.html?error=expired");
                return;
            }

            // Record Login
            String ip = getClientIp(request);
            String deviceInfo = request.getHeader("User-Agent");
            
            if (user.getLoginLogs() == null) {
                user.setLoginLogs(new ArrayList<>());
            }
            
            // Add new log
            user.getLoginLogs().add(new LoginLog(ip, deviceInfo, new Date()));
            
            // "qtfund" user exception
            if (!"qtfund".equals(user.getUsername())) {
                // Check unique devices/IPs
                Set<String> uniqueDevices = new HashSet<>();
                for (LoginLog log : user.getLoginLogs()) {
                    // Unique identifier: IP + UserAgent string
                    // This is strict. A user changing browser might be counted as new device.
                    uniqueDevices.add(log.getIp() + "|" + log.getDeviceInfo());
                }

                if (uniqueDevices.size() >= 3) { // Changed to >= based on "超过或等于" (exceed or equal)
                    // Expire user
                    user.setExpirationDate(new Date()); // Set expired NOW
                    userRepository.save(user);
                    
                    // Invalidate session immediately
                    request.getSession().invalidate();
                    response.sendRedirect("/login.html?error=max_devices");
                    return;
                }
            }
            
            userRepository.save(user);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
