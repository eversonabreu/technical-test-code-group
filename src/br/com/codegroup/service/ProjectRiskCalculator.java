package br.com.codegroup.service;

import br.com.codegroup.constants.ProjectRiskConstants;
import br.com.codegroup.enums.RiskLevel;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ProjectRiskCalculator implements RiskCalculator {

    @Override
    public RiskLevel calculate(BigDecimal totalBudget, LocalDate startDate, LocalDate expectedEndDate) {
        RiskLevel budgetRisk   = calculateBudgetRisk(totalBudget);
        RiskLevel durationRisk = calculateDurationRisk(startDate, expectedEndDate);
        return budgetRisk.getLevel() >= durationRisk.getLevel() ? budgetRisk : durationRisk;
    }

    private RiskLevel calculateBudgetRisk(BigDecimal totalBudget) {
        if (totalBudget == null) return RiskLevel.BAIXO;
        if (totalBudget.compareTo(ProjectRiskConstants.BUDGET_THRESHOLD_MEDIUM) <= 0) return RiskLevel.BAIXO;
        if (totalBudget.compareTo(ProjectRiskConstants.BUDGET_THRESHOLD_HIGH)   <= 0) return RiskLevel.MEDIO;
        return RiskLevel.ALTO;
    }

    private RiskLevel calculateDurationRisk(LocalDate startDate, LocalDate expectedEndDate) {
        if (startDate == null || expectedEndDate == null) return RiskLevel.BAIXO;
        long months = ChronoUnit.MONTHS.between(startDate, expectedEndDate);
        if (months <= ProjectRiskConstants.DURATION_THRESHOLD_MEDIUM_MONTHS) return RiskLevel.BAIXO;
        if (months <= ProjectRiskConstants.DURATION_THRESHOLD_HIGH_MONTHS)   return RiskLevel.MEDIO;
        return RiskLevel.ALTO;
    }
}