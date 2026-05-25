package br.com.portfolio.service;

import br.com.portfolio.entity.Member;
import br.com.portfolio.enums.MemberRole;
import br.com.portfolio.exception.ResourceNotFoundException;
import br.com.portfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member create(String name, MemberRole role) {
        Member member = Member.builder()
                .name(name)
                .role(role)
                .build();
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado: " + id));
    }
}