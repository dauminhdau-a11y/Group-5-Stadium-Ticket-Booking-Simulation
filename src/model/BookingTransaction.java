package model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingTransaction extends BaseEntity {
    private String transactionId;
    private String fanId;
    private String matchId;
    private double totalAmount;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BookingTransaction() {
    }

    public BookingTransaction(String transactionId, String fanId, String matchId, double totalAmount, LocalDateTime bookingDate, BookingStatus status) {
        this.transactionId = transactionId;
        this.fanId = fanId;
        this.matchId = matchId;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.status = status;
    }
  
    public String getTransactionId() {
        return transactionId;
    }
    @Override 
    public String toCsvLine() {
        return transactionId + "," + fanId + "," + matchId + "," + totalAmount + "," + bookingDate.format(DATE_TIME_FORMATTER) + "," + status.name();
    }
    @Override 
    public void fromCsvLine(String csvLine){
        if(csvLine != null && !csvLine.trim().isEmpty()){
            String[] parts = csvLine.split(",");
            if(parts.length == 6){
                this.transactionId = parts[0].trim();
                this.fanId = parts[1].trim();
                this.matchId = parts[2].trim();
                this.totalAmount = Double.parseDouble(parts[3].trim());
                this.bookingDate = LocalDateTime.parse(parts[4].trim(), DATE_TIME_FORMATTER);
                this.status = BookingStatus.valueOf(parts[5].trim());
            }

        }
    }
    public String getFanId() {
        return fanId;
    }
    public String getId(){
        return transactionId;
    }
    public double getTotalAmount() {
            return totalAmount;
        }
    public BookingStatus getStatus() {
            return status;
        }
}
