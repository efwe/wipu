package de.fw.wipu.snap.internal;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "wipu.flickr-config")
public interface FlickrConfig {

    String userId();

    String apiKey();
}
