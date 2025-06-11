package vn.uit.jobhunter.service.token;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.repository.UserRepository;


@Component
@AllArgsConstructor
public class TokenService {
    private final UserRepository userRepository;
   
     public void updateUserToken(String token, String email) {
        User currentUser = userRepository.findByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }
}
