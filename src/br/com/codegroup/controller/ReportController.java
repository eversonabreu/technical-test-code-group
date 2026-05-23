package br.com.codegroup.controller;

import br.com.codegroup.dto.PortfolioReportDto;
import br.com.codegroup.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Relatórios do portfólio")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/portfolio")
    @Operation(summary = "Gerar relatório resumido do portfólio")
    public ResponseEntity<PortfolioReportDto> generateReport() {
        return ResponseEntity.ok(reportService.generateReport());
    }
}