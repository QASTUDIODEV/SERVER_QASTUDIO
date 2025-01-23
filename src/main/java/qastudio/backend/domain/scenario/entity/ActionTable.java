package qastudio.backend.domain.scenario.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.global.comon.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ActionTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_table_id")
    private Long id;

    @Column(nullable = false, name = "action_description")
    private String actionDescription;

    @Column(nullable = false)
    private Integer step;

    @Column(nullable = false, name = "action_type")
    private String actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "scenario_id")
    private Scenario scenario;

}