package view;

import controller.BookingController;
import model.BookingTransaction;

import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class BookingView {
    private BookingController bookingController;
    private Scanner scanner;

    public BookingView(BookingController bookingController) {
        this.bookingController = bookingController;
        this.scanner = new Scanner(System.in);
    }

    public void showBookingForm(){
        System.out.println("\n=== HỆ THỐNG ĐẶT VÉ TRỰC TUYẾN ===");
        System.out.print("Nhập Fan ID của bạn (VD: F001): ");
        String fanId = scanner.nextLine().trim();

        System.out.print("Nhập mã trận đấu (VD: M001): ");
        String matchId = scanner.nextLine().trim();

        System.out.print("Nhập mã ghế muốn đặt (Cách nhau bởi dấu phẩy, VD: S1, S2): ");
        String seatInput = scanner.nextLine().trim();

        List<String> seatIds = Arrays.asList(seatInput.split("\\s*,\\s*"));

        try{
            System.out.println("Đang xử lý giao dịch...");
            BookingTransaction transaction = bookingController.bookSeats(fanId, matchId, seatIds);

            printBookingResult(transaction);
        }catch (Exception e){
            System.out.println("LỖI ĐẶT VÉ: " + e.getMessage());
        }
    }
    public void printBookingResult(BookingTransaction transaction){
        System.out.println("\n=== BIÊN LAI GIAO DỊCH THÀNH CÔNG ===");
        System.out.println("Mã giao dịch: " + transaction.getTransactionId());
        System.out.println("Fan ID: " + transaction.getFanId());
        System.out.println("Tổng số tiền: " + String.format("%.0f", transaction.getTotalAmount()) + " VND");
        System.out.println("Trạng thái: " + transaction.getStatus());
        System.out.println("--------------------------------\n");
    }
}
