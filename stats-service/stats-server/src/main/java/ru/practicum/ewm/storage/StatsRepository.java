package ru.practicum.ewm.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.entity.EndpointHitEntity;
import ru.practicum.ewm.entity.ViewStatsEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHitEntity, Integer> {
    @Query("select new ru.practicum.ewm.entity.ViewStatsEntity(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from EndpointHitEntity as hit " +
            "where hit.timestamp between ?1 AND ?2 " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc")
    List<ViewStatsEntity> getStatsForAllEndpointHitsWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.entity.ViewStatsEntity(hit.app, hit.uri, count(hit.ip)) " +
            "from EndpointHitEntity as hit " +
            "where hit.timestamp between ?1 AND ?2 " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc")
    List<ViewStatsEntity> getStatsForAllEndpointHitsWithNonUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.entity.ViewStatsEntity(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from EndpointHitEntity as hit " +
            "where hit.uri in (?3) AND " +
            "hit.timestamp between ?1 AND ?2 " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc")
    List<ViewStatsEntity> getStatsForEndpointHitsWithUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("select new ru.practicum.ewm.entity.ViewStatsEntity(hit.app, hit.uri, count(hit.ip)) " +
            "from EndpointHitEntity as hit " +
            "where hit.uri in (?3) AND " +
            "hit.timestamp between ?1 AND ?2 " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc")
    List<ViewStatsEntity> getStatsForEndpointHitsWithNonUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);
}
