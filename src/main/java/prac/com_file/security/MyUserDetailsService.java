package prac.com_file.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import prac.com_file.repository.UserRepository;

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 * It is responsible for loading user-specific data from the database.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Locates the user based on the username.
     * @param username The username identifying the user whose data is required.
     * @return A fully populated user record (or throws an exception if the user is not found).
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Here we assume the User entity implements UserDetails,
        // or we would need to map it to a custom UserDetails object.
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
