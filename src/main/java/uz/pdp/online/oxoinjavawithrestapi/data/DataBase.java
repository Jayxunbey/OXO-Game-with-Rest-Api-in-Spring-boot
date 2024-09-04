package uz.pdp.online.oxoinjavawithrestapi.data;

import org.springframework.stereotype.Component;
import uz.pdp.online.oxoinjavawithrestapi.domain.GameInProgress;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataBase {

    List<GameInProgress> gamesInProgress;

    {
        gamesInProgress = new ArrayList<>();
    }

}
