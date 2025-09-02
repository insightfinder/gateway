package com.insightfinder.gateway.router;

import io.micrometer.observation.annotation.Observed;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Observed
public class DefaultRouter {

    @RequestMapping("/")
    @Observed(name = "home", contextualName = "home")
    public String health() {
        return "OK";
    }

}
