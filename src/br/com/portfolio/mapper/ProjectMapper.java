package br.com.portfolio.mapper;

import br.com.portfolio.dto.ProjectRequestDto;
import br.com.portfolio.dto.ProjectResponseDto;
import br.com.portfolio.entity.Member;
import br.com.portfolio.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MemberMapper.class})
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "manager", expression = "java(memberStub(dto.getManagerId()))")
    Project toEntity(ProjectRequestDto dto);

    @Mapping(target = "riskLevel", expression = "java(project.getRiskLevel())", ignore = true)
    ProjectResponseDto toResponse(Project project);

    default Member memberStub(Long id) {
        if (id == null) return null;
        Member member = new Member();
        member.setId(id);
        return member;
    }
}