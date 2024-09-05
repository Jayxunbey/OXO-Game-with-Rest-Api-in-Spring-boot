package uz.pdp.online.oxoinjavawithrestapi.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.pdp.online.oxoinjavawithrestapi.domain.Cell;
import uz.pdp.online.oxoinjavawithrestapi.domain.Player;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class TableRespDto {
    List<List<CellRespDto>> table;
    List<PlayerRespDto> players;
    List<Integer> winnerLine;
    String gameStatus;
}
