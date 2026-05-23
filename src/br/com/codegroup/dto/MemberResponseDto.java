package br.com.codegroup.dto;

import br.com.codegroup.enums.MemberRole;
import lombok.Data;

@Data
public class MemberResponseDto {
    private Long id;
    private String name;
    private MemberRole role;
}