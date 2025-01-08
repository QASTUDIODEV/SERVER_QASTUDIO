package qastudio.backend.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.global.comon.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProjectStack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_stack_id")
    private Long id;

    @Column(name = "project_stack_name", nullable = false)
    private String projectStackName;

    @Column(name = "project_stack_image")
    private String projectStackImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}
