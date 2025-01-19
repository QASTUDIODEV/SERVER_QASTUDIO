package qastudio.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.global.comon.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "banner_image")
    private String bannerImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountTable> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProject> userProjects = new ArrayList<>();

    public void addAccount(AccountTable accountTable) {
        this.accounts.add(accountTable);
    }

    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void updateUserInfo(String nickname, String profileImage, String bannerImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.bannerImage = bannerImage;
    }
}