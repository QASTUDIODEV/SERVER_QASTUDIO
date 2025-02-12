package qastudio.backend.domain.selenium.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchPageSourceRequest {
    private String targetUrl;
}