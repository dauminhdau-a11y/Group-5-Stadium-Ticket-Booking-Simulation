# Group-5-Stadium-Ticket-Booking-Simulation
# 🏟️ LAB211: Stadium Ticket Booking Simulation

**Nhóm thực hiện:** Nhóm 5

## 👥 Danh sách thành viên

| STT | Họ và tên | Mã số SV (MSSV) | Vai trò |
| :---: | :--- | :--- | :--- |
| 1 | Trần Minh | QE200074 | Nhóm trưởng |
| 2 | [Tên thành viên 2] | [Nhập MSSV] | Thành viên |
| 3 | [Tên thành viên 3] | [Nhập MSSV] | Thành viên |
| 4 | Thái Quang Huy | QS190001 | Thành viên |
## Danh sách chức năng hệ thống

**1. Chức năng dành cho người mua (Buyer)**
* Đăng ký tài khoản (có ràng buộc dữ liệu).
* Đăng nhập, đăng xuất hệ thống.
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
