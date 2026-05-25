package br.com.codegroup.service;

import br.com.codegroup.enums.RiskLevel;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface RiskCalculator {

    RiskLevel calculate(BigDecimal totalBudget, LocalDate startDate, LocalDate expectedEndDate);
}
