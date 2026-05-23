package br.com.codegroup.dto;

import br.com.codegroup.enums.ProjectStatus;
import br.com.codegroup.enums.RiskLevel;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class ProjectResponseDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    private BigDecimal totalBudget;
    private String description;
    private MemberResponseDto manager;
    private ProjectStatus status;
    private RiskLevel riskLevel;
    private List<MemberResponseDto> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}