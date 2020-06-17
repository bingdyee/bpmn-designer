package io.incubator.wazx.uaa;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Description:
 *
 * @author yubb
 */
public class Main {

    @Test
    public void passwordEncoder() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.err.println(passwordEncoder.encode("123456"));
    }

}
