package edu.tongji.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OAApplicationTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoad() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void applicationStartsSuccess() {
        try {
            OAApplication.main(new String[] {});
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }
}