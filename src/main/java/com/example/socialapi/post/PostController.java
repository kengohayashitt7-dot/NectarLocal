package com.example.socialapi.post;

import com.example.socialapi.post.PostDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@Valid @RequestBody CreatePostRequest req, Authentication auth) {
        return PostResponse.from(service.create(Long.valueOf(auth.getName()), req.content()));
    }

    /** Newest first. Optional filters: ?authorId=1&limit=20 */
    @GetMapping
    public List<PostResponse> list(@RequestParam(required = false) Long authorId,
                                   @RequestParam(defaultValue = "20") int limit) {
        return service.recent(authorId, limit).stream().map(PostResponse::from).toList();
    }

    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id) {
        return PostResponse.from(service.get(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, Long.valueOf(auth.getName()));
    }
}
