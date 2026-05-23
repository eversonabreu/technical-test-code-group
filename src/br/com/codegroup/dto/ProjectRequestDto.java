package br.com.codegroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.AssertTrue;

@Data
public class ProjectRequestDto {

    @NotBlank
    private String name;

    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    private BigDecimal totalBudget;
    private String description;

    @NotNull
    private Long managerId;

    @AssertTrue(message = "A data prevista de término deve ser posterior à data de início")
    public boolean isExpectedEndDateAfterStartDate() {
        if (startDate == null || expectedEndDate == null) {
            return true;
        }
        return expectedEndDate.isAfter(startDate);
    }
}