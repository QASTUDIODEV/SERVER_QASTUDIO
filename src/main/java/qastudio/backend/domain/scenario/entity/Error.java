package qastudio.backend.domain.scenario.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Error {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "error_code", nullable = false)
    private Integer errorCode;

    @Column(name = "error_message", length = 3000)
    private String errorMessage;

    @Column(name = "error_image")
    private String errorImage;
}
