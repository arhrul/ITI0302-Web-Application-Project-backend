package org.example.backend.repository.room;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.example.backend.criteria.RoomSearchCriteria;
import org.example.backend.model.Reservation;
import org.example.backend.model.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoomCriteriaRepository {
    private final EntityManager entityManager;

    public static final String PRICE = "price";

    public List<Room> getAllRooms(RoomSearchCriteria criteria, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Room> query = cb.createQuery(Room.class);
        Root<Room> root = query.from(Room.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getType() != null) {
            predicates.add(cb.equal(root.get("type"), criteria.getType()));
        }
        if (criteria.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(PRICE), criteria.getMinPrice()));
        }
        if (criteria.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(PRICE), criteria.getMaxPrice()));
        }
        if (criteria.getCheckInDate() != null && criteria.getCheckOutDate() != null) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Reservation> reservationRoot = subquery.from(Reservation.class);

            subquery.select(reservationRoot.get("roomId").get("id"));

            Predicate overlappingReservations = cb.and(
                    cb.lessThanOrEqualTo(reservationRoot.get("checkInDate"), criteria.getCheckOutDate()),
                    cb.greaterThanOrEqualTo(reservationRoot.get("checkOutDate"), criteria.getCheckInDate())
            );

            subquery.where(overlappingReservations);

            predicates.add(cb.not(root.get("id").in(subquery)));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        Sort.Direction sortDir = criteria.getSortDirection()
                .map(String::toUpperCase)
                .map(Sort.Direction::valueOf)
                .orElse(Sort.Direction.ASC);

        if (sortDir == Sort.Direction.ASC) {
            query.orderBy(cb.asc(root.get(PRICE)));
        } else {
            query.orderBy(cb.desc(root.get(PRICE)));
        }

        TypedQuery<Room> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }
}
