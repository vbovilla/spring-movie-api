package in.dminc.auth.controller;

import in.dminc.auth.entities.ForgotPassword;
import in.dminc.auth.entities.User;
import in.dminc.auth.repositories.ForgotPasswordRepository;
import in.dminc.auth.repositories.UserRepository;
import in.dminc.dto.ChangePassword;
import in.dminc.dto.MailBody;
import in.dminc.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // send mail for email verification
    @PostMapping("/verifyEMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Please provide a valid email"));

        Integer forgotPasswordOTP = forgotPasswordOTPGenerator();

        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .text("Following is the OTP for your forgot password request \n" + forgotPasswordOTP)
                .subject("OTP for Forgot Password Request")
                .build();

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .oneTimePassword(forgotPasswordOTP)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();

        // save OTP to forgotPasswordRepository to verify it later.
        forgotPasswordRepository.save(forgotPassword);

        // send email with OTP
        emailService.sendSimpleMessage(mailBody);


        return new ResponseEntity<>("Email sent successfully with OTP", HttpStatus.OK);
    }


    @PostMapping("/verifyOtp/{email}/{otp}")
    public ResponseEntity<String> verifyForgotPasswordOTP(@PathVariable String email, @PathVariable Integer otp) {
        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Please provide a valid email"));

        //
        ForgotPassword forgotPassword =
                forgotPasswordRepository.findByUserAndOtp(user, otp)
                        .orElseThrow(() -> new RuntimeException("Please provide a valid OTP"));

        // delete forgotPassword record if OTP is expired
        if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.delete(forgotPassword);
            return new ResponseEntity<>("OTP expired. Please try again.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Please provide a valid OTP", HttpStatus.OK);
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword,
                                                 @PathVariable String email) {
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Please provide a valid email"));

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        if (user.getPassword().equals(encodedPassword)) {
            return new ResponseEntity<>("Please enter a different password", HttpStatus.BAD_REQUEST);
        }

        userRepository.updatePassword(email, encodedPassword);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    private Integer forgotPasswordOTPGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
