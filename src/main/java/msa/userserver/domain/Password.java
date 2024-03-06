package msa.userserver.domain;

import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Password {
    private String password;

    private LocalDateTime expirationDate;

    private int failedCount;

    private long ttl;

    @Builder
    public Password(final String value) {
        this.ttl = 90 * 24 * 60 * 60;; // 90 days
        this.password = encodePassword(value);
        this.expirationDate = extendExpirationDate();
    }

    public boolean isMatched(final String rawPassword) {
        if (failedCount >= 5) throw new IllegalArgumentException();

        boolean matches = isMatches(rawPassword);
        updateFailedCount(matches);
        return matches;
    }

    public void changePassword(final String newPassword, final String oldPassword) {
        if (isMatched(oldPassword)) {
            password = encodePassword(newPassword);
            extendExpirationDate();
        }
    }

    public boolean isExpiration() {
        return LocalDateTime.now().isAfter(expirationDate);
    }


    private String encodePassword(final String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    private LocalDateTime extendExpirationDate() {
        return LocalDateTime.now().plusSeconds(ttl);
    }

    private void resetFailedCount() {
        this.failedCount = 0;
    }

    public void updateFailedCount(boolean matches) {
        if (matches)
            resetFailedCount();
        else
            increaseFailCount();
    }

    private void increaseFailCount() {
        this.failedCount++;
    }

    private boolean isMatches(String rawPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, this.password);
    }
}
