package uz.pdp.online.oxoinjavawithrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CellRespDto {
    Boolean isMarked;
    PlayerRespDto markedBy;
}
