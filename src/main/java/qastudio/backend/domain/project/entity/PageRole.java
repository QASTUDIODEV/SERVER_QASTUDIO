package qastudio.backend.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PageRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "character_table_id")
    private CharacterTable characterTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "page_id")
    private Page page;
}
