package com.example.socialapi.post;

import com.example.socialapi.error.ForbiddenException;
import com.example.socialapi.error.NotFoundException;
import com.example.socialapi.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository posts;
    private final UserRepository users;

    public PostService(PostRepository posts, UserRepository users) {
        this.posts = posts;
        this.users = users;
    }

    @Transactional
    public Post create(Long authorId, String content) {
        // getReferenceById returns a lightweight proxy: no extra SELECT just to set the foreign key.
        Post post = posts.save(new Post(users.getReferenceById(authorId), content));
        return posts.findWithAuthorById(post.getId()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<Post> recent(Long authorId, int limit) {
        var page = PageRequest.of(0, Math.min(Math.max(limit, 1), 100));
        return authorId == null
                ? posts.findAllByOrderByCreatedAtDescIdDesc(page)
                : posts.findByAuthorIdOrderByCreatedAtDescIdDesc(authorId, page);
    }

    @Transactional(readOnly = true)
    public Post get(Long id) {
        return posts.findWithAuthorById(id).orElseThrow(() -> new NotFoundException("Post not found"));
    }

    @Transactional
    public void delete(Long postId, Long requesterId) {
        Post post = get(postId);
        if (!post.getAuthor().getId().equals(requesterId)) {
            throw new ForbiddenException("You can only delete your own posts");
        }
        posts.delete(post);
    }
}
