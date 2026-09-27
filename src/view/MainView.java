package view;

import java.util.Scanner;

public class MainView {
    private Scanner scanner;
    private BookingView bookingView;

    public MainView(BookingView bookingView) {
        this.scanner = new Scanner(System.in);
        this.bookingView = bookingView;
    }
    public void displayMenu(){
        while(true){
            System.out.println("=====================================");
            System.out.println("    STADIUM TICKET BOOKING SYSTEM    ");
            System.out.println("=====================================");
            System.out.println("1. Xem danh sách ghế trống");
            System.out.println("2. Đặt vé trực tuyến");
            System.out.println("3. Chạy công cụ giả lập");
            System.out.println("0. Thoát chương trình");
            System.out.print("Chọn chức năng (0-3): ");
            
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    System.out.println("Chức năng đang phát triển...");
                    break;
                case 2:
                    bookingView.showBookingForm();
                    break;
                case 3:
                    System.out.println("Chức năng đang phát triển...");
                    break;
                case 0:
                    System.out.println("Cảm ơn bạn đã sử dụng hệ thống. Tạm biệt!");
                    System.exit(0);
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }
    public int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }


}
