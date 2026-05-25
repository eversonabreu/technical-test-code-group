package br.com.portfolio.dto;

import br.com.portfolio.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequestDto {

    @NotNull
    private ProjectStatus status;
}