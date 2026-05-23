package br.com.codegroup.repository;

import br.com.codegroup.entity.Project;
import br.com.codegroup.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Project> findByStatusAndNameContainingIgnoreCase(
            ProjectStatus status, String name, Pageable pageable);

    List<Project> findByStatus(ProjectStatus status);

    @Query("""
            SELECT COUNT(p) FROM Project p
            JOIN p.members m
            WHERE m.id = :memberId
              AND p.status NOT IN (
                  br.com.codegroup.enums.ProjectStatus.ENCERRADO,
                  br.com.codegroup.enums.ProjectStatus.CANCELADO
              )
            """)
    long countActiveProjectsByMemberId(@Param("memberId") Long memberId);

    @Query("""
        SELECT DISTINCT p FROM Project p
        LEFT JOIN FETCH p.members
        LEFT JOIN FETCH p.manager
        WHERE p.id = :id
        """)
    Optional<Project> findByIdWithDetails(@Param("id") Long id);
}