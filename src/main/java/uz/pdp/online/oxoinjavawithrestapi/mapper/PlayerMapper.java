package uz.pdp.online.oxoinjavawithrestapi.mapper;

import org.mapstruct.*;
import uz.pdp.online.oxoinjavawithrestapi.domain.Player;
import uz.pdp.online.oxoinjavawithrestapi.dto.response.PlayerRespDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlayerMapper {

    @Mappings({
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "symbol", target = "symbol")
    })
    PlayerRespDto respDto(Player player);

}
