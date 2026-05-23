package br.com.codegroup.dto;

import br.com.codegroup.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequestDto {

    @NotNull
    private ProjectStatus status;
}