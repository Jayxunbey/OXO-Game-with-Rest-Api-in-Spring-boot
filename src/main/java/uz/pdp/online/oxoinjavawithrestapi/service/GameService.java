package uz.pdp.online.oxoinjavawithrestapi.service;

import org.springframework.stereotype.Service;
import uz.pdp.online.oxoinjavawithrestapi.data.DataBase;
import uz.pdp.online.oxoinjavawithrestapi.domain.Cell;
import uz.pdp.online.oxoinjavawithrestapi.domain.GameInProgress;
import uz.pdp.online.oxoinjavawithrestapi.domain.GameTable;
import uz.pdp.online.oxoinjavawithrestapi.domain.Player;
import uz.pdp.online.oxoinjavawithrestapi.dto.request.MarkingCellReqDto;
import uz.pdp.online.oxoinjavawithrestapi.dto.response.CellRespDto;
import uz.pdp.online.oxoinjavawithrestapi.dto.response.TableRespDto;
import uz.pdp.online.oxoinjavawithrestapi.mapper.PlayerMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameService {
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

        TableRespDto respObj = getRespObj();

        return respObj;
    }

    private TableRespDto getRespObj() {
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
                .gameStatus("ongoing")
                .build();

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

        while (generatedNumberForStep > -1) {
            for (List<Cell> row : table) {
                for (Cell cell : row) {
                    if (cell.getMarkedBy() == null) {
                        if (generatedNumberForStep == 0) {
                            cell.setMarkedBy(bot);
                            return;
                        }
                        generatedNumberForStep--;
                    }
                }
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
                            botTakesOneStep(
                                    dataBase.getGamesInProgress(),
                                    dataBase.getGamesInProgress().getFirstPlayer().getName().equals("bot")?
                                            dataBase.getGamesInProgress().getFirstPlayer(): dataBase.getGamesInProgress().getSecondPlayer());
                        }
                    }
                }
            }
        }

        return getRespObj();


    }

    private boolean checkSymbolForQueue(Character symbol) {
        Player firstPlayer = dataBase.getGamesInProgress().getFirstPlayer();
        if (firstPlayer.getName().equals("user")) {
            if (firstPlayer.getSymbol()==symbol) {
                return true;
            }
        }

        Player secondPlayer = dataBase.getGamesInProgress().getSecondPlayer();
        if (secondPlayer.getName().equals("user")) {
            if (secondPlayer.getSymbol()==symbol) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIsNotMarked(Integer cellNumber) {
        GameInProgress gamesInProgress = dataBase.getGamesInProgress();
        GameTable gameTable = gamesInProgress.getGameTable();
        int counter=0;
        for (List<Cell> cells : gameTable.getTable()) {
            for (Cell cell : cells) {
                if (cellNumber==(++counter)) {
                    if (cell.getMarkedBy()==null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
