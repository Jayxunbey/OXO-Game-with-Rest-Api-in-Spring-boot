package uz.pdp.online.oxoinjavawithrestapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MarkingCellReqDto {
     Integer cellNumber;
     Character symbol;

}
