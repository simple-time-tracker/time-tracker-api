package com.dovydasvenckus.timetracker.entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public class TimeEntryRepositoryImpl implements TimeEntryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TimeEntry findCurrentlyActive() {
        TimeEntry result;
        TypedQuery<TimeEntry> query = entityManager.createQuery("SELECT te FROM TimeEntry as te WHERE te.endDate is null ", TimeEntry.class);

        try {
            result = query.getSingleResult();
        }
        catch (NoResultException nre){
            result = null;
        }
        return result;
    }
}
