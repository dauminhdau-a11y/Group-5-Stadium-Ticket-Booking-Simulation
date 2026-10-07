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

**1. Chức năng dành cho Guest**
* Đăng ký tài khoản (có ràng buộc dữ liệu).
* Đăng nhập hệ thống.
* Xem danh sách trận đấu; hỗ trợ lọc theo ngày, sân vận động, đội bóng (hiển thị trạng thái "Hết vé" đối với các trận đã sold out).

**1. Chức năng dành cho Fan**
* Đăng xuất hệ thống.
* Xem danh sách trận đấu; hỗ trợ lọc theo ngày, sân vận động, đội bóng (hiển thị trạng thái "Hết vé" đối với các trận đã sold out).
* Xem danh sách khán đài kèm bảng giá vé (tích hợp sơ đồ đánh dấu trạng thái ghế đã đặt/chưa đặt).
* Đặt mua vé: 
  * Hỗ trợ giá vé khác nhau theo từng hạng ghế.
  * Giới hạn giao dịch: Tối đa 4 vé/lần.
  * Thanh toán qua mã QR (thông tin thanh toán sẽ được gửi về Seller, có hỗ trợ vé offline).
* Trả lại vé (Điều kiện: Phải thực hiện trước trận đấu ít nhất 12 tiếng).
* Nhận thông báo khi trận đấu sắp diễn ra.
* Áp dụng voucher giảm giá (Giới hạn sử dụng 1 lần/voucher).

**2. Chức năng dành cho Staff**
* Xem danh sách khách hàng đã mua vé.
* Xác nhận trạng thái mua vé và xử lý yêu cầu trả vé.
* Xuất hóa đơn giao dịch.
* Xem ca làm việc
* Bán vé tại quầy

**3. Chức năng dành cho Admin**
* Quản lý tài khoản, vé, chỗ ngồi, trận đấu, sân vận động (Tạo mới, sửa thông tin, xóa).
* Quản lý doanh thu
* Chạy giả lập
