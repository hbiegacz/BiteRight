package com.bd2_team6.biteright.entities.verification_code;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bd2_team6.biteright.entities.user.User;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUser(User user);
}
