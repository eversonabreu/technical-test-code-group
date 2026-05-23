package br.com.codegroup.mapper;

import br.com.codegroup.dto.ProjectRequestDto;
import br.com.codegroup.dto.ProjectResponseDto;
import br.com.codegroup.entity.Member;
import br.com.codegroup.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MemberMapper.class})
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "manager", expression = "java(memberStub(dto.getManagerId()))")
    Project toEntity(ProjectRequestDto dto);

    @Mapping(target = "riskLevel", expression = "java(project.getRiskLevel())")
    ProjectResponseDto toResponse(Project project);

    // Cria um Member com apenas o ID preenchido.
    // O service vai recarregar o registro completo do banco antes de salvar.
    default Member memberStub(Long id) {
        if (id == null) return null;
        Member member = new Member();
        member.setId(id);
        return member;
    }
}