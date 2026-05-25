package br.com.portfolio.mapper;

import br.com.portfolio.dto.MemberRequestDto;
import br.com.portfolio.dto.MemberResponseDto;
import br.com.portfolio.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    Member toEntity(MemberRequestDto dto);

    MemberResponseDto toResponse(Member member);
}