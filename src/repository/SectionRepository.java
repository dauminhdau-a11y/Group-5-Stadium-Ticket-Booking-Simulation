package repository;

import model.Section;

public class SectionRepository extends CsvRepository<Section> {
    public SectionRepository() {
        this("data/sections.csv");
    }

    public SectionRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Section parseLine(String line) {
        return new Section().fromCsvLine(line);
    }

    @Override
    public boolean isHeaderLine(String line) {
        String header = line.trim();
        return "sectionId,stadiumId,name,stand,capacity,price".equalsIgnoreCase(header)
                || "sectionId,stadiumId,name,type,capacity,price".equalsIgnoreCase(header);
    }
}