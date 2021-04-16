package com.test.jwt.lib.file;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:ftp.properties")
public class FtpPropertiesHandler {

    private final Environment environment;

    public String getValue(String key) {
        return environment.getProperty(key);
    }
}
