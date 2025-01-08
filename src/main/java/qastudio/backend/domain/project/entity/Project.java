package qastudio.backend.domain.project.entity;


import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.domain.project.entity.enums.ViewType;
import qastudio.backend.global.comon.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(name = "project_image")
    private String projectImage;

    @Column(nullable = false)
    private String projectName;

    @Column(name = "project_url")
    private String projectUrl;

    @Column(length = 500)
    private String introduction;

    @Column(name = "view_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ViewType viewType;
}
