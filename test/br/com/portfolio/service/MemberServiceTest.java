package br.com.portfolio.service;

import br.com.portfolio.entity.Member;
import br.com.portfolio.enums.MemberRole;
import br.com.portfolio.exception.ResourceNotFoundException;
import br.com.portfolio.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("Deve criar membro com os dados fornecidos")
    void create_deveCriarMembro() {
        Member saved = Member.builder().id(1L).name("Carlos").role(MemberRole.GERENTE).build();
        when(memberRepository.save(any())).thenReturn(saved);

        Member result = memberService.create("Carlos", MemberRole.GERENTE);

        assertThat(result.getName()).isEqualTo("Carlos");
        assertThat(result.getRole()).isEqualTo(MemberRole.GERENTE);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando membro não existe")
    void findById_deveLancarExcecao_quandoNaoEncontrado() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve retornar membro quando encontrado")
    void findById_deveRetornarMembro_quandoEncontrado() {
        Member member = Member.builder().id(1L).name("Ana").role(MemberRole.FUNCIONARIO).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ana");
    }

    @Test
    @DisplayName("Deve retornar lista de todos os membros")
    void findAll_deveRetornarLista() {
        when(memberRepository.findAll()).thenReturn(List.of(
                Member.builder().id(1L).name("Carlos").role(MemberRole.GERENTE).build(),
                Member.builder().id(2L).name("Ana").role(MemberRole.FUNCIONARIO).build()
        ));

        List<Member> result = memberService.findAll();

        assertThat(result).hasSize(2);
    }
}