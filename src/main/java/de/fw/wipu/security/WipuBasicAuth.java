package de.fw.wipu.security;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "wipu.basic-auth")
public interface WipuBasicAuth {
    String user();
    String password();
}
