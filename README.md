# ⚽ Football Management System

Hệ thống phần mềm hỗ trợ **quản lý giải vô địch bóng đá quốc gia**, bao gồm nhiều chức năng cho từng vai trò người dùng như Ban tổ chức, Ban quản lý CLB, Ban phân tích kết quả, v.v.

---

## 🧰 Yêu cầu hệ thống

* ✅ Java Development Kit (**JDK 23**)
* ✅ **JavaFX SDK**
* ✅ **Oracle JDBC Driver (ojdbc11)**
* ✅ **Oracle Database**

---

## 🚀 Hướng dẫn cài đặt

### 1️⃣ Clone & mở project

```bash
git clone [URL_REPO]
```

* Mở IntelliJ IDEA → **Open Project** → chọn thư mục vừa clone.

### 2️⃣ Thêm thư viện cần thiết

#### 📦 Thêm `ojdbc11`

* Vào `File` → `Project Structure` → `Modules` → `Dependencies` → `+` → **JARs or directories** → Chọn file `ojdbc11.jar`.

🔗 Tải `ojdbc11`:
👉 [Download ojdbc11](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html)

#### 🎨 Thêm JavaFX

* Vào `File` → `Project Structure` → `Libraries` → `+` → Chọn thư mục `lib` trong JavaFX SDK đã giải nén.

🔗 Tải JavaFX SDK:
👉 [Download JavaFX](https://gluonhq.com/products/javafx/)

* Cấu hình VM options khi chạy:

```sh
--module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.web
```

---

### 3️⃣ Cài đặt CSDL

* Mở Oracle SQL Developer hoặc môi trường tương đương.
* Tạo user mới:

```sql
CREATE USER c##QLDB1 IDENTIFIED BY 1;
GRANT ALL PRIVILEGES TO c##QLDB1;
```

* Mở file:
  `src/main/java/Database/QLDB1.sql`
  → Chạy toàn bộ nội dung SQL để tạo bảng và dữ liệu mẫu.

---

### 4️⃣ Chạy chương trình

* Chạy file:
  `src/main/java/Test/test.java`

---

### 5️⃣ Đăng nhập hệ thống

| Vai trò                     | Tên đăng nhập        | Mật khẩu |
|-----------------------------|----------------------|----------|
| 👑 Admin (Full quyền)       | `0`                  | `0`      |
| 🏆 Ban tổ chức giải đấu     | `admin`              | `123`    |
| ⚔ Ban quản lý thi đấu       | `admin4`             | `1234`   |
| 🏟 Ban quản lý CLB          | `admin45`            | `12345`  |
| 📊 Ban phân tích & tổng hợp | `admin456`           | `123456` |
| 👤 Khách (Guest)            | Bấm `Login as Guest` |          |

---

## 📂 Cấu trúc thư mục

```
FootballManagementSystem/
├— src/
│   ├— main/
│   │   └— java/
│   │       ├— Model
│   │       ├— Controller
│   │       ├— Database/
│   │       │   └— QLDB1.sql
│   │       ├— Service
│   │       ├— Util
│   │       └— Test/
│   │           └— test.java
│   │   └— Resources/
│   │       ├— CSS
│   │       ├— Icon
│   │       ├— Image
│   │       └— View
├— pom.xml 
└— README.md
```

---

## 📌 Ghi chú

* Đảm bảo `Oracle` đang hoạt động trước khi chạy chương trình.
* Nếu gặp lỗi kết nối, kiểm tra `username/password` và URL kết nối trong code.

---

## 💬 Liên hệ

Mọi góp ý hoặc thắc mắc, vui lòng liên hệ **nhóm phát triển** qua email: nhuquoc1104@gmail.com hoặc GitHub Issues.

---
## Tài liệu kèm theo
* Repository của dự án: [FootballManagerSystem](https://github.com/Ngnquoc1/FootballManagementSystem)
