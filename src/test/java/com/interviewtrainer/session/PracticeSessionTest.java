package com.interviewtrainer.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class PracticeSessionTest {

    @Test
    void endsOnlyOnce() {
        PracticeSession session = new PracticeSession();
        assertThat(session.isEnded()).isFalse();

        session.end();

        assertThat(session.isEnded()).isTrue();
        assertThat(session.getEndedAt()).isNotNull();
        assertThatIllegalStateException().isThrownBy(session::end);
    }

    @Test
    void renameTrimsAndClearsBlankTitle() {
        PracticeSession session = new PracticeSession();

        session.rename("  Kafka + Spring Security ");
        assertThat(session.getTitle()).isEqualTo("Kafka + Spring Security");

        session.rename("   ");
        assertThat(session.getTitle()).isNull();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> session.rename("x".repeat(PracticeSession.MAX_TITLE_LENGTH + 1)));
    }
}
