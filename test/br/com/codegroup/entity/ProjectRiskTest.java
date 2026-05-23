package br.com.codegroup.entity;

import br.com.codegroup.enums.RiskLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectRiskTest {

    @Test
    @DisplayName("Deve retornar BAIXO para orçamento <= 100k e prazo <= 3 meses")
    void getRiskLevel_deveBaixo_quandoOrcamentoBaixoEPrazoCurto() {
        Project project = Project.builder()
                .totalBudget(new BigDecimal("80000"))
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(2))
                .build();

        assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    @DisplayName("Deve retornar MEDIO para orçamento entre 100k e 500k")
    void getRiskLevel_deveMedio_quandoOrcamentoMedio() {
        Project project = Project.builder()
                .totalBudget(new BigDecimal("200000"))
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(2))
                .build();

        assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    @DisplayName("Deve retornar ALTO para orçamento > 500k")
    void getRiskLevel_deveAlto_quandoOrcamentoAlto() {
        Project project = Project.builder()
                .totalBudget(new BigDecimal("600000"))
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(2))
                .build();

        assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    @DisplayName("Deve retornar MEDIO para prazo entre 3 e 6 meses")
    void getRiskLevel_deveMedio_quandoPrazoMedio() {
        Project project = Project.builder()
                .totalBudget(new BigDecimal("50000"))
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(4))
                .build();

        assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    @DisplayName("Deve retornar ALTO para prazo superior a 6 meses")
    void getRiskLevel_deveAlto_quandoPrazoLongo() {
        Project project = Project.builder()
                .totalBudget(new BigDecimal("50000"))
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(8))
                .build();

        assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    @DisplayName("Deve retornar o maior risco entre orçamento e prazo")
    void getRiskLevel_deveRetornarMaiorRisco_quandoConflito() {
        // Budget BAIXO, prazo ALTO → resultado deve ser ALTO
        Project project = Project.builder()
                .totalBudget(new BigDecimal("50000"))
                .startDate(LocalDate.now())
                .expectedEndDate(LocalDate.now().plusMonths(8))
                .build();

        assertThat(project.getRiskLevel()).isEqualTo(RiskLevel.ALTO);
    }
}