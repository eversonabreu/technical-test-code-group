package br.com.codegroup.mapper;

import br.com.codegroup.dto.MemberRequestDto;
import br.com.codegroup.dto.MemberResponseDto;
import br.com.codegroup.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    Member toEntity(MemberRequestDto dto);

    MemberResponseDto toResponse(Member member);
}