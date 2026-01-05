package de.fw.wipu.emoji;

import de.fw.wipu.emoji.internal.EmojiService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/emoji")
public class EmojiResource {

    @Inject
    EmojiService emojiService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmoji(@QueryParam("count") Integer count) {
        if (count == null) {
            return Response.ok(emojiService.getRandomEmoji()).build();
        }

        if (count <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Count must be greater than 0").build();
        }
        if (count >= 100) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Count must not be greater than 100").build();
        }

        List<String> emojis = emojiService.getRandomEmojis(count);
        return Response.ok(emojis).build();
    }
}
