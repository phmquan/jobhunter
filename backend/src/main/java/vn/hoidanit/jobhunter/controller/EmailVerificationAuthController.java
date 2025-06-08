package vn.hoidanit.jobhunter.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.EmailVerificationService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class EmailVerificationAuthController {

    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepository;

    public EmailVerificationAuthController(EmailVerificationService emailVerificationService, UserRepository userRepository) {
        this.emailVerificationService = emailVerificationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth/email/resend-verification")
    public ResponseEntity<Void> resendVerificationLink(
            @RequestParam String email) throws IdInvalidException {
        User user = userRepository.findByEmail(email);
        if (user != null && user.isEmailVerified()) {
            throw new IdInvalidException("Email " + email + " is already verified");
        }
        emailVerificationService.resendVerificationToken(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auth/email/verify")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam("uid") Long userId, @RequestParam("t") String token) {

        emailVerificationService.verifyEmail(userId, token);

        return ResponseEntity.ok().build();
    }
}
