package uz.pdp.online.oxoinjavawithrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uz.pdp.online.oxoinjavawithrestapi.domain.Player;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class HistoryOfResultGameRespDto {
    PlayerRespDto winner;
    String dateEpoch;
}
