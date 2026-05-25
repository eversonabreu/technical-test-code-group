package br.com.portfolio.service;

import br.com.portfolio.enums.RiskLevel;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface RiskCalculator {

    RiskLevel calculate(BigDecimal totalBudget, LocalDate startDate, LocalDate expectedEndDate);
}
