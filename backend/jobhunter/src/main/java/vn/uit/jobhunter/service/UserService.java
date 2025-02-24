package vn.uit.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getUser(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    public User handleCreateUser(User user) {
        return userRepository.save(user);
    }

    public User handleGetUserByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }
}
