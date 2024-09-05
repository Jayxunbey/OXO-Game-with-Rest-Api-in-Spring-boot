package uz.pdp.online.oxoinjavawithrestapi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class HistoryOfResultGame {
    Player winner;
    Date date;
}
