package uz.pdp.online.oxoinjavawithrestapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.online.oxoinjavawithrestapi.dto.request.MarkingCellReqDto;
import uz.pdp.online.oxoinjavawithrestapi.dto.response.TableRespDto;
import uz.pdp.online.oxoinjavawithrestapi.service.GameService;

@RestController
@RequestMapping("/api/game/with-bot")
public class GameWithBotContoller {

    private final GameService gameService;

    public GameWithBotContoller(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/new")
    public ResponseEntity<TableRespDto> newGame() {

        TableRespDto tableRespDto = gameService.newGame();

        return ResponseEntity.ok(tableRespDto);

    }

    @PostMapping("/mark-cell")
    public ResponseEntity<TableRespDto> markCell(@RequestBody MarkingCellReqDto markingCellReqDto) {
        TableRespDto tableRespDto = gameService.markCell(markingCellReqDto);

        return ResponseEntity.ok(tableRespDto);
    }


}
