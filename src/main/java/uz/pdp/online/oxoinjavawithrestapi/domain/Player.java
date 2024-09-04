package uz.pdp.online.oxoinjavawithrestapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Player {
    private String name;
    private Character symbol;
}
