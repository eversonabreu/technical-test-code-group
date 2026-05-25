package br.com.portfolio.repository;

import br.com.portfolio.entity.Member;
import br.com.portfolio.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByRole(MemberRole role);

    boolean existsByNameAndRole(String name, MemberRole role);
}