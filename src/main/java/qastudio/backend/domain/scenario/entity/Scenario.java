package qastudio.backend.domain.scenario.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.domain.project.entity.CharacterTable;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "scenario_name")
    private String scenarioName;

    @Column(nullable = false, name = "scenario_description", length = 1000)
    private String scenarioDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "character_table_id")
    private CharacterTable characterTable;
}
