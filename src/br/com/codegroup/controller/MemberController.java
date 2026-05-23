package br.com.codegroup.controller;

import br.com.codegroup.dto.MemberRequestDto;
import br.com.codegroup.dto.MemberResponseDto;
import br.com.codegroup.mapper.MemberMapper;
import br.com.codegroup.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "API externa mockada para gerenciamento de membros")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @PostMapping
    @Operation(summary = "Criar membro")
    public ResponseEntity<MemberResponseDto> create(@Valid @RequestBody MemberRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberMapper.toResponse(memberService.create(dto.getName(), dto.getRole())));
    }

    @GetMapping
    @Operation(summary = "Listar todos os membros")
    public ResponseEntity<List<MemberResponseDto>> findAll() {
        List<MemberResponseDto> response = memberService.findAll().stream()
                .map(memberMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar membro por ID")
    public ResponseEntity<MemberResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(memberMapper.toResponse(memberService.findById(id)));
    }
}