package br.com.codegroup.service;

import br.com.codegroup.entity.Member;
import br.com.codegroup.entity.Project;
import br.com.codegroup.enums.MemberRole;
import br.com.codegroup.enums.ProjectStatus;
import br.com.codegroup.exception.BusinessException;
import br.com.codegroup.exception.ResourceNotFoundException;
import br.com.codegroup.mapper.ProjectMapper;
import br.com.codegroup.repository.MemberRepository;
import br.com.codegroup.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Member manager;
    private Member funcionario;
    private Project project;

    @BeforeEach
    void setUp() {
        manager    = Member.builder().id(1L).name("Carlos").role(MemberRole.GERENTE).build();
        funcionario = Member.builder().id(2L).name("Ana").role(MemberRole.FUNCIONARIO).build();
        project = Project.builder()
                .id(1L)
                .name("Projeto Teste")
                .status(ProjectStatus.EM_ANALISE)
                .manager(manager)
                .members(new ArrayList<>())
                .build();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar projeto com status EM_ANALISE")
    void create_deveDefinirStatusEmAnalise() {
        Project input = Project.builder().name("Novo").manager(manager).members(new ArrayList<>()).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.create(input);

        assertThat(result.getStatus()).isEqualTo(ProjectStatus.EM_ANALISE);
        verify(projectRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando gerente não existe")
    void create_deveLancarExcecao_quandoGerenteNaoExiste() {
        Project input = Project.builder().manager(manager).members(new ArrayList<>()).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.create(input))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Gerente não encontrado");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar BusinessException ao excluir projeto INICIADO")
    void delete_deveLancarExcecao_quandoStatusIniciado() {
        project.setStatus(ProjectStatus.INICIADO);
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.delete(1L))
                .isInstanceOf(BusinessException.class);
        verify(projectRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao excluir projeto EM_ANDAMENTO")
    void delete_deveLancarExcecao_quandoStatusEmAndamento() {
        project.setStatus(ProjectStatus.EM_ANDAMENTO);
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.delete(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao excluir projeto ENCERRADO")
    void delete_deveLancarExcecao_quandoStatusEncerrado() {
        project.setStatus(ProjectStatus.ENCERRADO);
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.delete(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Deve excluir projeto quando status permite")
    void delete_deveExcluir_quandoStatusEmAnalise() {
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));

        projectService.delete(1L);

        verify(projectRepository).delete(project);
    }

    // ── UPDATE STATUS ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar status com transição válida")
    void updateStatus_deveAtualizar_quandoTransicaoValida() {
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.updateStatus(1L, ProjectStatus.ANALISE_REALIZADA);

        assertThat(result.getStatus()).isEqualTo(ProjectStatus.ANALISE_REALIZADA);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando transição pula etapa")
    void updateStatus_deveLancarExcecao_quandoTransicaoPulaEtapa() {
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.updateStatus(1L, ProjectStatus.ENCERRADO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não é permitida");
    }

    @Test
    @DisplayName("Deve permitir cancelar em qualquer status ativo")
    void updateStatus_devePermitirCancelar_emQualquerStatus() {
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.updateStatus(1L, ProjectStatus.CANCELADO);

        assertThat(result.getStatus()).isEqualTo(ProjectStatus.CANCELADO);
    }

    // ── ADD MEMBER ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar BusinessException quando membro não é FUNCIONARIO")
    void addMember_deveLancarExcecao_quandoMembroNaoEFuncionario() {
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(manager));

        assertThatThrownBy(() -> projectService.addMember(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("FUNCIONARIO");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando membro já está no projeto")
    void addMember_deveLancarExcecao_quandoMembroJaAlocado() {
        project.getMembers().add(funcionario);
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(funcionario));

        assertThatThrownBy(() -> projectService.addMember(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já está alocado neste projeto");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando projeto atingiu limite de 10 membros")
    void addMember_deveLancarExcecao_quandoLimiteAtingido() {
        for (long i = 10; i < 20; i++) {
            project.getMembers().add(Member.builder().id(i).role(MemberRole.FUNCIONARIO).build());
        }
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(funcionario));

        assertThatThrownBy(() -> projectService.addMember(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("máximo de 10 membros");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando membro já está em 3 projetos ativos")
    void addMember_deveLancarExcecao_quandoMembroEm3ProjetosAtivos() {
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(funcionario));
        when(projectRepository.countActiveProjectsByMemberId(2L)).thenReturn(3L);

        assertThatThrownBy(() -> projectService.addMember(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("3 projetos ativos");
    }

    @Test
    @DisplayName("Deve adicionar membro com sucesso")
    void addMember_deveAdicionarComSucesso() {
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(funcionario));
        when(projectRepository.countActiveProjectsByMemberId(2L)).thenReturn(0L);
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.addMember(1L, 2L);

        assertThat(result.getMembers()).contains(funcionario);
    }

    // ── REMOVE MEMBER ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar BusinessException quando membro não está no projeto")
    void removeMember_deveLancarExcecao_quandoMembroNaoEstaNoProejto() {
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.removeMember(1L, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não está alocado");
    }

    @Test
    @DisplayName("Deve remover membro com sucesso")
    void removeMember_deveRemoverComSucesso() {
        project.getMembers().add(funcionario);
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.removeMember(1L, 2L);

        assertThat(result.getMembers()).doesNotContain(funcionario);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar os dados do projeto com sucesso")
    void update_deveAtualizarDados_quandoProjetoExiste() {
        Project dados = Project.builder()
                .name("Novo Nome")
                .manager(manager)
                .members(new ArrayList<>())
                .build();
        when(projectRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.update(1L, dados);

        assertThat(result.getName()).isEqualTo("Novo Nome");
        verify(projectRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar projeto inexistente")
    void update_deveLancarExcecao_quandoProjetoNaoExiste() {
        Project dados = Project.builder().name("X").manager(manager).members(new ArrayList<>()).build();
        when(projectRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.update(99L, dados))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}