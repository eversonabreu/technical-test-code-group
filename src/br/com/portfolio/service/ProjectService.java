package br.com.portfolio.service;

import br.com.portfolio.dto.ProjectResponseDto;
import br.com.portfolio.entity.Member;
import br.com.portfolio.entity.Project;
import br.com.portfolio.enums.MemberRole;
import br.com.portfolio.enums.ProjectStatus;
import br.com.portfolio.exception.BusinessException;
import br.com.portfolio.exception.ResourceNotFoundException;
import br.com.portfolio.mapper.ProjectMapper;
import br.com.portfolio.repository.MemberRepository;
import br.com.portfolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMapper projectMapper;
    private final RiskCalculator riskCalculator;

    @Transactional
    public Project create(Project project) {
        Member manager = memberRepository.findById(project.getManager().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Gerente não encontrado"));
        project.setManager(manager);
        project.setStatus(ProjectStatus.EM_ANALISE);
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project findById(Long id) {
        return projectRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> findAll(ProjectStatus status, String name, Pageable pageable) {
        Page<Project> page;

        if (status != null && name != null && !name.isBlank()) {
            page = projectRepository.findByStatusAndNameContainingIgnoreCase(status, name, pageable);
        } else if (status != null) {
            page = projectRepository.findByStatus(status, pageable);
        } else if (name != null && !name.isBlank()) {
            page = projectRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = projectRepository.findAll(pageable);
        }

        return page.map(this::toResponseWithRisk);
    }

    @Transactional
    public Project update(Long id, Project projectData) {
        Project project = findById(id);
        project.setName(projectData.getName());
        project.setStartDate(projectData.getStartDate());
        project.setExpectedEndDate(projectData.getExpectedEndDate());
        project.setActualEndDate(projectData.getActualEndDate());
        project.setTotalBudget(projectData.getTotalBudget());
        project.setDescription(projectData.getDescription());

        if (projectData.getManager() != null && projectData.getManager().getId() != null) {
            Member manager = memberRepository.findById(projectData.getManager().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Gerente não encontrado"));
            project.setManager(manager);
        }

        return projectRepository.save(project);
    }

    @Transactional
    public void delete(Long id) {
        Project project = findById(id);
        if (project.getStatus().isDeletionForbidden()) {
            throw new BusinessException(
                    "Projeto com status '" + project.getStatus() + "' não pode ser excluído.");
        }
        projectRepository.delete(project);
    }

    @Transactional
    public Project updateStatus(Long id, ProjectStatus newStatus) {
        Project project = findById(id);
        if (!project.getStatus().canTransitionTo(newStatus)) {
            throw new BusinessException(
                    "Transição de '" + project.getStatus() + "' para '" + newStatus + "' não é permitida.");
        }
        project.setStatus(newStatus);
        return projectRepository.save(project);
    }

    @Transactional
    public Project addMember(Long projectId, Long memberId) {
        Project project = findById(projectId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado: " + memberId));

        if (member.getRole() != MemberRole.FUNCIONARIO) {
            throw new BusinessException(
                    "Apenas membros com atribuição 'FUNCIONARIO' podem ser associados a projetos.");
        }
        if (project.getMembers().stream().anyMatch(m -> m.getId().equals(memberId))) {
            throw new BusinessException("Membro já está alocado neste projeto.");
        }
        if (project.getMembers().size() >= 10) {
            throw new BusinessException("O projeto já atingiu o limite máximo de 10 membros.");
        }

        long activeProjects = projectRepository.countActiveProjectsByMemberId(memberId);
        if (activeProjects >= 3) {
            throw new BusinessException(
                    "Membro já está alocado em 3 projetos ativos simultaneamente.");
        }

        project.getMembers().add(member);
        return projectRepository.save(project);
    }

    @Transactional
    public Project removeMember(Long projectId, Long memberId) {
        Project project = findById(projectId);
        boolean removed = project.getMembers().removeIf(m -> m.getId().equals(memberId));
        if (!removed) {
            throw new BusinessException("Membro não está alocado neste projeto.");
        }
        return projectRepository.save(project);
    }

    public ProjectResponseDto toResponseWithRisk(Project project) {
        ProjectResponseDto dto = projectMapper.toResponse(project);
        dto.setRiskLevel(riskCalculator.calculate(
                project.getTotalBudget(),
                project.getStartDate(),
                project.getExpectedEndDate()
        ));
        return dto;
    }
}