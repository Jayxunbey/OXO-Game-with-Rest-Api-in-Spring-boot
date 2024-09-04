package uz.pdp.online.oxoinjavawithrestapi.data;

import org.springframework.stereotype.Component;
import uz.pdp.online.oxoinjavawithrestapi.domain.GameInProgress;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataBase {

    private List<GameInProgress> gamesInProgressList;
    private int counterForChoosingSymbolForUser;

    {
        gamesInProgressList = new ArrayList<>();
        counterForChoosingSymbolForUser = 0;
    }

    public GameInProgress getGamesInProgress() {
        return gamesInProgressList.get(0);
    }

    public int getCounterForChoosingSymbolForUser() {
        return counterForChoosingSymbolForUser;
    }

    public void addGamesInProgress(GameInProgress gameInProgress) {
        gamesInProgressList.add(gameInProgress);
    }

    public void clearDatabase() {
        if (gamesInProgressList.isEmpty()) {
            return;
        }
        gamesInProgressList.remove(0);
    }
}
