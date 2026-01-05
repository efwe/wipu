package de.fw.wipu.emoji.internal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class EmojiService {

    @Inject
    EmojiRepository emojiRepository;

    private final Random random = new Random();

    public String getRandomEmoji() {
        return emojiRepository.get(random.nextInt(emojiRepository.count()));
    }

    public List<String> getRandomEmojis(int count) {
        List<String> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(getRandomEmoji());
        }
        return result;
    }
}
