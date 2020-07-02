package io.incubator.wazx.oauth;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Noa Swartz
 */
public class PasswordEncode {

    @Test
    public void encode() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.err.println(passwordEncoder.encode("123456"));
    }

}
