package qastudio.backend.domain.project.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import qastudio.backend.domain.project.dto.request.CharacterRequest.UpdateCharacter;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.global.comon.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CharacterTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_table_id")
    private Long id;

    @Column(name = "character_name", nullable = false)
    private String characterName;

    @Column(name = "character_description", nullable = false)
    private String characterDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToMany(mappedBy = "characterTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PageRole> pageRoles;

    @OneToMany(mappedBy = "characterTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scenario> scenarios = new ArrayList<>();

    public void update (UpdateCharacter updateCharacter) {
        this.characterName = updateCharacter.getCharacterName();
        this.characterDescription = updateCharacter.getCharacterDescription();
    }
}
