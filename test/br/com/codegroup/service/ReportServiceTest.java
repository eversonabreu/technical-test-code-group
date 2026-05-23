package br.com.codegroup.service;

import br.com.codegroup.dto.PortfolioReportDto;
import br.com.codegroup.entity.Member;
import br.com.codegroup.entity.Project;
import br.com.codegroup.enums.MemberRole;
import br.com.codegroup.enums.ProjectStatus;
import br.com.codegroup.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("Deve gerar relatório com contagem de projetos por status")
    void generateReport_deveContarProjetosPorStatus() {
        when(projectRepository.findAll()).thenReturn(projetosMock());

        PortfolioReportDto report = reportService.generateReport();

        assertThat(report.getProjectCountByStatus()).containsKey(ProjectStatus.ENCERRADO);
        assertThat(report.getProjectCountByStatus().get(ProjectStatus.ENCERRADO)).isEqualTo(1L);
        assertThat(report.getProjectCountByStatus().get(ProjectStatus.EM_ANALISE)).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve calcular total orçado por status")
    void generateReport_deveCalcularTotalOrcadoPorStatus() {
        when(projectRepository.findAll()).thenReturn(projetosMock());

        PortfolioReportDto report = reportService.generateReport();

        assertThat(report.getTotalBudgetByStatus().get(ProjectStatus.ENCERRADO))
                .isEqualByComparingTo(new BigDecimal("150000"));
    }

    @Test
    @DisplayName("Deve calcular média de duração dos projetos encerrados")
    void generateReport_deveCalcularMediaDuracao() {
        when(projectRepository.findAll()).thenReturn(projetosMock());

        PortfolioReportDto report = reportService.generateReport();

        // Projeto encerrado: 01/01 a 01/04 = 90 dias
        assertThat(report.getAverageDurationDaysOfClosedProjects()).isEqualTo(90.0);
    }

    @Test
    @DisplayName("Deve contar membros únicos alocados")
    void generateReport_deveContarMembrosUnicos() {
        when(projectRepository.findAll()).thenReturn(projetosMock());

        PortfolioReportDto report = reportService.generateReport();

        assertThat(report.getTotalUniqueMembers()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve retornar zero quando não há projetos encerrados")
    void generateReport_deveRetornarZero_quandoSemProjetosEncerrados() {
        Member membro = Member.builder().id(1L).name("Ana").role(MemberRole.FUNCIONARIO).build();
        Project projeto = Project.builder()
                .id(1L)
                .status(ProjectStatus.EM_ANALISE)
                .totalBudget(new BigDecimal("50000"))
                .members(new ArrayList<>())
                .build();
        when(projectRepository.findAll()).thenReturn(List.of(projeto));

        PortfolioReportDto report = reportService.generateReport();

        assertThat(report.getAverageDurationDaysOfClosedProjects()).isEqualTo(0.0);
        assertThat(report.getTotalUniqueMembers()).isEqualTo(0L);
    }

    private List<Project> projetosMock() {
        Member membro = Member.builder().id(2L).name("Ana").role(MemberRole.FUNCIONARIO).build();

        Project encerrado = Project.builder()
                .id(1L)
                .status(ProjectStatus.ENCERRADO)
                .totalBudget(new BigDecimal("150000"))
                .startDate(LocalDate.of(2026, 1, 1))
                .actualEndDate(LocalDate.of(2026, 4, 1))
                .members(new ArrayList<>(List.of(membro)))
                .build();

        Project emAnalise = Project.builder()
                .id(2L)
                .status(ProjectStatus.EM_ANALISE)
                .totalBudget(new BigDecimal("80000"))
                .members(new ArrayList<>())
                .build();

        return List.of(encerrado, emAnalise);
    }
}