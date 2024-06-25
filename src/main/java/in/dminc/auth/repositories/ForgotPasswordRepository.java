package in.dminc.auth.repositories;

import in.dminc.auth.entities.ForgotPassword;
import in.dminc.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {

    @Query("select fp from ForgotPassword fp where fp.user=?1 and fp.oneTimePassword=?2")
    Optional<ForgotPassword> findByUserAndOtp(User user, Integer otp);

}
