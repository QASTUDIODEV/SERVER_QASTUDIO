package qastudio.backend.domain.scenario.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.global.comon.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Scenario extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scenario_id")
    private Long id;

    @Column(nullable = false, name = "scenario_name")
    private String scenarioName;

    @Column(nullable = false, name = "scenario_description", length = 1000)
    private String scenarioDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "character_table_id")
    private CharacterTable characterTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "page_id")
    private Page page;

    public void update (String scenarioName, String scenarioDescription, CharacterTable characterTable, Page page) {
        this.scenarioName = scenarioName;
        this.scenarioDescription = scenarioDescription;
        this.characterTable = characterTable;
        this.page = page;
    }
}
