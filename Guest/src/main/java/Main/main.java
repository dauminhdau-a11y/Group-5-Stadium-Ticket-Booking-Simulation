package Main;

import Controller.GuestController;
import repository.*;
import View.GuestView;

public class main {
    public static void main(String[] args) {
        SeatRepository seatRepo = new SeatRepository();
        SectionRepository secRepo = new SectionRepository();
        MatchRepository matchRepo = new MatchRepository();
        FanRepository fanRepo = new FanRepository();

        GuestController Controller = new GuestController(seatRepo, secRepo, matchRepo, fanRepo);
        GuestView View = new GuestView(Controller);
        
        // BỔ SUNG DÒNG NÀY ĐỂ MỞ MENU:
        View.run();
    }
}

