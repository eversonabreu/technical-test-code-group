package br.com.portfolio.dto;

import br.com.portfolio.enums.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private MemberRole role;
}