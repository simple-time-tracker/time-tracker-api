package com.dovydasvenckus.timetracker.entry;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TimeEntryRepository {

    @Insert("""
        INSERT INTO time_entries (project_id, description, start_date, end_date, deleted, created_by)
        VALUES (#{projectId}, #{description}, #{startDate}, #{endDate}, #{deleted}, #{createdBy})
    """)
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn = "id")
    Long insert(TimeEntry entry);


    @Update("""
        UPDATE time_entries
        SET description = #{description},
            end_date = #{endDate},
            deleted = #{deleted}
        WHERE id = #{id}
        """)
    void update(TimeEntry timeEntry);


    @Select("""
        SELECT * FROM time_entries te
        WHERE te.id = #{id}
        AND te.created_by = #{userId}
        """)
    Optional<TimeEntry> findByIdAndCreatedBy(@Param("id") long id, @Param("userId") UUID userId);

    @Select("""
        SELECT * FROM time_entries te
         WHERE te.created_by = #{userId}
         AND te.end_date IS NULL
         AND te.deleted = FALSE
        """)
    Optional<TimeEntry> findCurrentlyActive(@Param("userId") UUID userId);

    @Select("""
        SELECT COUNT(te.id)
        FROM time_entries te
        WHERE te.created_by = #{userId} AND te.deleted = #{deleted}
    """)
    long countEntries(@Param("userId") UUID userId, @Param("deleted") boolean deleted);

    @Select("""
        SELECT * FROM time_entries te
        WHERE te.created_by = #{userId}
        AND te.deleted = #{deleted}
        ORDER BY te.start_date DESC
        LIMIT #{pageSize}
        OFFSET #{offset}
    """)
    Collection<TimeEntry> findAllByDeleted(@Param("userId") UUID userId,
                                           @Param("deleted") boolean deleted,
                                           @Param("pageSize") int pageSize,
                                           @Param("offset") int offset
    );

    @Select("""
        SELECT COUNT(te.id)
        FROM time_entries te
        WHERE te.project_id = #{projectId}
        AND te.created_by = #{userId}
        AND te.deleted = false
    """)
    long countEntriesByProject(@Param("projectId") long projectId, @Param("userId") UUID userId);

    @Select("""
        SELECT * FROM time_entries te
        WHERE te.project_id = #{projectId}
        AND te.created_by = #{userId}
        AND te.deleted = false
        ORDER BY te.start_date DESC
        LIMIT #{pageSize}
        OFFSET #{offset}
    """)
    List<TimeEntry> findAllByProjectPage(@Param("projectId") long projectId,
                                         @Param("userId") UUID userId,
                                         @Param("pageSize") int pageSize,
                                         @Param("offset") int offset
    );
}
