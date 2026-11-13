# 🏟️ Stadium Ticket Booking Simulation System

> **Dự án LAB211: Hệ thống Đặt vé và Mô phỏng Quản lý Sân vận động**  
> **Nhóm thực hiện:** Nhóm 5  
> **Ngôn ngữ & Nền tảng:** Java (JDK 17+) | Pure Console Application | Kiến trúc MVC

---

## 👥 Danh sách thành viên

| STT | Họ và tên       | Mã số SV (MSSV) | Vai trò     |
| :-: | :-------------- | :-------------: | :---------- |
|  1  | Trần Minh       |    QE200074     | Nhóm trưởng |
|  2  | Nguyễn Anh Khoa |    QE200060     | Thành viên  |
|  3  | Lê Đỗ Anh Khoa  |    QE210269     | Thành viên  |
|  4  | Thái Quang Huy  |    QS190001     | Thành viên  |
|  5  | Nguyễn Tấn Đạt  |    QE200091     | Thành viên  |

---

## 📌 Tổng quan kiến trúc hệ thống

Dự án được xây dựng theo mô hình **MVC (Model - View - Controller - Repository)** với các nguyên tắc hướng đối tượng (OOP) vững chắc:

- **Model**: Thực thể dữ liệu kế thừa `BaseEntity` (`Account`, `Fan`, `Staff`, `Admin`, `Match`, `Stadium`, `Section`, `Seat`, `Ticket`, `Transaction`, `Voucher`, `Notification`, `SupportRequest`).
- **Repository**: Tầng truy xuất dữ liệu dạng bảng generic `CsvRepository<T>` lưu trữ độc lập tại thư mục `data/*.csv`.
- **Controller**: Điều phối logic nghiệp vụ, quản lý luồng dữ liệu, xử lý concurrency / race condition và kiểm soát tính toàn vẹn dữ liệu.
- **View**: Giao diện dòng lệnh (CLI) thân thiện, trực quan, 100% tiếng Anh chuẩn hóa, bảng biểu căn lề thẳng hàng động và có cơ chế bắt lỗi an toàn (`try-catch`), chống crash ứng dụng khi người dùng nhập sai.

---

## 🔐 Cơ chế Đăng nhập & Tài khoản mẫu

Hệ thống phân quyền nghiêm ngặt theo 3 vai trò độc lập với cơ chế mã hóa mật khẩu và cờ khóa tài khoản (`locked`):

- **Fan**: Đăng nhập bằng dữ liệu từ `data/fans.csv`.
- **Staff**: Đăng nhập bằng dữ liệu từ `data/staff.csv`.
- **Admin**: Đăng nhập bằng dữ liệu từ `data/admins.csv`.

### Tài khoản trải nghiệm mặc định (Mật khẩu: `demo123`):

| Vai trò (Role) | Tên đăng nhập (Username) | Mật khẩu (Password) | Quyền hạn chính                                                                   |
| :------------- | :----------------------- | :------------------ | :-------------------------------------------------------------------------------- |
| **FAN**        | `fan01`                  | `demo123`           | Đặt vé, xem sơ đồ ghế, lịch sử đặt vé, hoàn vé, áp mã giảm giá                    |
| **STAFF**      | `staff01`                | `demo123`           | Bán vé tại quầy, duyệt thanh toán, duyệt hoàn vé, xem sơ đồ ghế, xuất hóa đơn     |
| **ADMIN**      | `admin01`                | `demo123`           | Toàn quyền quản trị tài khoản, sân, trận đấu, ghế/vé hàng loạt, mô phỏng đa luồng |

---

## 🎯 Chi tiết các chức năng hệ thống theo yêu cầu

### 1. Dành cho Khách vãng lai & Màn hình xác thực (AuthView)

- **Đăng ký tài khoản (Register):** Đăng ký tài khoản Fan mới với kiểm tra định dạng dữ liệu chặt chẽ (username không trùng, email hợp lệ, số điện thoại, mật khẩu).
- **Đăng nhập hệ thống (Login):** Phân luồng đăng nhập chính xác theo vai trò; tự động từ chối tài khoản bị khóa (`locked`).
- **Tìm kiếm trận đấu (Search match):** Tìm kiếm trận đấu theo từ khóa (tên đội bóng, tên sân, mã trận đấu).
- **Lọc trận đấu (Filter match):** Lọc theo ngày, sân vận động, đội bóng (tự động đánh dấu trận đã "Hết vé" khi sold out).

---

### 2. Dành cho Cổ động viên (Fan - `FanView`)

- **1. Xem thông tin cá nhân (View profile):** Tra cứu thông tin tài khoản, họ tên, email, số điện thoại.
- **2. Chỉnh sửa hồ sơ (Update profile):** Cập nhật số điện thoại, đổi mật khẩu bảo mật.
- **3. Xem sơ đồ khán đài trực quan (View seat map):**
  - Hiển thị trực quan tình trạng ghế theo 4 khán đài: **Stand A (South)**, **Stand B (North)**, **Stand C (West)**, **Stand D (East)**.
  - Phân biệt rõ ràng trạng thái: `[O]` Ghế trống (Available), `[X]` Ghế đã đặt (Booked), `[-]` Ghế bị khóa (Locked).
  - Thống kê tỷ lệ lấp đầy ghế theo thời gian thực.
- **4. Đặt vé trận đấu (Book tickets):**
  - Hỗ trợ chọn trận đấu, chọn phân khu/khán đài và danh sách ghế mong muốn.
  - Giới hạn an toàn: Tối đa **4 vé/lần đặt** (chống đầu cơ gom vé).
  - **Áp dụng Voucher giảm giá:** Tự động kiểm tra điều kiện đơn hàng tối thiểu (`minimumOrderAmount`), tính số tiền được giảm và trừ trực tiếp vào tổng tiền thanh toán.
  - **Phương thức thanh toán:** Hỗ trợ quét mã QR ngân hàng (`QR_CODE`) hiển thị hướng dẫn chuyển khoản, hoặc thanh toán tiền mặt/thẻ (`CASH`, `BANK_TRANSFER`).
  - **Cơ chế Hybrid thanh toán:** Hỗ trợ hoàn tất ngay (`CONFIRMED`) hoặc tạo giao dịch chờ duyệt (`PENDING`) để nhân viên xác nhận.
- **5. Kiểm tra voucher (Check / Verify voucher):** Tra cứu mã voucher, phần trăm giảm giá và điều kiện áp dụng.
- **6. Lịch sử đặt vé nâng cao (View booking history):**
  - Bảng hiển thị **căn lề động 100% thẳng hàng**, tự động co giãn theo số lượng ghế:
    `# | Transaction ID | Booking Date | Match | Seats | Paid Amount | Method | Status`
  - **Bộ lọc sắp xếp đa năng (`sort`):**
    1. Đặt vé mới nhất (Mặc định)
    2. Đặt vé cũ nhất
    3. Số tiền thanh toán cao nhất
    4. Số tiền thanh toán thấp nhất
    5. Thứ tự ưu tiên trạng thái (`PENDING` $\rightarrow$ `CONFIRMED` $\rightarrow$ `REFUNDED`)
  - Bảng tổng kết thống kê: Tổng số giao dịch, tổng tiền đã chi trả thành công và số lượng chi tiết theo từng trạng thái.
  - Tra cứu chi tiết từng giao dịch bằng số thứ tự `#` hoặc mã `Transaction ID`.
- **7. Trả lại vé / Hoàn tiền (Refund ticket):**
  - Ràng buộc nghiệp vụ: Chỉ cho phép yêu cầu hoàn vé **trước giờ thi đấu ít nhất 12 tiếng**.
  - Sau khi hoàn tiền thành công, hệ thống tự động giải phóng toàn bộ ghế liên quan về trạng thái khả dụng (`AVAILABLE`).
- **8. Hộp thư thông báo (Receive notifications):** Nhận thông báo xác nhận đặt vé, hoàn vé, lịch thi đấu; tự động đánh dấu đã đọc.
- **9. Hỗ trợ khách hàng (Support contact):** Gửi yêu cầu trợ giúp (`REQ-xxxx`), theo dõi trạng thái phản hồi từ nhân viên.
- **10. Đăng xuất (Logout).**

---

### 3. Dành cho Nhân viên (Staff - `StaffView`)

- **1. Danh sách khách mua vé (View ticket fan list):** Tra cứu danh sách Fan đã mua vé theo từng trận đấu kèm phân trang.
- **2. Bảng giá vé (Ticket fee list):** Xem bảng giá vé niêm yết theo từng khán đài của trận đấu.
- **3. Bán vé trực tiếp tại quầy (Sell tickets offline):** Nhập thông tin khách hàng vãng lai, chọn ghế trống, xác nhận thanh toán tiền mặt/thẻ, tự động phát hành vé và xuất hóa đơn tại chỗ.
- **4. Xác nhận thanh toán đặt chỗ (Confirm reservation payment):**
  - Hiển thị danh sách tất cả giao dịch đang chờ (`PENDING`) với bảng kẻ căn lề chuẩn xác.
  - Hỗ trợ xác nhận từng giao dịch theo `Transaction ID`, theo số thứ tự `#`, hoặc nhập **`all`** để duyệt thanh toán đồng loạt toàn bộ giao dịch đang chờ.
- **5. Sơ đồ khán đài thời gian thực (View seat map):** Giám sát trạng thái lấp đầy từng khán đài của trận đấu.
- **6. Xử lý yêu cầu hỗ trợ (Support contact):** Xem danh sách câu hỏi của Fan và gửi câu trả lời phản hồi chính thức.
- **7. Xuất và lưu hóa đơn (Shift report):** In chi tiết hóa đơn thanh toán lên màn hình và tự động lưu file văn bản vào thư mục `data/invoices/`.
- **8. Báo cáo ca làm việc & Lịch trực (Shift report & schedule):** Thống kê số lượng vé bán trong ca, tổng doanh thu ca trực, số tiền hoàn vé kèm thông tin nhân viên trực.
- **9. Đăng xuất (Logout).**

---

### 4. Dành cho Quản trị viên (Admin - `AdminView`)

- **1. Quản lý tài khoản (Manage Account):**
  - Xem danh sách toàn bộ tài khoản Staff và Fan trong hệ thống.
  - Thêm mới tài khoản Staff/Fan.
  - Tìm kiếm tài khoản theo username hoặc email.
  - Khóa (`Lock`) hoặc Mở khóa (`Unlock`) tài khoản người dùng ngay lập tức.
  - Xóa tài khoản khỏi hệ thống.
- **2. Quản lý trận đấu (Manage Match):**
  - Xem danh sách tất cả các trận đấu hiện có.
  - Thêm mới trận đấu (chọn sân vận động, thời gian thi đấu, đội nhà, đội khách, giá vé cơ bản).
  - Cập nhật thông tin chi tiết và dời lịch thi đấu (Reschedule).
  - Tìm kiếm và xóa trận đấu.
- **3. Quản lý sân vận động (Manage Stadium):**
  - Xem danh sách tất cả các sân vận động.
  - **Tạo sân vận động tự động có 4 khán đài:** Khi thêm mới một sân vận động, hệ thống **tự động khởi tạo ngay 4 khán đài tiêu chuẩn**:
    - **Khán đài A (Stand A - South)**: Đơn giá tiêu chuẩn `500,000 VND`
    - **Khán đài B (Stand B - North)**: Đơn giá tiêu chuẩn `300,000 VND`
    - **Khán đài C (Stand C - West)**: Đơn giá tiêu chuẩn `150,000 VND`
    - **Khán đài D (Stand D - East)**: Đơn giá tiêu chuẩn `80,000 VND`
    - Sức chứa tự động chia đều theo tổng sức chứa của sân vận động.
  - Cập nhật thông tin sân và xóa sân vận động (tự động dọn dẹp các phân khu khán đài liên quan).
- **4. Quản lý phân khu khán đài (Manage Section):**
  - Xem danh sách phân khu (xem toàn bộ hoặc lọc theo sân).
  - Thêm mới phân khu, chỉnh sửa tên/sức chứa/giá vé, xóa phân khu.
- **5. Quản lý chỗ ngồi (Manage Seat) - Hỗ trợ thao tác hàng loạt:**
  - Xem danh sách ghế theo trận đấu (phân trang 50 ghế/trang, hỗ trợ xem tất cả `all`).
  - Thêm ghế đơn lẻ.
  - **Thêm hàng loạt ghế theo Section (Batch create seats):** Nhập số lượng ghế mong muốn, hệ thống tự động sinh mã ghế tuần tự và phân hàng ghế (`Row A, B, C...`).
  - Cập nhật trạng thái 1 ghế.
  - **Cập nhật trạng thái hàng loạt ghế theo Section (Batch update seat status):** Đổi toàn bộ ghế thuộc một khán đài sang `AVAILABLE`, `BOOKED`, hoặc `LOCKED` chỉ trong 1 thao tác.
  - Xóa 1 ghế đơn lẻ.
  - **Xóa hàng loạt ghế theo Section (Batch remove seats):** Xóa nhanh toàn bộ ghế của một khán đài.
  - Tìm kiếm ghế theo mã.
- **6. Quản lý vé (Manage Ticket) - Hỗ trợ thao tác hàng loạt:**
  - Xem danh sách vé theo trận đấu kèm phân trang.
  - Tạo vé đơn lẻ cho một ghế cụ thể.
  - **Khởi tạo vé hàng loạt cho trận đấu / theo Section (Batch generate tickets):** Tự động quét toàn bộ ghế chưa có vé và sinh vé hàng loạt theo giá phân khu hoặc giá tùy chỉnh (override).
  - Cập nhật giá bán 1 vé.
  - **Cập nhật giá vé hàng loạt theo Section (Batch update ticket prices):** Điều chỉnh giá cho toàn bộ vé thuộc một khán đài.
  - Xóa 1 vé đơn lẻ.
  - **Xóa vé hàng loạt theo Section (Batch remove tickets):** Xóa toàn bộ vé của một khán đài.
- **7. Quản lý khuyến mãi (Manage Voucher):**
  - Xem danh sách voucher đang hoạt động.
  - Tạo mới voucher (mã code, phần trăm giảm giá, giá trị đơn tối thiểu, hạn sử dụng).
  - Chỉnh sửa và xóa voucher.
- **8. Giả lập & Đo lường hiệu năng đồng bộ (Simulation & Data):**
  - **Tự động sinh dữ liệu lớn (Bulk data generation):** Tự động sinh $\ge$ 10,000 dòng dữ liệu ghế và vé mẫu để kiểm thử tải lớn.
  - **Mô phỏng đặt vé đa luồng (Multithreading simulation):** Tùy chỉnh số lượng luồng chạy đồng thời (Threads) và số lượt đặt chỗ.
  - **4 cơ chế đồng bộ hóa dữ liệu (Sync Mechanism):**
    1. `NO_LOCK`: Chạy song song không đồng bộ (dùng để phát hiện xung đột / Race Condition và hiện tượng bán trùng ghế).
    2. `FILE_LOCK`: Đồng bộ hóa cấp tệp tin hệ thống.
    3. `OPTIMISTIC`: Cơ chế khóa lạc quan (Optimistic Concurrency Control kèm phiên bản dữ liệu).
    4. `SYNCHRONIZED`: Đồng bộ hóa bộ nhớ sử dụng monitor lock / `ReentrantLock` của Java.
  - **Bảng so sánh hiệu năng chi tiết:** Đo lường chính xác thời gian thực thi (Execution Time), số giao dịch thành công/thất bại và số lượng xung đột ghế (Seat conflicts).
  - Xuất kết quả giả lập ra tệp CSV để phân tích.
  - Xem toàn bộ lịch sử giao dịch toàn hệ thống và tổng doanh thu thực tế (hỗ trợ phân trang và xem toàn bộ `all`).
- **9. Đăng xuất (Logout).**

---

## 🚀 Hướng dẫn Cài đặt & Chạy chương trình

### Yêu cầu môi trường

- **Java Development Kit (JDK):** Phiên bản 17, 21, 25 hoặc 27 trở lên.
- **Hệ điều hành:** Windows / Linux / macOS (Hỗ trợ UTF-8).

### Cách 1: Chạy nhanh bằng script (Khuyên dùng trên Windows)

Dự án đã tích hợp sẵn script tự động biên dịch và chạy với encoding UTF-8:

- **Dùng Command Prompt:**
  ```cmd
  run.bat
  ```
- **Dùng PowerShell:**
  ```powershell
  .\run.ps1
  ```

### Cách 2: Biên dịch và chạy thủ công bằng lệnh

1. **Biên dịch toàn bộ mã nguồn vào thư mục `bin`:**
   ```bash
   javac -encoding UTF-8 -d bin src/model/enums/*.java src/model/*.java src/repository/*.java src/controller/*.java src/view/*.java src/Main.java
   ```
2. **Khởi chạy chương trình:**
   ```bash
   java -cp bin Main
   ```

---

## 📁 Cấu trúc thư mục dự án

```text
bookticket/
├── bin/                          # Thư mục chứa các tệp .class đã biên dịch
├── data/                         # Thư mục lưu trữ dữ liệu dạng CSV
│   ├── accounts.csv              # Danh sách tài khoản chung
│   ├── admins.csv                # Dữ liệu tài khoản Admin
│   ├── fans.csv                  # Dữ liệu tài khoản Fan
│   ├── staff.csv                 # Dữ liệu tài khoản Staff
│   ├── matches.csv               # Danh sách các trận đấu
│   ├── stadiums.csv              # Danh sách sân vận động
│   ├── sections.csv              # Phân khu khán đài (Stand A, B, C, D)
│   ├── seats.csv                 # Dữ liệu chỗ ngồi
│   ├── tickets.csv               # Dữ liệu vé
│   ├── transactions.csv          # Lịch sử các giao dịch thanh toán
│   ├── vouchers.csv              # Danh sách mã giảm giá
│   ├── notifications.csv         # Thông báo gửi đến người dùng
│   ├── support_requests.csv      # Yêu cầu hỗ trợ khách hàng
│   └── invoices/                 # Thư mục lưu các hóa đơn thanh toán xuất ra
├── src/                          # Mã nguồn chính của chương trình (Java)
│   ├── Main.java                 # Điểm khởi chạy ứng dụng (Entry point)
│   ├── model/                    # Các lớp thực thể dữ liệu (Entity)
│   │   ├── enums/                # Các kiểu liệt kê (Role, Stand, SeatStatus, SyncMechanism...)
│   │   └── BaseEntity.java       # Lớp cơ sở cho thực thể có định danh ID
│   ├── repository/               # Tầng tương tác và đọc/ghi dữ liệu CSV
│   │   ├── CsvRepository.java    # Generic Repository cơ sở
│   │   └── ...                   # Các repository cụ thể cho từng thực thể
│   ├── controller/               # Tầng xử lý logic nghiệp vụ và điều phối
│   │   ├── ApplicationController.java
│   │   ├── TransactionController.java (Đa luồng & Concurrency control)
│   │   ├── SimulatorController.java   (Giả lập tải lớn & Đo hiệu năng)
│   │   └── ...                   # Các controller nghiệp vụ
│   └── view/                     # Tầng hiển thị giao diện dòng lệnh (CLI)
│       ├── AuthView.java         # Đăng nhập, đăng ký
│       ├── FanView.java          # Giao diện dành cho Cổ động viên
│       ├── StaffView.java        # Giao diện dành cho Nhân viên
│       ├── AdminView.java        # Giao diện dành cho Quản trị viên
│       └── ...
├── run.bat                       # Script chạy nhanh trên CMD Windows
├── run.ps1                       # Script chạy nhanh trên PowerShell
└── README.md                     # Tài liệu đặc tả hệ thống và hướng dẫn sử dụng
```
