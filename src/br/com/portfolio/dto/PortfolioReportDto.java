package br.com.portfolio.dto;

import br.com.portfolio.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class PortfolioReportDto {
    private Map<ProjectStatus, Long> projectCountByStatus;
    private Map<ProjectStatus, BigDecimal> totalBudgetByStatus;
    private Double averageDurationDaysOfClosedProjects;
    private Long totalUniqueMembers;
}