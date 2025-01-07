package qastudio.backend.domain.scenario.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.global.comon.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Feature extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "feature_json", columnDefinition = "TEXT")
    private String featureJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "action_table_id")
    private ActionTable action;
}
