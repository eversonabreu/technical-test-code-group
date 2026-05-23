package br.com.codegroup.enums;

public enum ProjectStatus {
    EM_ANALISE,
    ANALISE_REALIZADA,
    ANALISE_APROVADA,
    INICIADO,
    PLANEJADO,
    EM_ANDAMENTO,
    ENCERRADO,
    CANCELADO;

    private static final ProjectStatus[] ORDERED_FLOW = {
            EM_ANALISE, ANALISE_REALIZADA, ANALISE_APROVADA,
            INICIADO, PLANEJADO, EM_ANDAMENTO, ENCERRADO
    };

    public boolean canTransitionTo(ProjectStatus next) {
        if (next == CANCELADO) return this != ENCERRADO && this != CANCELADO;

        for (int i = 0; i < ORDERED_FLOW.length - 1; i++) {
            if (ORDERED_FLOW[i] == this) {
                return ORDERED_FLOW[i + 1] == next;
            }
        }
        return false;
    }

    public boolean isDeletionForbidden() {
        return this == INICIADO || this == EM_ANDAMENTO || this == ENCERRADO;
    }
}