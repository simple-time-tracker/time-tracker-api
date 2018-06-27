package com.dovydasvenckus.timetracker.entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public class TimeEntryRepositoryImpl implements TimeEntryRepositoryCustom {

    private static final String ACTIVE_ENTRY_QUERY = "SELECT te FROM TimeEntry as te WHERE te.endDate is null";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TimeEntry findCurrentlyActive() {
        TimeEntry result;
        TypedQuery<TimeEntry> query = entityManager.createQuery(ACTIVE_ENTRY_QUERY, TimeEntry.class);

        try {
            result = query.getSingleResult();
        } catch (NoResultException nre) {
            result = null;
        }
        return result;
    }
}
