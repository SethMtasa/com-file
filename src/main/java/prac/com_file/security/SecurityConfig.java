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
                                // ========= PUBLIC ENDPOINTS =========
                                .requestMatchers(
                                        "/api/users/login"

                                ).permitAll()

                                // ========= USER MANAGEMENT =========
                                // ADMIN-only endpoints
                                .requestMatchers(
                                                     // GET all users
                                                  // GET active users
                                        "/api/users/inactive",
                                        "/api/users/register"  ,// FIXED: Changed from /api/users/register/**// GET inactive users
                                        "/api/users/active-kars",      // GET active KARs
                                        "/api/users/active-kars/count",// GET active KARs count
                                        "/api/users/with-assigned-files", // GET users with files
                                        "/api/users/role/**",          // GET users by role
                                        "/api/users/search",           // GET search users
                                        "/api/users/deactivate/**",    // PUT deactivate user
                                        "/api/users/activate/**",      // PUT activate user
                                        "/api/users/delete/**",        // DELETE user (backward compat)
                                        "/api/users/update/**"         // PUT update user
                                ).hasRole("ADMIN")

                                // USER can access their own profile
                                .requestMatchers(
                                        "/api/users/{username}",      // GET user by username
                                        "/api/users/profile"          // GET user profile
                                ).authenticated()

                                // ========= COMMERCIAL FILE MANAGEMENT =========
                                // File endpoints - authenticated users
                                .requestMatchers(
                                        "/api/commercial-files",
                                        "/api/commercial-files/**",
                                        "/api/commercial-files/search",
                                        "/api/commercial-files/my-files",
                                        "/api/commercial-files/{id}",
                                        "/api/files/upload",
                                        "/api/files/update/**",
                                        "/api/files/delete/**",
                                        "/api/files/version/**",
                                        "/api/files/**",
                                        "/api/files/search",
                                        "/api/files/expiring/**",
                                        "/api/files/expired",
                                        "/api/files/region/**",
                                        "/api/files/type/**",
                                        "/api/files/kar/**",
                                        "/api/users/all",
                                        "/api/users/active"
                                        ).hasAnyRole("ADMIN", "USER", "SITE_ACQUISITION")

                                // ========= REGION MANAGEMENT =========
                                .requestMatchers(
                                        "/api/regions/**"
                                ).hasAnyRole("ADMIN", "USER")

                                // ========= CHANNEL PARTNER TYPES =========
                                .requestMatchers(
                                        "/api/channel-partner-types/**"
                                ).hasAnyRole("ADMIN", "USER")

                                // ========= FILE HISTORY =========
                                .requestMatchers(
                                        "/api/file-history/file/**",
                                        "/api/file-history/user/**"
                                ).hasAnyRole("ADMIN", "USER", "SITE_ACQUISITION")

                                // ========= NOTIFICATIONS =========
                                .requestMatchers(
                                        "/api/notifications/user/**",
                                        "/api/notifications/{id}/mark-read",
                                        "/api/notifications/user/{userId}/mark-all-read",
                                        "/api/notifications/user/{userId}/unread-count"
                                ).authenticated()

                                // ========= TEST ENDPOINTS =========
                                .requestMatchers("/api/test/**").hasRole("ADMIN")

                                // ========= CATCH-ALL =========
                                .anyRequest().authenticated()
                )
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