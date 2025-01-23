package qastudio.backend.domain.test.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.entity.enums.State;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.global.comon.domain.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Test extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long id;

    @Column(nullable = false, name = "test_date")
    private LocalDate testDate;

    @Column(nullable = false, name = "test_name")
    private String testName;

    @Column(nullable = false)
    private Integer attainment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false)
    private Double time;

    @Column(columnDefinition = "TEXT", nullable = false, name = "scenario_record")
    private String scenarioRecord;

    @Column(nullable = false, name = "total_action_count")
    private Integer totalActionCount;

    @Column(nullable = false, name = "execution_action_count")
    private Integer executionActionCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "page_id")
    private Page page;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_id")
    private Error error;
}
