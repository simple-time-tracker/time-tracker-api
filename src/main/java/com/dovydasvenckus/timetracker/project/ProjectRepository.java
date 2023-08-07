package com.dovydasvenckus.timetracker.project;

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
public interface ProjectRepository {

    String PROJECT_SUMMARY_BASE_SELECT = """
        SELECT p.id, p."name", p.archived, p.date_created,
              SUM(CASE WHEN te.deleted = FALSE AND end_date IS NOT NULL
                THEN extract(epoch FROM (te.end_date - te.start_date) * 1000)
                ELSE 0
                END
              ) AS timeSpentInMilliseconds
            FROM projects p
            LEFT JOIN time_entries te ON te.project_id = p.id
        """;

    String PROJECT_SUMMARY_ORDER_BY = " ORDER BY p.name DESC ";

    @Insert("""
            INSERT INTO projects (name, archived, date_created, created_by, date_modified, modified_by)
            VALUES (#{name}, #{archived}, #{dateCreated}, #{createdBy}, #{dateModified}, #{modifiedBy})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(Project project);

    @Update("""
        UPDATE projects
        SET name = #{name},
            archived = #{archived},
            date_modified = #{dateModified},
            modified_by = #{modifiedBy}
        WHERE id = #{id}
        """)
    void update(Project project);

    @Select("SELECT * FROM projects WHERE id = #{id}")
    Optional<Project> findById(@Param("id") long id);

    @Select("SELECT * FROM projects WHERE id = #{id} AND created_by = #{userId}")
    Optional<Project> findByIdAndCreatedBy(@Param("id") long id, @Param("userId") UUID userId);

    @Select("SELECT * FROM projects WHERE name = #{name} AND created_by = #{userId}")
    Optional<Project> findByNameAndCreatedBy(@Param("name") String name, @Param("userId") UUID userId);

    @Select("""
                SELECT COUNT(p.id)
                FROM projects p
                WHERE created_by = #{userId} AND archived = #{archived}
        """)
    long countProjectsByCreatedByAndAndArchiveStatus(@Param("userId") UUID userId,
                                                     @Param("archived") boolean isArchived);

    @Select(PROJECT_SUMMARY_BASE_SELECT +
        """
                    WHERE p.archived = #{archived}
                    AND p.created_by = #{userId}
                    GROUP BY p.id
            """ + PROJECT_SUMMARY_ORDER_BY + "LIMIT #{pageSize} OFFSET #{offset}"
    )
    List<ProjectReadDTO> findAllByCreatedByAndArchived(@Param("userId") UUID userId,
                                                       @Param("archived") boolean isArchived,
                                                       @Param("pageSize") int pageSize,
                                                       @Param("offset") int offset);

    @Select(PROJECT_SUMMARY_BASE_SELECT +
        """
            WHERE p.id = #{id}
            AND p.created_by = #{userId}
            GROUP BY p.id
            """ + PROJECT_SUMMARY_ORDER_BY
    )
    Optional<ProjectReadDTO> findProjectSummaryById(@Param("id") long id, @Param("userId") UUID userId);

    @Select("""
        SELECT p.* FROM projects p
        WHERE p.created_by = #{userId} AND archived = FALSE
        """ + PROJECT_SUMMARY_ORDER_BY
    )
    List<Project> findByCreatedByAndArchivedFalse(@Param("userId") UUID userId);
}
