package uz.pdp.online.oxoinjavawithrestapi.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GameInProgress {

    private Player firstPlayer;

    private Player secondPlayer;

    private GameTable gameTable;


}
