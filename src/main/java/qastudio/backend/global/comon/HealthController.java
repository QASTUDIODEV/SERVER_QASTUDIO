package qastudio.backend.global.comon;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "헬스 체킹 Controller")
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "I'm healthy!";
    }
}
