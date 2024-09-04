package uz.pdp.online.oxoinjavawithrestapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameInProgress {

    private Player firstPlayer;

    private Player secondPlayer;

    private GameTable gameTable;


}
