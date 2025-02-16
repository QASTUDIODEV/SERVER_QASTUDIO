package qastudio.backend.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.domain.project.entity.enums.ProjectStack;
import qastudio.backend.domain.project.entity.enums.ViewType;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.global.comon.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "view_type")
    @Enumerated(EnumType.STRING)
    private ViewType viewType;

    @Column(name = "assistant_id")
    private String assistantId;

    @Column(name = "development_skill")
    private ProjectStack developmentSkill;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CharacterTable> characterTables = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "user_project_id")
    private List<UserProject> userProjects = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "test_id")
    private List<Test> tests = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Page> pages = new ArrayList<>();

    public void updateProjectInfo(String assistantId, String introduction, String viewType, String developmentSkill) {
        this.assistantId = assistantId;
        this.introduction = introduction;

        try {
            // 대소문자 확인
            this.developmentSkill = ProjectStack.valueOf(developmentSkill.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 ProjectStack 값: " + developmentSkill, e);
        }

        try {
            // 대소문자 확인
            this.viewType = ViewType.valueOf(viewType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 ViewType 값: " + viewType, e);
        }
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
