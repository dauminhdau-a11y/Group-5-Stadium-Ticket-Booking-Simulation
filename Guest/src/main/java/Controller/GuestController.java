/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import model.*;
import repository.*;
import java.util.List;
import java.util.stream.Collectors;
public class GuestController {
private final SeatRepository seatRepository;
    private final SectionRepository sectionRepository;
    private final MatchRepository matchRepository;
    private final FanRepository fanRepository;

    public GuestController(SeatRepository seatRepo, SectionRepository secRepo,
                           MatchRepository matchRepo, FanRepository fanRepo) {
        this.seatRepository = seatRepo;
        this.sectionRepository = secRepo;
        this.matchRepository = matchRepo;
        this.fanRepository = fanRepo;
    }

    // 1. +register(...): Fan
    public Fan register(String username, String password, String fullName, String email, String phone) throws Exception {
        if (username == null || username.trim().length() < 4) {
            throw new Exception("Username phải có ít nhất 4 ký tự!");
        }
        if (password == null || password.length() < 6) {
            throw new Exception("Mật khẩu phải từ 6 ký tự trở lên!");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new Exception("Họ và tên không được để trống!");
        }
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new Exception("Email không đúng định dạng!");
        }
        if (phone == null || !phone.matches("^0[0-9]{9}$")) {
            throw new Exception("Số điện thoại phải gồm 10 chữ số (bắt đầu bằng 0)!");
        }

        List<Fan> existing = fanRepository.findAll();
        boolean usernameExists = existing.stream().anyMatch(f -> f.getUsername().equalsIgnoreCase(username.trim()));
        if (usernameExists) throw new Exception("Tên đăng nhập đã tồn tại!");

        boolean emailExists = existing.stream().anyMatch(f -> f.getEmail().equalsIgnoreCase(email.trim()));
        if (emailExists) throw new Exception("Email đã được đăng ký!");

        String fanId = "FAN_" + System.currentTimeMillis();
        Fan newFan = new Fan(fanId, username.trim(), fullName.trim(), email.trim(), phone.trim(), password);
        fanRepository.save(newFan);
        return newFan;
    }

    // 2. +login(u, p): Fan
    public Fan login(String u, String p) throws Exception {
        if (u == null || u.trim().isEmpty() || p == null || p.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập và mật khẩu không được để trống!");
        }
        return fanRepository.findAll().stream()
                .filter(f -> f.getUsername().equalsIgnoreCase(u.trim()) && f.getPassword().equals(p))
                .findFirst()
                .orElseThrow(() -> new Exception("Sai tên đăng nhập hoặc mật khẩu!"));
    }

    // 3. +updateProfile(...): Fan
    public Fan updateProfile(String fanId, String fullName, String email, String phone, String newPassword) throws Exception {
        Fan fan = fanRepository.findAll().stream()
                .filter(f -> f.getFanId().equalsIgnoreCase(fanId))
                .findFirst()
                .orElseThrow(() -> new Exception("Không tìm thấy người dùng!"));

        if (fullName != null && !fullName.trim().isEmpty()) fan.setFullName(fullName.trim());
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) throw new Exception("Email không hợp lệ!");
            fan.setEmail(email.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("^0[0-9]{9}$")) throw new Exception("SĐT không hợp lệ!");
            fan.setPhone(phone.trim());
        }
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 6) throw new Exception("Mật khẩu mới phải từ 6 ký tự!");
            fan.setPassword(newPassword);
        }

        fanRepository.update(fan);
        return fan;
    }

    // 4. +searchMatches(date, stadium, team): List<Match>
    public List<Match> searchMatches(String date, String stadium, String team) {
        return matchRepository.findAll().stream()
                .filter(m -> date == null || date.isEmpty() || m.getMatchDate().equalsIgnoreCase(date))
                .filter(m -> stadium == null || stadium.isEmpty() || m.getStadiumId().equalsIgnoreCase(stadium))
                .filter(m -> team == null || team.isEmpty() ||
                        m.getHomeTeam().toLowerCase().contains(team.toLowerCase()) ||
                        m.getAwayTeam().toLowerCase().contains(team.toLowerCase()) ||
                        m.getMatchName().toLowerCase().contains(team.toLowerCase()))
                .collect(Collectors.toList());
    }

    // 5. +isMatchSoldOut(matchId): boolean
    public boolean isMatchSoldOut(String matchId) {
        List<Seat> seats = seatRepository.findAll().stream()
                .filter(s -> s.getMatchId().equalsIgnoreCase(matchId))
                .collect(Collectors.toList());
        if (seats.isEmpty()) return false;
        // Trận đấu sold out khi không còn ghế nào AVAILABLE
        return seats.stream().noneMatch(s -> s.getStatus() == SeatStatus.available);
    }

    // 6. +getSeatsForSection(matchId, secId): List<Seat>
    public List<Seat> getSeatsForSection(String matchId, String secId) {
        return seatRepository.findAll().stream()
                .filter(s -> s.getMatchId().equalsIgnoreCase(matchId) && s.getSectionId().equalsIgnoreCase(secId))
                .collect(Collectors.toList());
    }

    // Helper phụ trợ hiển thị bảng giá khán đài
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }
}
