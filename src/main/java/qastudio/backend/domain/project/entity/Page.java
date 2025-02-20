package qastudio.backend.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.global.comon.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Page extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id")
    private Long id;

    @Column(name = "page_name", nullable = false)
    private String pageName;

    @Column(name = "page_description", nullable = false)
    private String pageDescription;

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "page_role_id")
    private List<PageRole> pageRoles = new ArrayList<>();

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "page_scenario_id")
    private List<PageScenario> pageScenarios = new ArrayList<>();

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scenario> scenarios = new ArrayList<>();
}
