package qastudio.backend.domain.scenario.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ActionTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "scenario_name")
    private String actionName;

    @Column(nullable = false)
    private Integer step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "scenario_id")
    private Scenario scenario;
}
