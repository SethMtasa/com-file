package prac.com_file.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import prac.com_file.repository.UserRepository;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService myUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor to inject dependencies
    public SecurityConfig(UserDetailsService myUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request
                                // Public endpoints - no authentication required
                                .requestMatchers(
                                        "/login",
                                        "/register"

                                ).permitAll()

                                // User Management - ADMIN only
                                .requestMatchers(

                                        "/users/{id}",
                                        "/users/update/{id}",
                                        "/users/delete/{id}",
                                        "/users"

                                ).hasRole("ADMIN")

                                // File Management - Different access levels
                                .requestMatchers(
                                        "/api/files/upload",
                                        "/api/files/update/**",
                                        "/api/files/delete/**",
                                        "/api/files/version/**"
                                ).hasAnyRole("ADMIN", "USER")

                                .requestMatchers(
                                        "/api/files",
                                        "/api/files/{id}",
                                        "/api/files/search",
                                        "/api/files/expiring/**",
                                        "/api/files/expired",
                                        "/api/files/region/**",
                                        "/api/files/type/**",
                                        "/api/files/my-files"
                                ).hasAnyRole("ADMIN", "USER")

                                .requestMatchers(
                                        "/api/files/kar/**"
                                ).hasAnyRole("ADMIN", "USER")

                                // Region Management - ADMIN only
                                .requestMatchers(
                                        "/api/regions",
                                        "/api/regions/**"
                                ).hasAnyRole("ADMIN", "USER")

                                // Channel Partner Type Management - ADMIN only
                                .requestMatchers(
                                        "/api/channel-partner-types",
                                        "/api/channel-partner-types/**"
                                ).hasAnyRole("ADMIN", "USER")

                                // File History - ADMIN and KAR can view their file history
                                .requestMatchers(
                                        "/api/file-history/file/**",
                                        "/api/file-history/user/**"
                                ).hasAnyRole("ADMIN", "USER")

                                .requestMatchers(
                                        "/api/file-history",
                                        "/api/file-history/**"
                                ).hasRole("ADMIN")

                                // Notifications - Users can view their own notifications
                                .requestMatchers(
                                        "/api/notifications/user/**",
                                        "/api/notifications/{id}/mark-read",
                                        "/api/notifications/user/{userId}/mark-all-read",
                                        "/api/notifications/user/{userId}/unread-count"
                                ).hasAnyRole("ADMIN", "USER")

                                .requestMatchers(
                                        "/api/notifications",
                                        "/api/notifications/file/**",
                                        "/api/notifications/status/**",
                                        "/api/notifications/pending"
                                ).hasRole("ADMIN")

                                // Test endpoints - ADMIN only in production, but open for testing
                                .requestMatchers(
                                        "/api/test/**"
                                ).hasRole("ADMIN")

                                // User profile - Authenticated users can view their own profile
                                .requestMatchers(
                                        "/api/users/profile"
                                ).authenticated()

                                // Any other request requires authentication
                                .anyRequest()
                                .authenticated()
                ).userDetailsService(myUserDetailsService)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider(
            ActiveDirectoryProperties activeDirectoryProperties, UserRepository userRepository) {

        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
                activeDirectoryProperties.getDomain(),
                activeDirectoryProperties.getUrl(),
                activeDirectoryProperties.getRootDn());

        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
        provider.setUserDetailsContextMapper(new CustomUserDetailsContextMapper(userRepository));
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}