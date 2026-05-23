package br.com.codegroup.dto;

import br.com.codegroup.enums.MemberRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MemberResponseDto {
    private Long id;
    private String name;
    private MemberRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}