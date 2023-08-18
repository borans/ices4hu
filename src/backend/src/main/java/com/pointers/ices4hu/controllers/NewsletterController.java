package com.pointers.ices4hu.controllers;


import com.pointers.ices4hu.models.NewsletterPost;
import com.pointers.ices4hu.services.NewsletterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('department_manager', 'instructor')")
    public ResponseEntity<Object> getNewsletterPosts(@RequestParam String user) {
        return newsletterService.getNewsletterPosts(user);
    }

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('department_manager', 'instructor')")
    public ResponseEntity<Object> getNewsletterPosts(@RequestParam String user,
                                                     @RequestParam Long post) {
        return newsletterService.viewNewsletterPost(user, post);
    }

    @PostMapping("/department_manager")
    @PreAuthorize("hasAnyAuthority('department_manager')")
    public ResponseEntity<Object> createNewsletterPost(@RequestParam String user,
                                                       @RequestBody NewsletterPost newsletterPost) {
        return newsletterService.createNewsletterPost(user, newsletterPost);
    }

    @PutMapping("/department_manager")
    @PreAuthorize("hasAnyAuthority('department_manager')")
    public ResponseEntity<Object> createNewsletterPost(@RequestParam String user,
                                                       @RequestParam Long post,
                                                       @RequestBody NewsletterPost newsletterPost) {
        return newsletterService.updateNewsletterPost(user, post, newsletterPost);
    }

    @DeleteMapping("/department_manager")
    @PreAuthorize("hasAnyAuthority('department_manager')")
    public ResponseEntity<Object> createNewsletterPost(@RequestParam String user,
                                                       @RequestParam Long post) {
        return newsletterService.deleteNewsletterPost(user, post);
    }


}
