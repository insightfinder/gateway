package com.insightfinder.gateway.router;

import io.micrometer.observation.annotation.Observed;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Observed
public class DefaultRouter {

    @RequestMapping("/")
    @Observed(name = "home", contextualName = "home")
    public ResponseEntity<Resource> index() {
        Resource indexHtml = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(indexHtml);
    }
}
