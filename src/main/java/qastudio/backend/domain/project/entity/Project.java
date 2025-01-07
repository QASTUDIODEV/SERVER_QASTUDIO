package qastudio.backend.domain.project.entity;


import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.global.comon.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectImage;

    @Column(nullable = false)
    private String projectName;

    private String projectUrl;

    @Column(length = 500)
    private String introduction;
}
