# FootballManagementSystem

## Mô tả
Mô tả ngắn về chức năng của dự án.

## Yêu cầu hệ thống
- JDK 23
- JavaFX
- ojdbc11
- Oracle Database

## Hướng dẫn thêm các thư viện
Thêm tất cả các thư viện cần thiết vào mục External Library:
- JDK 23
- ojdbc11: Vào File -> Project Structure -> Modules -> Add -> Jars or Directories -> Chọn file ojdbc11.jar hoặc thư mục chứa file .jar.
- JavaFX : Vào File -> Project Structure -> Libraries -> Add -> Chọn thư mục lib trong JavaFx SDK đã tải (https://gluonhq.com/products/javafx/)

## Hướng dẫn cài đặt chương trình
1. Bước 1: Clone project FootballManagementSystem.
2. Bước 2: Vào IntelliJ chọn Open project và mở project vừa clone về.
3. Bước 3: Làm theo mục "Hướng dẫn thêm các thư viện".
4. Bước 4: Trong `src/main/java/Database` có file `QLDB1.sql`. Vào Oracle tạo user mới với Username là `c##QLDB1` và Password là `1`, sau đó chạy toàn bộ file `Database.sql`.
5. Bước 5: Chạy file `src/main/java/Test/test.java` để khởi động chương trình, đăng nhập với vai trò quản lý bằng Username là `admin` và Password là `123`.
