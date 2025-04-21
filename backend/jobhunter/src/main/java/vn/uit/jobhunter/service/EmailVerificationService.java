package vn.uit.jobhunter.service;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.GONE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import vn.uit.jobhunter.domain.User;
import vn.uit.jobhunter.repository.UserRepository;
import vn.uit.jobhunter.util.error.IdInvalidException;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final OtpService otpService;

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationToken(UUID userId, String email) {
        final var token = otpService.generateAndStoreOtp(userId);

        // Localhost URL with userId and OTP token
        final var emailVerificationUrl
                = "http://localhost:8080/api/v1/auth/email/verify?uid=%s&t=%s"
                        .formatted(userId, token);
        final var emailText
                = "Click the link to verify your email: " + emailVerificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setFrom("System");
        message.setText(emailText);

        mailSender.send(message);
    }

    public void resendVerificationToken(String email) throws IdInvalidException {
        User user = userRepository.findByEmail(email);
        if (user.getEmailVerified()) {
            throw new IdInvalidException("Email " + email + " is already verified");
        }

        sendVerificationToken(user.getId(), user.getEmail());
    }

    @Transactional
    public void verifyEmail(UUID userId, String token) {
        if (!otpService.isOtpValid(userId, token)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Token invalid or expired");
        }
        otpService.deleteOtp(userId);

        final var user = userRepository.findById(userId)
                .orElseThrow(()
                        -> new ResponseStatusException(GONE,
                        "User account has been deleted or deactivated"));

        if (user.getEmailVerified()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Email is already verified");
        }

        user.setEmailVerified(true);

        // return user;
    }

}
