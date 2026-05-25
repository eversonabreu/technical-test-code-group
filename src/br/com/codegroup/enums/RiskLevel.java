package br.com.codegroup.enums;

import lombok.Getter;

@Getter
public enum RiskLevel {
    BAIXO(1), MEDIO(2), ALTO(3);

    private final int level;

    RiskLevel(int level) {
        this.level = level;
    }
}