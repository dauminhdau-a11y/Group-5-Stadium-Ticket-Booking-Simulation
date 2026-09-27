package repository;

import model.BookingTransaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class TransactionRepository implements CsvRepository<BookingTransaction> {
    private static final String FILE_PATH = "data/transactions.csv";

    @Override 
    public List<BookingTransaction> findAll(){
        List<BookingTransaction> transactions = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))){
            String line;
            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()){
                    continue;
                }
                    BookingTransaction trans = new BookingTransaction();
                    trans.fromCsvLine(line);
                    transactions.add(trans);

            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file giao dịch: " + e.getMessage());
        }
        return transactions;
    }
    @Override 
    public void save(BookingTransaction entity){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))){
            bw.write(entity.toCsvLine());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Lỗi ghi file giao dịch: " + e.getMessage());
        }
    }
    @Override 
    public BookingTransaction findById(String id){
        List<BookingTransaction> all = findAll();
        for(BookingTransaction trans : all){
            if(trans.getTransactionId().equals(id)){
                return trans;
            }
        }
        return null;
    }
    @Override 
    public List<BookingTransaction> findByCondition(Predicate<BookingTransaction> predicate){
        List<BookingTransaction> all = findAll();
        return all.stream().filter(predicate).collect(Collectors.toList());
    }
    @Override 
    public void delete(String id){

        List<BookingTransaction> all = findAll();
        all.removeIf(trans -> trans.getTransactionId().equals(id));

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))){
            for(BookingTransaction trans : all){
                bw.write(trans.toCsvLine());
                bw.newLine();
            }
        }catch(IOException e){
            System.out.println("Lỗi cập nhật file khi xóa giao dịch: " + e.getMessage());
        }
    }
    public List<BookingTransaction> findByFanId(String fanId){
        return findByCondition(trans -> trans.getFanId().equals(fanId));
    }

}
