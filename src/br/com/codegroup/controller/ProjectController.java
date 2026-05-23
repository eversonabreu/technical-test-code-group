package br.com.codegroup.controller;

import br.com.codegroup.dto.*;
import br.com.codegroup.enums.ProjectStatus;
import br.com.codegroup.mapper.ProjectMapper;
import br.com.codegroup.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Gerenciamento de portfólio de projetos")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @PostMapping
    @Operation(summary = "Criar projeto")
    public ResponseEntity<ProjectResponseDto> create(@Valid @RequestBody ProjectRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectMapper.toResponse(projectService.create(projectMapper.toEntity(dto))));
    }

    @GetMapping
    @Operation(summary = "Listar projetos com paginação e filtros")
    public ResponseEntity<Page<ProjectResponseDto>> findAll(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(projectService.findAll(status, name, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por ID")
    public ResponseEntity<ProjectResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectMapper.toResponse(projectService.findById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto")
    public ResponseEntity<ProjectResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDto dto) {
        return ResponseEntity.ok(
                projectMapper.toResponse(projectService.update(id, projectMapper.toEntity(dto))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do projeto")
    public ResponseEntity<ProjectResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequestDto dto) {
        return ResponseEntity.ok(
                projectMapper.toResponse(projectService.updateStatus(id, dto.getStatus())));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Adicionar membro ao projeto")
    public ResponseEntity<ProjectResponseDto> addMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberIdRequestDto dto) {
        return ResponseEntity.ok(
                projectMapper.toResponse(projectService.addMember(id, dto.getMemberId())));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    @Operation(summary = "Remover membro do projeto")
    public ResponseEntity<ProjectResponseDto> removeMember(
            @PathVariable Long id,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(
                projectMapper.toResponse(projectService.removeMember(id, memberId)));
    }
}