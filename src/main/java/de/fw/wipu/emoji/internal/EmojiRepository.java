package de.fw.wipu.emoji.internal;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class EmojiRepository {

    private static final List<String> EMOJIS_CACHE = new ArrayList<>();

    public int count() {
        return EMOJIS_CACHE.size();
    }

    public String get(int index) {
        return EMOJIS_CACHE.get(index);
    }

    public List<String> get(List<Integer> indices) {
        return indices.stream()
                .map(EMOJIS_CACHE::get)
                .toList();
    }

    @PostConstruct
    public void loadAllEmojis() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("emoji-test.txt")), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // skip comments / blanks
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Format: codepoints ; status # emoji name
                // 1F600                                      ; fully-qualified     # 😀 grinning face
                String[] parts = line.split(";");
                if (parts.length < 2) continue;

                String codePointsPart = parts[0].trim();
                String rest = parts[1];

                // we usually only want "fully-qualified"
                if (!rest.contains("fully-qualified")) {
                    continue;
                }

                // convert hex code points to a Java String
                String[] hexPoints = codePointsPart.split("\\s+");
                int[] codePoints = new int[hexPoints.length];
                for (int i = 0; i < hexPoints.length; i++) {
                    codePoints[i] = Integer.parseInt(hexPoints[i], 16);
                }
                EMOJIS_CACHE.add(new String(codePoints, 0, codePoints.length));
            }
        }
    }
}
