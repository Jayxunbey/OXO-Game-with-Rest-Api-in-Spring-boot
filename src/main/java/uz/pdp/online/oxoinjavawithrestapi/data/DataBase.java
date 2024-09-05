package uz.pdp.online.oxoinjavawithrestapi.data;

import org.springframework.stereotype.Component;
import uz.pdp.online.oxoinjavawithrestapi.domain.GameInProgress;
import uz.pdp.online.oxoinjavawithrestapi.domain.HistoryOfResultGame;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataBase {

    private List<GameInProgress> gamesInProgressList;
    private List<HistoryOfResultGame> historyOfResultGameList;
    private int counterForChoosingSymbolForUser;

    {
        gamesInProgressList = new ArrayList<>();
        historyOfResultGameList = new ArrayList<>();
        counterForChoosingSymbolForUser = 0;
    }

    public GameInProgress getGamesInProgress() {

        GameInProgress gameInProgress = gamesInProgressList.get(0);

        return gameInProgress;
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

    public boolean hasGame() {
        return !gamesInProgressList.isEmpty();
    }

    public boolean hasSteps() {
        return getGamesInProgress().getGameTable().getTable().stream().anyMatch(cells -> {
            return cells.stream().anyMatch(cell -> {
                if (cell.getMarkedBy() == null) {
                    return true;
                }
                return false;
            });
        });
    }

    public List<HistoryOfResultGame> getHistoryOfResultGameList() {
        return historyOfResultGameList;
    }
}
