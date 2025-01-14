package qastudio.backend.domain.project.repository.PageRole;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PageRoleRepositoryImpl implements PageRoleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

}
