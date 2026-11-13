package controller;

import model.Section;
import model.Stadium;
import model.enums.Stand;
import repository.SectionRepository;
import repository.StadiumRepository;
import java.io.IOException;
import java.util.List;

public class StadiumController {
    private StadiumRepository stadiumRepository;
    private SectionRepository sectionRepository;

    public StadiumController() {
        this(new StadiumRepository("data/stadiums.csv"), new SectionRepository("data/sections.csv"));
    }

    public StadiumController(StadiumRepository stadiumRepository) {
        this(stadiumRepository, new SectionRepository("data/sections.csv"));
    }

    public StadiumController(StadiumRepository stadiumRepository, SectionRepository sectionRepository) {
        this.stadiumRepository = stadiumRepository;
        this.sectionRepository = sectionRepository;
    }

    public StadiumRepository getStadiumRepository() {
        return stadiumRepository;
    }

    public void setStadiumRepository(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    }

    public SectionRepository getSectionRepository() {
        return sectionRepository;
    }

    public void setSectionRepository(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Stadium> getAllStadiums() throws IOException {
        return stadiumRepository.findAll();
    }

    public Stadium getStadiumById(String id) throws IOException {
        return stadiumRepository.findById(id);
    }

    public boolean createStadium(Stadium stadium) throws IOException {
        if (stadium == null || stadium.getStadiumId() == null || stadium.getStadiumId().trim().isEmpty()) {
            return false;
        }
        if (stadiumRepository.findById(stadium.getStadiumId().trim()) != null) {
            return false;
        }
        stadiumRepository.save(stadium);

        // Auto-provision 4 stands (A, B, C, D) for this stadium
        if (sectionRepository != null) {
            String stdId = stadium.getStadiumId().trim();
            int quarterCap = Math.max(1, stadium.getCapacity() / 4);

            Section secA = new Section("SEC_" + stdId + "_A", stdId, "Stand A (South)", Stand.A, quarterCap, 500000.0);
            Section secB = new Section("SEC_" + stdId + "_B", stdId, "Stand B (North)", Stand.B, quarterCap, 400000.0);
            Section secC = new Section("SEC_" + stdId + "_C", stdId, "Stand C (West)", Stand.C, quarterCap, 300000.0);
            Section secD = new Section("SEC_" + stdId + "_D", stdId, "Stand D (East)", Stand.D, quarterCap, 200000.0);

            sectionRepository.save(secA);
            sectionRepository.save(secB);
            sectionRepository.save(secC);
            sectionRepository.save(secD);
        }
        return true;
    }

    public boolean updateStadium(String stadiumId, String name, String location, int capacity) throws IOException {
        Stadium stadium = stadiumRepository.findById(stadiumId);
        if (stadium == null) {
            return false;
        }
        if (name != null && !name.trim().isEmpty()) {
            stadium.setName(name.trim());
        }
        if (location != null && !location.trim().isEmpty()) {
            stadium.setLocation(location.trim());
        }
        if (capacity > 0) {
            stadium.setCapacity(capacity);
        }
        stadiumRepository.save(stadium);
        return true;
    }

    public boolean removeStadium(String stadiumId) throws IOException {
        if (stadiumId == null || stadiumId.trim().isEmpty()) {
            return false;
        }
        String idTrim = stadiumId.trim();
        // Remove associated sections
        if (sectionRepository != null) {
            List<Section> sections = sectionRepository.findByCondition(s -> s.getStadiumId() != null && s.getStadiumId().equalsIgnoreCase(idTrim));
            for (Section sec : sections) {
                sectionRepository.deleteById(sec.getSectionId());
            }
        }
        return stadiumRepository.deleteById(idTrim);
    }
}
