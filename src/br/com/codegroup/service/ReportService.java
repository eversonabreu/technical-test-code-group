package br.com.codegroup.service;

import br.com.codegroup.dto.PortfolioReportDto;
import br.com.codegroup.entity.Project;
import br.com.codegroup.enums.ProjectStatus;
import br.com.codegroup.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public PortfolioReportDto generateReport() {
        List<Project> allProjects = projectRepository.findAll();

        Map<ProjectStatus, Long> countByStatus = allProjects.stream()
                .collect(Collectors.groupingBy(Project::getStatus, Collectors.counting()));

        Map<ProjectStatus, BigDecimal> budgetByStatus = allProjects.stream()
                .filter(p -> p.getTotalBudget() != null)
                .collect(Collectors.groupingBy(
                        Project::getStatus,
                        Collectors.reducing(BigDecimal.ZERO, Project::getTotalBudget, BigDecimal::add)
                ));

        double avgDurationDays = allProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.ENCERRADO)
                .filter(p -> p.getStartDate() != null && p.getActualEndDate() != null)
                .mapToLong(p -> ChronoUnit.DAYS.between(p.getStartDate(), p.getActualEndDate()))
                .average()
                .orElse(0.0);

        long uniqueMembers = allProjects.stream()
                .flatMap(p -> p.getMembers().stream())
                .map(m -> m.getId())
                .distinct()
                .count();

        return PortfolioReportDto.builder()
                .projectCountByStatus(countByStatus)
                .totalBudgetByStatus(budgetByStatus)
                .averageDurationDaysOfClosedProjects(avgDurationDays)
                .totalUniqueMembers(uniqueMembers)
                .build();
    }
}