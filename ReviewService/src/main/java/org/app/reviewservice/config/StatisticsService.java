package org.app.reviewservice.config;


import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.util.logging.Logger;

public class StatisticsService {
    private final Statistics statistics;
    private static final Logger log = Logger.getLogger(StatisticsService.class.getName());

    public StatisticsService(EntityManagerFactory emf) {
        this.statistics = emf.unwrap(SessionFactory.class).getStatistics();
    }

    public void logStatistics() {
        log.info("Query execution count: " + statistics.getQueryExecutionCount());
        log.info("Entity fetch count: " + statistics.getEntityFetchCount());
        log.info("Collection fetch count: " + statistics.getCollectionFetchCount());
    }

    public void clearStatistics() {
        statistics.clear();
    }
}