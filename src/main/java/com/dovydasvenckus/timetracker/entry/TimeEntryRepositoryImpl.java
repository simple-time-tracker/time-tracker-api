package com.dovydasvenckus.timetracker.entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.UUID;

public class TimeEntryRepositoryImpl implements TimeEntryRepositoryCustom {

    private static final String ACTIVE_ENTRY_QUERY = "SELECT te "
            + "FROM TimeEntry as te "
            + "WHERE te.createdBy = :userId AND te.endDate is null";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<TimeEntry> findCurrentlyActive(UUID userId) {
        Optional<TimeEntry> result;
        TypedQuery<TimeEntry> query = entityManager.createQuery(ACTIVE_ENTRY_QUERY, TimeEntry.class);
        query.setParameter("userId", userId);

        try {
            result = Optional.of(query.getSingleResult());
        } catch (NoResultException nre) {
            result = Optional.empty();
        }
        return result;
    }
}
