package br.com.codegroup.dto;

import br.com.codegroup.enums.MemberRole;
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