package br.com.portfolio.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectStatusTest {

    @Test
    @DisplayName("Deve permitir transições válidas em sequência")
    void canTransitionTo_devePermitirSequenciaCorreta() {
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.ANALISE_REALIZADA)).isTrue();
        assertThat(ProjectStatus.ANALISE_REALIZADA.canTransitionTo(ProjectStatus.ANALISE_APROVADA)).isTrue();
        assertThat(ProjectStatus.ANALISE_APROVADA.canTransitionTo(ProjectStatus.INICIADO)).isTrue();
        assertThat(ProjectStatus.INICIADO.canTransitionTo(ProjectStatus.PLANEJADO)).isTrue();
        assertThat(ProjectStatus.PLANEJADO.canTransitionTo(ProjectStatus.EM_ANDAMENTO)).isTrue();
        assertThat(ProjectStatus.EM_ANDAMENTO.canTransitionTo(ProjectStatus.ENCERRADO)).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar transições que pulam etapas")
    void canTransitionTo_deveRejeitarPuloDeEtapa() {
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.INICIADO)).isFalse();
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.ENCERRADO)).isFalse();
        assertThat(ProjectStatus.ANALISE_APROVADA.canTransitionTo(ProjectStatus.EM_ANDAMENTO)).isFalse();
    }

    @Test
    @DisplayName("Deve permitir cancelar a partir de qualquer status ativo")
    void canTransitionTo_devePermitirCancelar_emQualquerStatusAtivo() {
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.CANCELADO)).isTrue();
        assertThat(ProjectStatus.INICIADO.canTransitionTo(ProjectStatus.CANCELADO)).isTrue();
        assertThat(ProjectStatus.EM_ANDAMENTO.canTransitionTo(ProjectStatus.CANCELADO)).isTrue();
    }

    @Test
    @DisplayName("Não deve permitir cancelar projeto já encerrado")
    void canTransitionTo_naoDevePermitirCancelar_quandoEncerrado() {
        assertThat(ProjectStatus.ENCERRADO.canTransitionTo(ProjectStatus.CANCELADO)).isFalse();
    }

    @Test
    @DisplayName("isDeletionForbidden deve retornar true para INICIADO, EM_ANDAMENTO e ENCERRADO")
    void isDeletionForbidden_deveRetornarTrue_paraStatusProibidos() {
        assertThat(ProjectStatus.INICIADO.isDeletionForbidden()).isTrue();
        assertThat(ProjectStatus.EM_ANDAMENTO.isDeletionForbidden()).isTrue();
        assertThat(ProjectStatus.ENCERRADO.isDeletionForbidden()).isTrue();
    }

    @Test
    @DisplayName("isDeletionForbidden deve retornar false para demais status")
    void isDeletionForbidden_deveRetornarFalse_paraDemaisStatus() {
        assertThat(ProjectStatus.EM_ANALISE.isDeletionForbidden()).isFalse();
        assertThat(ProjectStatus.ANALISE_REALIZADA.isDeletionForbidden()).isFalse();
        assertThat(ProjectStatus.CANCELADO.isDeletionForbidden()).isFalse();
    }
}