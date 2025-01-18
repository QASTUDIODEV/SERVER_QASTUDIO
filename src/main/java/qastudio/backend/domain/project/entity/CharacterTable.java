package qastudio.backend.domain.project.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
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

    @OneToMany(mappedBy = "characterTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PageRole> pageRoles;

    public void updateCharacter(String characterName, String characterDescription) {
        this.characterName = characterName;
        this.characterDescription = characterDescription;
    }
}
