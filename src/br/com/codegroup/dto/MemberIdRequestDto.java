package br.com.codegroup.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberIdRequestDto {

    @NotNull
    private Long memberId;
}