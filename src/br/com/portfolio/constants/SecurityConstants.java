package br.com.portfolio.constants;

public class SecurityConstants {

    // ******************* Configs de autenticação  ***************************
    // Os valores abaixo servem apenas como demonstração de autenticação básica,
    // por este motivo os valores estão fixos.
    public static final String USER_TEST = "admin";
    public static final String PASSWORD_TEST = "admin123";
    // ************************************************************************

    public static final String[] PUBLIC_URLS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };
}