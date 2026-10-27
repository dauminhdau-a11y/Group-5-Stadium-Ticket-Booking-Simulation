# Group-5-Stadium-Ticket-Booking-Simulation
# 🏟️ LAB211: Stadium Ticket Booking Simulation

**Nhóm thực hiện:** Nhóm 5

## 👥 Danh sách thành viên

| STT | Họ và tên | Mã số SV (MSSV) | Vai trò |
| :---: | :--- | :--- | :--- |
| 1 | Trần Minh | QE200074 | Nhóm trưởng |
| 2 | Nguyễn Anh Khoa |QE200060 | Thành viên |
| 3 | Lê Đỗ Anh Khoa | QE210269 | Thành viên |
| 4 | Thái Quang Huy | QS190001 | Thành viên |
| 5 | Nguyễn Tấn Đạt | QE200091 | Thành viên |
## Danh sách chức năng hệ thống

### Đăng nhập theo vai trò

Màn hình đăng nhập hỗ trợ ba luồng riêng: Fan dùng `data/fans.csv`, Staff dùng
`data/staff.csv` và Admin dùng `data/admins.csv`. Mỗi tài khoản chỉ đăng nhập
được qua đúng luồng tương ứng với role trong dữ liệu. Tài khoản mẫu (mật khẩu
`demo123`):

| Role | Username |
| :--- | :--- |
| FAN | `fan01` |
| STAFF | `staff01` |
| ADMIN | `admin01` |

CSV Staff/Admin dùng các cột
`id,accountId,username,passwordHash,email,role,locked`.

**1. Chức năng dành cho khách (Guest)**
* Đăng ký tài khoản (có ràng buộc dữ liệu).
* Đăng nhập hệ thống.
* Xem và chỉnh sửa thông tin cá nhân (Profile).
* Xem danh sách trận đấu; hỗ trợ lọc theo ngày, sân vận động, đội bóng (hiển thị trạng thái "Hết vé" đối với các trận đã sold out).
* Xem danh sách khán đài kèm bảng giá vé (tích hợp sơ đồ đánh dấu trạng thái ghế đã đặt/chưa đặt).

**1. Chức năng dành cho người mua (Buyer)**
* Đăng xuất hệ thống.
* Xem và chỉnh sửa thông tin cá nhân (Profile).
* Xem danh sách trận đấu; hỗ trợ lọc theo ngày, sân vận động, đội bóng (hiển thị trạng thái "Hết vé" đối với các trận đã sold out).
* Xem danh sách khán đài kèm bảng giá vé (tích hợp sơ đồ đánh dấu trạng thái ghế đã đặt/chưa đặt).
* Đặt mua vé: 
  * Hỗ trợ giá vé khác nhau theo từng hạng ghế.
  * Giới hạn giao dịch: Tối đa 4 vé/lần.
  * Thanh toán qua mã QR (thông tin thanh toán sẽ được gửi về Seller, có hỗ trợ vé offline).
* Trả lại vé (Điều kiện: Phải thực hiện trước trận đấu ít nhất 12 tiếng).
* Nhận thông báo khi trận đấu sắp diễn ra.
* Áp dụng voucher giảm giá (Giới hạn sử dụng 1 lần/voucher).
* Thêm trận đấu vào danh sách yêu thích (Wishlist).
* Đánh giá trận đấu/dịch vụ.

**2. Chức năng dành cho người bán (Seller)**
* Xem danh sách khách hàng đã mua vé.
* Xác nhận trạng thái mua vé và xử lý yêu cầu trả vé.
* Xuất hóa đơn giao dịch.

**3. Chức năng dành cho Admin**
* Quản lý trận đấu, sân vận động (Tạo mới, sửa thông tin, xóa).
* Quản lý doanh thu
* Sửa lỗi thủ công




# Module Bán vé tại quầy (Offline Selling Flow)

Bộ code Java thuần, không phụ thuộc thư viện ngoài, mô phỏng độc lập luồng bán vé tại quầy
theo kiến trúc: SellerView -> SellerController -> Repositories -> Entities.

## Cách chạy

```bash
javac seller/*.java -d out
java -Dfile.encoding=UTF-8 -cp out seller.SellerDemo
```

Nếu tiếng Việt hiển thị lỗi font trên terminal Windows, chạy thêm:
```bash
chcp 65001
```
trước khi chạy lệnh java ở trên.

## Chạy giao diện quầy tương tác (nhập tay)

Sửa `seller.SellerDemo` hoặc tạo file main mới gọi:
```java
SellerView view = new SellerView(controller);
view.showMenu();
```

## Các điểm đã bổ sung so với Class Diagram gốc (đã thống nhất ở phần phân tích trước)

1. `Ticket.transactionId` + `BookingTransaction.ticketIds`
   -> để `confirmRefund()` tra đúng các vé thuộc 1 giao dịch (diagram gốc không có liên kết này).
2. `Seat.matchId`
   -> để 1 ghế có trạng thái độc lập theo từng trận, tránh việc ghế "dính BOOKED" mãi mãi qua các trận khác nhau.
3. `SellerController` dùng `synchronized` trên toàn bộ thao tác kiểm tra + cập nhật ghế
   -> mô phỏng cơ chế `SYNCHRONIZED` trong enum `SyncMechanism`, đảm bảo 2 quầy bán cùng lúc
      không thể cùng bán trùng 1 ghế (xem "MO PHONG 4" trong SellerDemo).

## Danh sách file

- `BaseEntity.java` — lớp trừu tượng gốc (đúng diagram)
- `SeatStatus.java`, `TicketStatus.java`, `BookingStatus.java` — enum trạng thái (đúng diagram)
- `Section.java`, `Seat.java`, `Ticket.java`, `BookingTransaction.java` — Entity
- `CsvRepository.java`, `InMemoryCsvRepository.java` — interface + lớp nền Repository
- `SeatRepository.java`, `TicketRepository.java`, `TransactionRepository.java` — Repository cụ thể
- `SellerController.java` — nghiệp vụ bán vé tại quầy + hoàn tiền
- `SellerView.java` — giao diện console tương tác thật
- `SellerDemo.java` — **main class chạy mô phỏng tự động**, seed dữ liệu mẫu và chạy 4 kịch bản demo
