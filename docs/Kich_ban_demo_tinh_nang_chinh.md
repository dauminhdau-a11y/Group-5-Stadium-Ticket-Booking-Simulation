# 🏟️ HƯỚNG DẪN THỰC THI & DEMO CÁC TÍNH NĂNG CỐT LÕI
## HỆ THỐNG ĐẶT VÉ SÂN VẬN ĐỘNG (LAB211 - NHÓM 5)
> **Trọng tâm tài liệu:** Hướng dẫn chi tiết **Cách bấm phím / Chạy thế nào** và **Tác dụng thực tế của tính năng**, đi thẳng vào tài khoản mẫu (không cần đăng ký mới).

---

### 🔑 Thông tin tài khoản mẫu có sẵn (Mật khẩu chung: `demo123`):
* **Cổ động viên (Fan):** `fan01`
* **Nhân viên (Staff):** `staff01`
* **Quản trị viên (Admin):** `admin01`

---

## 1. PHẦN CỔ ĐỘNG VIÊN (FAN) - Đăng nhập: `fan01` / `demo123`

### Bước 1.1: Đăng nhập thẳng tài khoản Fan
* **👉 Cách chạy:**
  1. Khởi động bằng file `run.bat` (CMD) hoặc `.\run.ps1` (PowerShell).
  2. Chọn `2` (Login) $\rightarrow$ Username: `fan01` $\rightarrow$ Password: `demo123`.
* **🎯 Tác dụng & Ý nghĩa:**
  * Vào thẳng giao diện đặt vé của Fan mà không cần mất thời gian đăng ký.
  * Hệ thống tự động xác thực từ `data/fans.csv` và kiểm tra cờ khóa tài khoản (`locked`).

---

### Bước 1.2: Xem sơ đồ 4 khán đài trực quan theo thời gian thực (Seat Map)
* **👉 Cách chạy:**
  1. Tại Fan Menu, chọn `6` (View seat map).
  2. Nhập Match ID: `M001`.
  3. Chọn `n` (để xem sơ đồ tổng quan) hoặc `y` (để xem chi tiết toàn bộ các ghế).
* **🎯 Tác dụng & Ý nghĩa:**
  * Hiển thị trực quan 4 khán đài: **Stand A (Nam)**, **Stand B (Bắc)**, **Stand C (Tây)**, **Stand D (Đông)**.
  * Phân biệt rõ trạng thái từng ghế: `[O]` Còn trống, `[X]` Đã bán, `[-]` Bị khóa.
  * Tự động thống kê số ghế khả dụng và tỷ lệ phần trăm lấp đầy khán đài theo thời gian thực.

---

### Bước 1.3: Đặt vé, Chống gom vé (> 4 vé), Áp Voucher & Thanh toán QR
* **👉 Cách chạy:**
  1. Chọn `7` (Book tickets) $\rightarrow$ Chọn trận `M001` $\rightarrow$ Chọn khán đài `Stand A`.
  2. **Thử nhập 5 ghế:** Hệ thống chặn lại và báo lỗi không cho đặt quá 4 vé.
  3. **Nhập lại 2 ghế hợp lệ** (chọn 2 ghế đang trống).
  4. **Nhập mã voucher:** `GIAM20` (hoặc `DISCOUNT10`).
  5. **Chọn phương thức thanh toán:** Chọn `1` (QR_CODE).
* **🎯 Tác dụng & Ý nghĩa:**
  * **Chống đầu cơ:** Thiết lập giới hạn cứng tối đa 4 vé/lần đặt để ngăn chặn hành vi đầu cơ gom vé chợ đen.
  * **Khuyến mãi:** Tự động kiểm tra điều kiện đơn hàng tối thiểu (`minimumOrderAmount`) và trừ tiền trực tiếp vào hóa đơn.
  * **Thanh toán:** Tự động sinh mã QR chuyển khoản và tạo giao dịch thanh toán.

---

### Bước 1.4: Lịch sử đặt vé căn lề động 100% & Sắp xếp đa năng (`sort`)
* **👉 Cách chạy:**
  1. Chọn `9` (View booking history): Quan sát bảng lịch sử **căn lề động 100% thẳng hàng**, không bao giờ bị xô lệch cột.
  2. Gõ lệnh **`sort`** $\rightarrow$ Chọn `3` (Highest amount first) hoặc `5` (Priority status).
  3. Gõ số thứ tự `1` (hoặc mã `TXN-...`) để xem chi tiết giao dịch.
* **🎯 Tác dụng & Ý nghĩa:**
  * Bảng dữ liệu tự động co giãn theo độ dài của danh sách ghế (`maxSeatsLen`), giải quyết triệt để lỗi tràn dòng/lệch cột.
  * Lệnh `sort` giúp người dùng lọc và xem lịch sử linh hoạt: xem theo số tiền chi trả, theo thời gian hoặc gom nhóm các đơn chờ duyệt (`PENDING`) lên đầu.
  * Xem chi tiết từng vé đơn lẻ và voucher đã áp dụng.

---

### Bước 1.5: Chính sách hoàn vé trước giờ thi đấu 12 tiếng
* **👉 Cách chạy:**
  1. Chọn `11` (Refund ticket).
  2. Nhập mã giao dịch vừa đặt (lấy mã `TXN-...` trong bảng lịch sử).
* **🎯 Tác dụng & Ý nghĩa:**
  * Kiểm soát thời gian hoàn vé: Chỉ chấp nhận nếu yêu cầu được gửi trước giờ bóng lăn tối thiểu 12 tiếng.
  * Sau khi hoàn tiền thành công, hệ thống tự động giải phóng toàn bộ các ghế liên quan về trạng thái `AVAILABLE` để người khác có thể mua tiếp.

---

## 2. PHẦN NHÂN VIÊN (STAFF) - Đăng nhập: `staff01` / `demo123`

### Bước 2.1: Bán vé quầy Offline & Tự động xuất lưu hóa đơn
* **👉 Cách chạy:**
  1. Đăng xuất Fan (chọn `13`) $\rightarrow$ Chọn Login $\rightarrow$ Nhập `staff01` / `demo123`.
  2. Chọn `3` (Sell tickets offline) $\rightarrow$ Nhập trận `M001` $\rightarrow$ Nhập ghế trống $\rightarrow$ Xác nhận tiền mặt.
* **🎯 Tác dụng & Ý nghĩa:**
  * Bán vé trực tiếp cho khách vãng lai tại quầy.
  * Hệ thống phát hành vé ngay lập tức, in hóa đơn chi tiết lên màn hình và tự động lưu tệp văn bản vào thư mục `data/invoices/` làm bằng chứng đối soát tài chính.

---

### Bước 2.2: Duyệt thanh toán đặt chỗ hàng loạt bằng lệnh `'all'`
* **👉 Cách chạy:**
  1. Chọn `4` (Confirm reservation payment).
  2. Danh sách các đơn chờ duyệt (`PENDING`) hiện ra.
  3. Gõ chữ **`all`** rồi nhấn Enter.
* **🎯 Tác dụng & Ý nghĩa:**
  * Giải quyết tình trạng ùn tắc đơn: Thay vì phải gõ duyệt từng mã giao dịch thủ công, nhân viên chỉ cần gõ `all` để xác nhận toàn bộ các đơn đang chờ sang trạng thái `CONFIRMED` chỉ trong 1 thao tác.

---

### Bước 2.3: Báo cáo kết ca làm việc (Shift report)
* **👉 Cách chạy:**
  1. Chọn `8` (Shift report).
* **🎯 Tác dụng & Ý nghĩa:**
  * Thống kê tổng số lượng vé bán ra trong ca trực, tổng doanh thu thực thu và số tiền hoàn vé để phục vụ việc bàn giao ca chính xác.

---

## 3. PHẦN QUẢN TRỊ VIÊN (ADMIN) - Đăng nhập: `admin01` / `demo123`

### Bước 3.1: Tạo Sân vận động - Tự động khởi tạo ngay 4 Khán đài tiêu chuẩn
* **👉 Cách chạy:**
  1. Đăng xuất Staff (chọn `9`) $\rightarrow$ Chọn Login $\rightarrow$ Nhập `admin01` / `demo123`.
  2. Chọn `3` (Manage stadium) $\rightarrow$ Chọn `2` (Create stadium).
  3. Nhập tên sân mới (VD: `My Dinh National Stadium`), sức chứa: `40000`.
* **🎯 Tác dụng & Ý nghĩa:**
  * Tự động hóa cấu hình: Hệ thống tự động khởi tạo ngay 4 khán đài tiêu chuẩn:
    * Stand A (Nam): Giá 500k
    * Stand B (Bắc): Giá 400k
    * Stand C (Tây): Giá 300k
    * Stand D (Đông): Giá 200k
  * Sức chứa tự động chia đều, khán đài được liên kết sẵn vào hệ thống bán vé mà không cần tạo thủ công từng khán đài.

---

### Bước 3.2: Thao tác hàng loạt (Batch Processing) cho Ghế và Vé
* **👉 Cách chạy:**
  1. Chọn `5` (Manage seat) $\rightarrow$ Chọn `3` (Batch create seats) $\rightarrow$ Chọn Section $\rightarrow$ Nhập số lượng (VD: `50` ghế).
  2. Chọn `6` (Manage ticket) $\rightarrow$ Chọn `3` (Batch generate tickets) $\rightarrow$ Quét phát hành vé tự động cho phân khu.
* **🎯 Tác dụng & Ý nghĩa:**
  * Admin không cần mất công tạo từng ghế đơn lẻ. Hệ thống tự động sinh số ghế, chia hàng (`Row A, Row B...`) và tự động quét toàn bộ ghế khả dụng để tạo vé đồng loạt.

---

### Bước 3.3: Mô phỏng Đa luồng (Simulation) & Xử lý tranh chấp ghế (Race Condition)
* **👉 Cách chạy:**
  1. Chọn `8` (Simulation & Data).
  2. Chọn cơ chế khóa **`NO_LOCK`** $\rightarrow$ Chạy thử mô phỏng 10 luồng đặt vé đồng thời.
  3. Chạy lại với cơ chế **`SYNCHRONIZED`** hoặc **`OPTIMISTIC`**.
  4. Quan sát bảng so sánh kết quả đo lường hiệu năng.
* **🎯 Tác dụng & Ý nghĩa:**
  * **Trọng tâm đề tài LAB211:**
    * Khi chạy `NO_LOCK`: Chỉ ra hiện tượng xung đột ghế (Seat conflicts) và bán trùng vé khi nhiều luồng cùng tranh 1 ghế.
    * Khi chạy `SYNCHRONIZED` hoặc `OPTIMISTIC`: Chứng minh hệ thống khóa đồng bộ an toàn 100%, bảo vệ toàn vẹn dữ liệu.
    * Bảng thống kê hiển thị chi tiết thời gian thực thi (mili-giây), tỷ lệ thành công và số lượng xung đột ghế.
