package br.com.codegroup.service;

import br.com.codegroup.entity.Member;
import br.com.codegroup.enums.MemberRole;
import br.com.codegroup.exception.ResourceNotFoundException;
import br.com.codegroup.repository.MemberRepository;
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