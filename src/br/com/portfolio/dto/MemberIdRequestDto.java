package br.com.portfolio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberIdRequestDto {

    @NotNull
    private Long memberId;
}