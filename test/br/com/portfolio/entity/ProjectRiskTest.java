package br.com.portfolio.entity;

import br.com.portfolio.enums.RiskLevel;
import br.com.portfolio.service.ProjectRiskCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectRiskCalculatorTest {

    private ProjectRiskCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ProjectRiskCalculator();
    }

    // ── Risco por orçamento ──────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar BAIXO para orçamento <= 100k e prazo <= 3 meses")
    void calculate_deveBaixo_quandoOrcamentoBaixoEPrazoCurto() {
        RiskLevel result = calculator.calculate(
                new BigDecimal("80000"),
                LocalDate.now(),
                LocalDate.now().plusMonths(2)
        );
        assertThat(result).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    @DisplayName("Deve retornar MEDIO para orçamento entre 100k e 500k")
    void calculate_deveMedio_quandoOrcamentoMedio() {
        RiskLevel result = calculator.calculate(
                new BigDecimal("200000"),
                LocalDate.now(),
                LocalDate.now().plusMonths(2)
        );
        assertThat(result).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    @DisplayName("Deve retornar ALTO para orçamento > 500k")
    void calculate_deveAlto_quandoOrcamentoAlto() {
        RiskLevel result = calculator.calculate(
                new BigDecimal("600000"),
                LocalDate.now(),
                LocalDate.now().plusMonths(2)
        );
        assertThat(result).isEqualTo(RiskLevel.ALTO);
    }

    // ── Risco por duração ────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar MEDIO para prazo entre 3 e 6 meses")
    void calculate_deveMedio_quandoPrazoMedio() {
        RiskLevel result = calculator.calculate(
                new BigDecimal("50000"),
                LocalDate.now(),
                LocalDate.now().plusMonths(4)
        );
        assertThat(result).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    @DisplayName("Deve retornar ALTO para prazo superior a 6 meses")
    void calculate_deveAlto_quandoPrazoLongo() {
        RiskLevel result = calculator.calculate(
                new BigDecimal("50000"),
                LocalDate.now(),
                LocalDate.now().plusMonths(8)
        );
        assertThat(result).isEqualTo(RiskLevel.ALTO);
    }

    // ── Conflito entre riscos ────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar o maior risco quando orçamento e prazo divergem")
    void calculate_deveRetornarMaiorRisco_quandoConflito() {
        // Budget BAIXO, prazo ALTO → resultado deve ser ALTO
        RiskLevel result = calculator.calculate(
                new BigDecimal("50000"),
                LocalDate.now(),
                LocalDate.now().plusMonths(8)
        );
        assertThat(result).isEqualTo(RiskLevel.ALTO);
    }

    // ── Valores nulos ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar BAIXO quando orçamento é nulo")
    void calculate_deveBaixo_quandoOrcamentoNulo() {
        RiskLevel result = calculator.calculate(
                null,
                LocalDate.now(),
                LocalDate.now().plusMonths(2)
        );
        assertThat(result).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    @DisplayName("Deve retornar BAIXO quando datas são nulas")
    void calculate_deveBaixo_quandoDatasNulas() {
        RiskLevel result = calculator.calculate(
                new BigDecimal("50000"),
                null,
                null
        );
        assertThat(result).isEqualTo(RiskLevel.BAIXO);
    }
}