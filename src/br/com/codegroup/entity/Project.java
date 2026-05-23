package br.com.codegroup.entity;

import br.com.codegroup.enums.ProjectStatus;
import br.com.codegroup.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "total_budget", precision = 15, scale = 2)
    private BigDecimal totalBudget;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Member manager;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.EM_ANALISE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> members = new ArrayList<>();

    @Transient
    public RiskLevel getRiskLevel() {
        RiskLevel budgetRisk = calculateBudgetRisk();
        RiskLevel durationRisk = calculateDurationRisk();
        return budgetRisk.ordinal() >= durationRisk.ordinal() ? budgetRisk : durationRisk;
    }

    private RiskLevel calculateBudgetRisk() {
        if (totalBudget == null) return RiskLevel.BAIXO;
        if (totalBudget.compareTo(new BigDecimal("100000")) <= 0) return RiskLevel.BAIXO;
        if (totalBudget.compareTo(new BigDecimal("500000")) <= 0) return RiskLevel.MEDIO;
        return RiskLevel.ALTO;
    }

    private RiskLevel calculateDurationRisk() {
        if (startDate == null || expectedEndDate == null) return RiskLevel.BAIXO;
        long months = ChronoUnit.MONTHS.between(startDate, expectedEndDate);
        if (months <= 3) return RiskLevel.BAIXO;
        if (months <= 6) return RiskLevel.MEDIO;
        return RiskLevel.ALTO;
    }
}