package uz.pdp.online.oxoinjavawithrestapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.pdp.online.oxoinjavawithrestapi.data.DataBase;
import uz.pdp.online.oxoinjavawithrestapi.domain.*;
import uz.pdp.online.oxoinjavawithrestapi.dto.request.MarkingCellReqDto;
import uz.pdp.online.oxoinjavawithrestapi.dto.response.CellRespDto;
import uz.pdp.online.oxoinjavawithrestapi.dto.response.HistoryOfResultGameRespDto;
import uz.pdp.online.oxoinjavawithrestapi.dto.response.TableRespDto;
import uz.pdp.online.oxoinjavawithrestapi.mapper.PlayerMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final DataBase dataBase;
    private final PlayerMapper playerMapper;

    public GameService(DataBase dataBase, PlayerMapper playerMapper) {
        this.dataBase = dataBase;
        this.playerMapper = playerMapper;
    }

    public TableRespDto newGame() {

        Player player1 = new Player("user", dataBase.getCounterForChoosingSymbolForUser() % 2 == 0 ? 'O' : 'X');
        Player player2 = new Player("bot", dataBase.getCounterForChoosingSymbolForUser() % 2 == 0 ? 'X' : 'O');

        boolean isPlayer1StartsTheGame = selectPlayerForStartAsRondomly();

        GameInProgress gameInProgress = GameInProgress
                .builder()
                .firstPlayer(isPlayer1StartsTheGame ? player1 : player2)
                .secondPlayer(isPlayer1StartsTheGame ? player2 : player1)
                .build();

        gameInProgress.setGameTable(generateTable(3, 3));

        if (!isPlayer1StartsTheGame) {
            botTakesOneStep(gameInProgress, player2);
        }

        // Nima uchun Databaseni clear qilyapman chunki bu o'yin bitta odamlik qilingan chunuchin
        dataBase.clearDatabase();

        dataBase.addGamesInProgress(gameInProgress);

        TableRespDto respObj = getRespObj(null, "ongoing");

        return respObj;
    }

    private TableRespDto getRespObj(List<Integer> winnerLine, String gameStatus) {
        GameInProgress gamesInProgress = dataBase.getGamesInProgress();
        List<List<Cell>> table = gamesInProgress.getGameTable().getTable();

        List<List<CellRespDto>> resultTable = new ArrayList<>();

        for (List<Cell> row : table) {
            List<CellRespDto> rowRespDtos = new ArrayList<>();
            for (Cell cell : row) {
                Player player = cell.getMarkedBy();
                boolean isMarked = player != null;

                rowRespDtos.add(
                        new CellRespDto(
                                isMarked,
                                isMarked ? playerMapper.respDto(player) : null)
                );
            }

            resultTable.add(rowRespDtos);

        }

        TableRespDto tableRespDto = TableRespDto
                .builder()
                .table(resultTable)
                .players(
                        List.of(
                                playerMapper.respDto(dataBase.getGamesInProgress().getFirstPlayer()),
                                playerMapper.respDto(dataBase.getGamesInProgress().getSecondPlayer())
                        )
                )
                .winnerLine(winnerLine)
                .gameStatus(gameStatus)
                .build();

        if (gameStatus.equals("won") || gameStatus.equals("equal")) {
            dataBase.clearDatabase();
        }

        return tableRespDto;

    }

    private void botTakesOneStep(GameInProgress gameInProgress, Player bot) {
        List<List<Cell>> table = gameInProgress.getGameTable().getTable();

        /**
         * Bu yerda shunaqangi logika ishlatganman yani 25 gacha raqam generatsiya qilib olaman va
         * hali belgilanmagan joylarni ustidan yurib o'tib 25 chi belgilanmagan joyni belgilash uchun.
         * Nima uchun 25, chunki o'yin kengayishi ko'p odamlar o'ynashi va 3x3 table emas balki 5x5 tablelarda ham o'ynash
         * mumkin bo'ladi.
         */
        int generatedNumberForStep = new Random().nextInt(25);

        boolean haveOneCellAtLeast = false;

        while (generatedNumberForStep > -1) {
            for (List<Cell> row : table) {
                for (Cell cell : row) {
                    if (cell.getMarkedBy() == null) {
                        haveOneCellAtLeast = true;
                        if (generatedNumberForStep == 0) {
                            cell.setMarkedBy(bot);
                            return;
                        }
                        generatedNumberForStep--;
                    }
                }
            }
            if (!haveOneCellAtLeast) {
                return;
            }
        }
    }

    private GameTable generateTable(int rowSize, int columnSize) {

        List<List<Cell>> table = new ArrayList<>();

        for (int i = 0; i < rowSize; i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < columnSize; j++) {
                row.add(new Cell());
            }

            table.add(row);

        }

        return new GameTable(table);
    }

    private boolean selectPlayerForStartAsRondomly() {
        return new Random().nextBoolean();
    }

    public TableRespDto markCell(MarkingCellReqDto markingCellReqDto) {
        Integer cellNumber = markingCellReqDto.getCellNumber();
        Character symbol = markingCellReqDto.getSymbol();

        TableRespDto respDto;

        if (checkIsNotMarked(cellNumber)) {
            if (checkSymbolForQueue(symbol)) {
                List<List<Cell>> table = dataBase.getGamesInProgress().getGameTable().getTable();
                int counter = 0;
                for (List<Cell> cells : table) {
                    for (Cell cell : cells) {
                        if (cellNumber == (++counter)) {
                            cell.setMarkedBy(
                                    new Player("user", symbol)
                            );

                            respDto = getWithCheckingWinner();
                            if (!respDto.getGameStatus().equals("ongoing")) {
                                return respDto;
                            }

                            botTakesOneStep(
                                    dataBase.getGamesInProgress(),
                                    dataBase.getGamesInProgress().getFirstPlayer().getName().equals("bot") ?
                                            dataBase.getGamesInProgress().getFirstPlayer() : dataBase.getGamesInProgress().getSecondPlayer());
                        }
                    }
                }
            }
        }

        return getWithCheckingWinner();
    }

    private TableRespDto getWithCheckingWinner() {

        List<Integer> winnerLine = getWinnerLine();
        Boolean hasStep = hasStep();
        if (!hasStep) {
            if (winnerLine == null) {
                completeAndSaveResultTheGame(winnerLine);
                return getRespObj(winnerLine, "equal");
            }
        }

        if (winnerLine != null) {
            completeAndSaveResultTheGame(winnerLine);
            return getRespObj(winnerLine, "won");
        }

        return getRespObj(winnerLine, "ongoing");


    }

    private void completeAndSaveResultTheGame(List<Integer> winnerLine) {

        List<HistoryOfResultGame> historyOfResultGameList = dataBase.getHistoryOfResultGameList();

        if (winnerLine == null) {
            historyOfResultGameList.add(
                    new HistoryOfResultGame(null, new Date())
            );
            return;
        }

        Integer cellNumber = winnerLine.get(0);
        int counter = 0;


        GameTable gameTable = dataBase.getGamesInProgress().getGameTable();
        for (List<Cell> cells : gameTable.getTable()) {
            for (Cell cell : cells) {
                if (cellNumber == (++counter)) {
                    historyOfResultGameList.add(
                            new HistoryOfResultGame(cell.getMarkedBy(), new Date())
                    );

                    return;
                }
            }
        }


    }

    private Boolean hasStep() {
        return dataBase.hasSteps();
    }

    private List<Integer> getWinnerLine() {
        List<List<Integer>> cases = List.of(
                List.of(1, 2, 3),
                List.of(4, 5, 6),
                List.of(7, 8, 9),
                List.of(1, 4, 7),
                List.of(2, 5, 8),
                List.of(3, 6, 9),
                List.of(1, 5, 9),
                List.of(3, 5, 7)
        );

        List<List<Cell>> table = dataBase.getGamesInProgress().getGameTable().getTable();

        List<Cell> singleList = new ArrayList<>();

        for (List<Cell> row : table) {
            for (Cell cell : row) {
                singleList.add(cell);
            }
        }

        for (List<Integer> caseNumbers : cases) {

            if ((singleList.get(caseNumbers.get(0) - 1).getMarkedBy() != null && singleList.get(caseNumbers.get(1) - 1).getMarkedBy() != null && singleList.get(caseNumbers.get(2) - 1).getMarkedBy() != null) &&
                    (singleList.get(caseNumbers.get(0) - 1).getMarkedBy().getSymbol().charValue() == singleList.get(caseNumbers.get(1) - 1).getMarkedBy().getSymbol().charValue() &&
                            singleList.get(caseNumbers.get(0) - 1).getMarkedBy().getSymbol().charValue() == singleList.get(caseNumbers.get(2) - 1).getMarkedBy().getSymbol().charValue())) {
                return caseNumbers;
            }

        }

        return null;


    }

    private boolean checkSymbolForQueue(Character symbol) {
        Player firstPlayer = dataBase.getGamesInProgress().getFirstPlayer();
        if (firstPlayer.getName().equals("user")) {
            if (firstPlayer.getSymbol() == symbol) {
                return true;
            }
        }

        Player secondPlayer = dataBase.getGamesInProgress().getSecondPlayer();
        if (secondPlayer.getName().equals("user")) {
            if (secondPlayer.getSymbol() == symbol) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIsNotMarked(Integer cellNumber) {
        GameInProgress gamesInProgress = null;
        try {
            gamesInProgress = dataBase.getGamesInProgress();

            GameTable gameTable = gamesInProgress.getGameTable();
            int counter = 0;
            for (List<Cell> cells : gameTable.getTable()) {
                for (Cell cell : cells) {
                    if (cellNumber == (++counter)) {
                        if (cell.getMarkedBy() == null) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return false;
    }

    public TableRespDto checkingAndGet() {
        if (dataBase.hasGame()) {
            return getWithCheckingWinner();
        } else {
            return newGame();
        }
    }

    public List<HistoryOfResultGameRespDto> getAllResult() {

        List<HistoryOfResultGame> historyOfResultGameList = dataBase.getHistoryOfResultGameList();

        List<HistoryOfResultGameRespDto> returnResultDto = new ArrayList<>();

        for (HistoryOfResultGame historyOfResultGame : historyOfResultGameList) {
            returnResultDto.add(
                    new HistoryOfResultGameRespDto(
                            playerMapper.respDto(historyOfResultGame.getWinner()),
                            String.valueOf(historyOfResultGame.getDate().getTime())
                    )
            );
        }

        return returnResultDto;

    }
}
