package uz.pdp.online.oxoinjavawithrestapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.online.oxoinjavawithrestapi.service.GameService;

@RestController
@RequestMapping("/api/game")
public class GameContoller {

    private final GameService gameService;

    public GameContoller(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    public void newGame() {
//        gameService
    }


}
