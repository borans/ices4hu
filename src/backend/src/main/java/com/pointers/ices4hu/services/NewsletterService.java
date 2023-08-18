package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.NewsletterPost;
import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.repositories.NewsletterPostRepository;
import com.pointers.ices4hu.repositories.UserRepository;
import com.pointers.ices4hu.responses.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsletterService {
    private final NewsletterPostRepository newsletterPostRepository;
    private final UserRepository userRepository;

    public NewsletterService(NewsletterPostRepository newsletterPostRepository,
                             UserRepository userRepository) {
        this.newsletterPostRepository = newsletterPostRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Object> getNewsletterPosts(String loginId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByLoginID(loginId);
        if (user == null)
            return new ResponseEntity<>(new MessageResponse("No such user!"),
                    HttpStatus.UNAUTHORIZED);

        if (user.getDepartment() == null || user.getDepartment().getId() == null)
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        List<NewsletterPost> newsletterPosts = newsletterPostRepository
                .findNewsletterPostsByDepartmentId(user.getDepartment().getId());

        for (NewsletterPost newsletterPost: newsletterPosts)
            newsletterPost.setContent(null);

        return new ResponseEntity<>(newsletterPosts, HttpStatus.OK);
    }

    public ResponseEntity<Object> viewNewsletterPost(String loginId, Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByLoginID(loginId);
        if (user == null || user.getDepartment() == null || user.getDepartment().getId() == null)
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        NewsletterPost newsletterPost = newsletterPostRepository.findById(postId).orElse(null);
        if (newsletterPost == null)
            return new ResponseEntity<>(new MessageResponse("No such newsletter post!"),
                    HttpStatus.BAD_REQUEST);

        if (newsletterPost.getDepartment() == null || newsletterPost.getDepartment().getId() == null
                || newsletterPost.getDepartment().getId() != user.getDepartment().getId())
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(newsletterPost, HttpStatus.OK);

    }

    public ResponseEntity<Object> createNewsletterPost(String loginId, NewsletterPost newsletterPost) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByLoginID(loginId);
        if (user == null || user.getDepartment() == null || user.getDepartment().getId() == null)
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        newsletterPost.setDepartment(user.getDepartment());
        newsletterPost.setId(null);
        newsletterPost.setUser(user);
        newsletterPost.setCreationDatetime(LocalDateTime.now());

        if (!StringUtils.hasText(newsletterPost.getTopic()))
            return new ResponseEntity<>(new MessageResponse("Topic cannot be empty!"),
                    HttpStatus.BAD_REQUEST);

        if (!StringUtils.hasText(newsletterPost.getContent()))
            return new ResponseEntity<>(new MessageResponse("Content cannot be empty!"),
                    HttpStatus.BAD_REQUEST);

        newsletterPostRepository.save(newsletterPost);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);
    }

    public ResponseEntity<Object> updateNewsletterPost(String loginId, Long postId, NewsletterPost newsletterPostBody) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByLoginID(loginId);
        if (user == null || user.getDepartment() == null || user.getDepartment().getId() == null)
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        NewsletterPost newsletterPost = newsletterPostRepository.findById(postId).orElse(null);
        if (newsletterPost == null)
            return new ResponseEntity<>(new MessageResponse("No such newsletter post!"),
                    HttpStatus.BAD_REQUEST);

        if (newsletterPost.getDepartment() == null || newsletterPost.getDepartment().getId() == null
                || newsletterPost.getDepartment().getId() != user.getDepartment().getId())
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        if (!StringUtils.hasText(newsletterPostBody.getTopic()))
            return new ResponseEntity<>(new MessageResponse("Topic cannot be empty!"),
                    HttpStatus.BAD_REQUEST);

        if (!StringUtils.hasText(newsletterPostBody.getContent()))
            return new ResponseEntity<>(new MessageResponse("Content cannot be empty!"),
                    HttpStatus.BAD_REQUEST);

        newsletterPost.setTopic(newsletterPostBody.getTopic());
        newsletterPost.setContent(newsletterPostBody.getContent());

        newsletterPostRepository.save(newsletterPost);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);

    }

    public ResponseEntity<Object> deleteNewsletterPost(String loginId, Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByLoginID(loginId);
        if (user == null || user.getDepartment() == null || user.getDepartment().getId() == null)
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        NewsletterPost newsletterPost = newsletterPostRepository.findById(postId).orElse(null);
        if (newsletterPost == null)
            return new ResponseEntity<>(new MessageResponse("No such newsletter post!"),
                    HttpStatus.BAD_REQUEST);

        if (newsletterPost.getDepartment() == null || newsletterPost.getDepartment().getId() == null
                || newsletterPost.getDepartment().getId() != user.getDepartment().getId())
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        newsletterPostRepository.delete(newsletterPost);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);
    }
}
