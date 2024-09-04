package uz.pdp.online.oxoinjavawithrestapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.online.oxoinjavawithrestapi.service.GameService;

@RestController
@RequestMapping("/api/game/with-bot")
public class GameWithBotContoller {

    private final GameService gameService;

    public GameWithBotContoller(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    public void newGame() {
        gameService.newGame();
    }


}
