package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.endpoints.ws.dto.NameConditionDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.SearchParametersDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.TimeStampRangeDto;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Access_;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntry_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class SearchRepositoryImpl implements SearchRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<DirectoryEntry> search(Actor actor, SearchParametersDto conditions, PageRequest pageRequest) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        final long totalMatches = countMatches(cb, actor, conditions);
        final List<DirectoryEntry> result = findEntries(cb, actor, conditions, pageRequest);
        return new PageImpl<>(result, pageRequest, totalMatches);
    }

    private long countMatches(CriteriaBuilder cb, Actor actor, SearchParametersDto conditions) {
        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<Access> root = cq.from(Access.class);
        final Path<DirectoryEntry> directoryEntryPath = root.get(Access_.entry);

        return entityManager.createQuery(cq.select(cb.count(directoryEntryPath)).where(buildConditions(cb, root, actor, conditions))).getSingleResult();
    }

    private List<DirectoryEntry> findEntries(CriteriaBuilder cb, Actor actor, SearchParametersDto conditions, PageRequest pageRequest) {
        final CriteriaQuery<DirectoryEntry> cq = cb.createQuery(DirectoryEntry.class);
        final Root<Access> root = cq.from(Access.class);
        final Path<DirectoryEntry> directoryEntryPath = root.get(Access_.entry);
        final Order customOrder = createOrderByParam(cb, directoryEntryPath, pageRequest);
        return entityManager.createQuery(cq
                        .select(directoryEntryPath)
                        .where(buildConditions(cb, root, actor, conditions))
                        .orderBy(cb.asc(directoryEntryPath.get(DirectoryEntry_.type)), customOrder)
                )
                .setFirstResult((int) pageRequest.getOffset())
                .setMaxResults(pageRequest.getPageSize())
                .getResultList();
    }

    private Predicate buildConditions(CriteriaBuilder cb, Root<Access> root, Actor actor, SearchParametersDto dto) {
        final Path<DirectoryEntry> directoryEntryPath = root.get(Access_.entry);
        final Path<Actor> actorPath = root.get(Access_.actor);
        return cb.and(
                cb.equal(actorPath, actor),
                createNameCondition(cb, directoryEntryPath, dto.getName()),
                createCreationTimeStampCondition(cb, directoryEntryPath.get(DirectoryEntry_.creationTimeStamp), dto.getCreationDateTime())
        );
    }

    private Predicate createNameCondition(CriteriaBuilder cb, Path<DirectoryEntry> directoryEntryPath, NameConditionDto dto) {
        if (dto == null) {
            return createStaticCondition(cb);
        }

        return switch (dto.getNameComparisonMode()) {
            case EQUALS -> cb.equal(directoryEntryPath.get(DirectoryEntry_.name), dto.getName());
            default -> throw new UnsupportedOperationException();
        };
    }

    private Predicate createCreationTimeStampCondition(CriteriaBuilder cb, Path<OffsetDateTime> creationTimeStampPath, TimeStampRangeDto dto) {
        if (dto == null) {
            return createStaticCondition(cb);
        }

        if (dto.getStart() != null && dto.getEnd() != null) {
            return cb.between(creationTimeStampPath, dto.getStart(), dto.getEnd());
        } else if (dto.getEnd() != null) {
            return cb.lessThanOrEqualTo(creationTimeStampPath, dto.getEnd());
        } else if (dto.getStart() != null) {
            return cb.greaterThanOrEqualTo(creationTimeStampPath, dto.getStart());
        } else {
            return createStaticCondition(cb);
        }
    }

    private Predicate createStaticCondition(CriteriaBuilder cb) {
        return cb.equal(cb.literal(true), true);
    }

    private Order createOrderByParam(CriteriaBuilder cb, Path<DirectoryEntry> dirEntryPath, PageRequest rq) {
        if (rq.getSort() == null) {
            return cb.asc(dirEntryPath.get(DirectoryEntry_.name));
        }

        final Sort.Order orderParam = rq.getSort().get()
                .findAny()
                .orElseThrow();
        final Path<?> propertyPath = dirEntryPath.get(orderParam.getProperty());
        return orderParam.isAscending() ? cb.asc(propertyPath) : cb.desc(propertyPath);
    }
}
