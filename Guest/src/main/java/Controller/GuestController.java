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

    // 1. USE CASE: Register
    public Fan register(String username, String password, String fullName, String email, String phone) throws Exception {
        if (username == null || username.trim().length() < 4) {
            throw new Exception("Username phai co it nhat 4 ky tu (ban da nhap: '" + (username == null ? "" : username.trim()) + "')!");
        }
        if (password == null || password.length() < 6) {
            throw new Exception("Mat khau phai tu 6 ky tu tro len (ban da nhap: " + (password == null ? 0 : password.length()) + " ky tu)!");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new Exception("Ho va ten khong duoc de trong!");
        }
        if (email == null || !email.trim().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new Exception("Email khong dung dinh dang (ban da nhap: '" + email + "')!");
        }
        final String cleanPhone = phone == null ? "" : phone.replaceAll("\\s+", "").trim();
        if (!cleanPhone.matches("^0[0-9]{9}$")) {
            throw new Exception("So dien thoai phai gom dung 10 chu so bat dau bang 0 (ban da nhap: '" + phone + "')!");
        }

        final String cleanUsername = username.trim();
        final String cleanEmail = email.trim();
        final String cleanFullName = fullName.trim();

        List<Fan> existing = fanRepository.findAll();
        boolean usernameExists = existing.stream().anyMatch(f -> f.getUsername().equalsIgnoreCase(cleanUsername));
        if (usernameExists) throw new Exception("Ten dang nhap '" + cleanUsername + "' da ton tai!");

        boolean emailExists = existing.stream().anyMatch(f -> f.getEmail().equalsIgnoreCase(cleanEmail));
        if (emailExists) throw new Exception("Email '" + cleanEmail + "' da duoc dang ky truoc do!");

        // Tao FanID theo so thu tu tang dan (vd: FAN_0001, FAN_0002, ..., FAN_1001)
        int maxNum = 0;
        for (Fan f : existing) {
            String id = f.getFanId();
            if (id != null) {
                String numPart = id.replaceAll("\\D+", "");
                if (!numPart.isEmpty()) {
                    try {
                        int num = Integer.parseInt(numPart);
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        String fanId = String.format("FAN_%04d", maxNum + 1);

        Fan newFan = new Fan(fanId, cleanUsername, cleanFullName, cleanEmail, cleanPhone, password);
        boolean saved = fanRepository.save(newFan);
        if (!saved) {
            throw new Exception("Loi: Khong the luu du lieu tai khoan vao file!");
        }
        return newFan;
    }

    // 2. USE CASE: Login
    public Fan login(String u, String p) throws Exception {
        if (u == null || u.trim().isEmpty() || p == null || p.trim().isEmpty()) {
            throw new Exception("Ten dang nhap va mat khau khong duoc de trong!");
        }
        final String cleanU = u.trim();
        return fanRepository.findAll().stream()
                .filter(f -> f.getUsername().equalsIgnoreCase(cleanU) && f.getPassword().equals(p))
                .findFirst()
                .orElseThrow(() -> new Exception("Sai ten dang nhap hoac mat khau!"));
    }

    // 3. USE CASE: Search match
    public List<Match> searchMatches(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return matchRepository.findAll();
        }
        final String kw = keyword.trim().toLowerCase();
        return matchRepository.findAll().stream()
                .filter(m -> m.getMatchId().toLowerCase().contains(kw) ||
                             m.getMatchName().toLowerCase().contains(kw) ||
                             m.getHomeTeam().toLowerCase().contains(kw) ||
                             m.getAwayTeam().toLowerCase().contains(kw) ||
                             m.getStadiumId().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public List<Match> searchMatches(String date, String stadium, String team) {
        return filterMatches(date, stadium, null).stream()
                .filter(m -> team == null || team.trim().isEmpty() ||
                        m.getHomeTeam().toLowerCase().contains(team.trim().toLowerCase()) ||
                        m.getAwayTeam().toLowerCase().contains(team.trim().toLowerCase()) ||
                        m.getMatchName().toLowerCase().contains(team.trim().toLowerCase()))
                .collect(Collectors.toList());
    }

    // 4. USE CASE: Filter match (theo ngay, san van dong, tinh trang ve)
    public List<Match> filterMatches(String date, String stadium, String ticketStatus) {
        return matchRepository.findAll().stream()
                .filter(m -> date == null || date.trim().isEmpty() || m.getMatchDate().equalsIgnoreCase(date.trim()))
                .filter(m -> stadium == null || stadium.trim().isEmpty() || m.getStadiumId().equalsIgnoreCase(stadium.trim()))
                .filter(m -> {
                    if (ticketStatus == null || ticketStatus.trim().isEmpty() || ticketStatus.equalsIgnoreCase("all")) {
                        return true;
                    }
                    boolean soldOut = isMatchSoldOut(m.getMatchId());
                    if (ticketStatus.equalsIgnoreCase("con_ve") || ticketStatus.equalsIgnoreCase("available")) {
                        return !soldOut;
                    }
                    if (ticketStatus.equalsIgnoreCase("het_ve") || ticketStatus.equalsIgnoreCase("sold_out")) {
                        return soldOut;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    // 5. USE CASE: View match detail
    public Match getMatchById(String matchId) throws Exception {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new Exception("Khong tim thay tran dau voi ma: " + matchId));
    }

    public List<Section> getSectionsForMatch(String matchId) throws Exception {
        Match m = getMatchById(matchId);
        return sectionRepository.findByStadium(m.getStadiumId());
    }

    public List<Seat> getSeatsForSection(String matchId, String secId) {
        return seatRepository.findAll().stream()
                .filter(s -> s.getMatchId().equalsIgnoreCase(matchId) && s.getSectionId().equalsIgnoreCase(secId))
                .collect(Collectors.toList());
    }

    public boolean isMatchSoldOut(String matchId) {
        List<Seat> seats = seatRepository.findAll().stream()
                .filter(s -> s.getMatchId().equalsIgnoreCase(matchId))
                .collect(Collectors.toList());
        if (seats.isEmpty()) return false;
        return seats.stream().noneMatch(s -> s.getStatus() == SeatStatus.available || s.getStatus() == SeatStatus.AVAILABLE);
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Fan updateProfile(String fanId, String fullName, String email, String phone, String newPassword) throws Exception {
        Fan fan = fanRepository.findAll().stream()
                .filter(f -> f.getFanId().equalsIgnoreCase(fanId))
                .findFirst()
                .orElseThrow(() -> new Exception("Khong tim thay nguoi dung!"));

        if (fullName != null && !fullName.trim().isEmpty()) fan.setFullName(fullName.trim());
        if (email != null && !email.trim().isEmpty()) {
            String trimmedEmail = email.trim();
            if (!trimmedEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) throw new Exception("Email khong hop le: " + email);
            fan.setEmail(trimmedEmail);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            String cleanPhone = phone.replaceAll("\\s+", "").trim();
            if (!cleanPhone.matches("^0[0-9]{9}$")) throw new Exception("SDT khong hop le: " + phone);
            fan.setPhone(cleanPhone);
        }
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 6) throw new Exception("Mat khau moi phai tu 6 ky tu tro len!");
            fan.setPassword(newPassword);
        }

        boolean updated = fanRepository.update(fan);
        if (!updated) {
            throw new Exception("Loi: Khong the cap nhat thong tin nguoi dung vao file du lieu!");
        }
        return fan;
    }
}
