package qastudio.backend.domain.project.repository.Page;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PageRepositoryImpl implements PageRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

}
