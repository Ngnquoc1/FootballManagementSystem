CREATE TABLE MuaGiai (
                         MaMG NUMBER PRIMARY KEY,
                         TenMG NVARCHAR2(100) NOT NULL,
                         NgayKhaiMac DATE NOT NULL,
                         NgayBeMac DATE NOT NULL,
                         LogoMG NVARCHAR2(100)
);
CREATE TABLE VongDau (
                         MaVD NUMBER PRIMARY KEY,
                         MaMG  NUMBER NOT NULL,
                         TENVD NVARCHAR2(20),
                         NgayBD DATE NOT NULL,
                         NgayKT DATE NOT NULL
);
CREATE TABLE CLB (
                     MaCLB NUMBER PRIMARY KEY,
                     TenCLB NVARCHAR2(100) NOT NULL,
                     LogoCLB NVARCHAR2(255),
                     SanNha NUMBER NOT NULL
);
CREATE TABLE SAN (
                     MaSan NUMBER PRIMARY KEY,
                     TenSan NVARCHAR2(100) NOT NULL,
                     DiaChi NVARCHAR2(100) NOT NULL,
                     Succhua NUMBER NOT NULL
);
CREATE TABLE CLB_THAMGIAMUAGIAI (
                                    MaCLB NUMBER,
                                    MaMG NUMBER,
                                    PRIMARY KEY (MaCLB, MaMG)
);
CREATE TABLE CauThu (
                        MaCT NUMBER PRIMARY KEY,
                        TenCT NVARCHAR2(50) NOT NULL,
                        NgaySinh DATE,
                        LoaiCT NUMBER NOT NULL
);
CREATE TABLE CAUTHU_CLB(
                           MaMG NUMBER ,
                           MaCLB NUMBER NOT NULL,
                           MaCT NUMBER NOT NULL,
                           SoAo NUMBER NOT NULL,
                           ViTriTD NVARCHAR2(2) NOT NULL,
                           PRIMARY KEY (MaMG,MaCLB,MaCT)
);
CREATE TABLE TranDau (
                         MaTD NUMBER  PRIMARY KEY,
                         MaCLB1 NUMBER NOT NULL,
                         MaCLB2 NUMBER NOT NULL,
                         MaSan NUMBER NOT NULL,
                         MaVD NUMBER NOT NULL,
                         ThoiGian DATE NOT NULL
);
CREATE TABLE KetQuaTD (
                          MaTD NUMBER PRIMARY KEY,
                          DiemCLB1 NUMBER,
                          DiemCLB2 NUMBER
);
-- Bảng BanThang
CREATE TABLE BanThang (
                          MaBT NUMBER PRIMARY KEY,
                          MaCT NUMBER NOT NULL,
                          MaTD NUMBER NOT NULL,
                          PhutGhiBan NUMBER NOT NULL,
                          LoaiBanThang NVARCHAR2(50) NOT NULL
);
CREATE TABLE QuyDinh (
                         MaQD NUMBER PRIMARY KEY,
                         MaMG NUMBER,
                         TUOITOITHIEU NUMBER DEFAULT 16,
                         TUOITOIDA NUMBER DEFAULT 40,
                         SOCTTOITHIEU NUMBER DEFAULT 15,
                         SOCTTOIDA NUMBER DEFAULT 22,
                         SOCTNUOCNGOAITOIDA NUMBER DEFAULT 3
);
CREATE TABLE BANGXEPHANG_CLB (
                                 MaMG NUMBER NOT NULL,
                                 MaCLB NUMBER NOT NULL,
                                 Thang NUMBER DEFAULT 0,
                                 Hoa NUMBER DEFAULT 0,
                                 Thua NUMBER DEFAULT 0,
                                 HieuSo NUMBER DEFAULT 0,
                                 Hang NUMBER,
                                 PRIMARY KEY (MaMG, MaCLB)
);
CREATE TABLE BANGXEPHANG_BANTHANG (
                                      MaMG NUMBER NOT NULL,
                                      MaCT NUMBER NOT NULL,
                                      SoBanThang NUMBER DEFAULT 0,
                                      XepHang NUMBER UNIQUE,
                                      PRIMARY KEY (MaMG, MaCT)
);
CREATE TABLE TaiKhoan (
                          TenDangNhap VARCHAR2(50) PRIMARY KEY,
                          MatKhau VARCHAR2(100) NOT NULL,
                          VaiTro VARCHAR2(30) CHECK (VaiTro IN ('A', 'B', 'C', 'D'))
);

-----Foreign key----------------------------
--CLB
ALTER TABLE CLB ADD CONSTRAINT Fk_SanNha FOREIGN KEY(SanNha) REFERENCES SAN(MaSan);

--CLB_THAMGIAMUAGIAI
ALTER TABLE CLB_THAMGIAMUAGIAI ADD CONSTRAINT FK_CLB_TG FOREIGN KEY(MaCLB) REFERENCES CLB(MaCLB);
ALTER TABLE CLB_THAMGIAMUAGIAI ADD CONSTRAINT FK_MG_TG FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG);

--VONGDAU
ALTER TABLE VONGDAU ADD CONSTRAINT Fk_MGVD FOREIGN KEY(MaMG) REFERENCES MUAGIAI(MaMG);

--CAUTHU_CLB
ALTER TABLE CAUTHU_CLB ADD CONSTRAINT  Fk_MGCT FOREIGN KEY(MaMG) REFERENCES MUAGIAI(MaMG);
ALTER TABLE CAUTHU_CLB ADD CONSTRAINT  Fk_CLBCT FOREIGN KEY(MaCLB) REFERENCES CLB(MaCLB);
ALTER TABLE CAUTHU_CLB ADD CONSTRAINT  Fk_CT FOREIGN KEY(MaCT) REFERENCES CAUTHU(MaCT);


--TRANDAU
ALTER TABLE TRANDAU ADD CONSTRAINT Fk_CLB1 FOREIGN KEY(MaCLB1) REFERENCES CLB(MaCLB);
ALTER TABLE TRANDAU ADD CONSTRAINT Fk_CLB2 FOREIGN KEY(MaCLB2) REFERENCES CLB(MaCLB);
ALTER TABLE TRANDAU ADD CONSTRAINT Fk_VONGDAU_TD FOREIGN KEY(MaVD) REFERENCES VONGDAU(MaVD);
ALTER TABLE TRANDAU ADD CONSTRAINT Fk_SanTD FOREIGN KEY(MaSan) REFERENCES SAN(MaSan);

--KQUATRANDAU
ALTER TABLE KetQuaTD ADD CONSTRAINT Fk_KQTD FOREIGN KEY(MaTD) REFERENCES TranDau(MaTD);

--BANTHANG
ALTER TABLE BANTHANG ADD CONSTRAINT Fk_TD_BT FOREIGN KEY(MaTD) REFERENCES TranDau(MaTD);
ALTER TABLE BANTHANG ADD CONSTRAINT Fk_CT_BT FOREIGN KEY(MaCT) REFERENCES CauThu(MaCT);

--QUYDINH
ALTER TABLE QUYDINH ADD CONSTRAINT Fk_QDMG FOREIGN KEY(MaMG) REFERENCES MUAGIAI(MaMG);

--BANGXEPHANG_CLB
ALTER TABLE BANGXEPHANG_CLB ADD CONSTRAINT fk_maMG FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG);
ALTER TABLE BANGXEPHANG_CLB ADD CONSTRAINT fk_chitietbxh_madoi FOREIGN KEY (MaCLB) REFERENCES CLB(MaCLB);

--BANGXEPHANG_BANTHANG
ALTER TABLE BANGXEPHANG_BANTHANG ADD CONSTRAINT fk_maMG_bxhbt FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG);
ALTER TABLE BANGXEPHANG_BANTHANG ADD CONSTRAINT fk_chitietbxh_maCT FOREIGN KEY (MaCT) REFERENCES CAUTHU(MaCT);


-------------MienGiaTri----------------
--VongDau
ALTER TABLE VongDau ADD CONSTRAINT chk_TenVD CHECK (TENVD IN ('LUOT DI', 'LUOT VE'));

--Cauthu
ALTER TABLE CauThu_CLB ADD CONSTRAINT chk_ViTriTD CHECK (ViTriTD IN ('ST', 'MF', 'DF', 'GK'));
ALTER TABLE CauThu ADD CONSTRAINT chk_LoaiCT CHECK (LoaiCT IN (0,1));

--Banthang
ALTER TABLE BanThang ADD CONSTRAINT chk_LoaiBT CHECK (LoaiBanThang IN ('A','B','C'));
ALTER TABLE BanThang ADD CONSTRAINT chk_PhutGB CHECK (PhutGhiBan BETWEEN 0 AND 90);

------------------INSERT---------------
INSERT INTO TaiKhoan VALUES ('admin', 'admin123', 'A');

INSERT INTO MuaGiai (MaMG, TenMG, NgayKhaiMac, NgayBeMac) VALUES (1, 'V-League 2025', TO_DATE('01/01/2025', 'DD/MM/YYYY'), TO_DATE('30/06/2025', 'DD/MM/YYYY'));

INSERT INTO VongDau (MaVD, MaMG, TENVD, NgayBD, NgayKT) VALUES (1, 1, 'LUOT DI', TO_DATE('01/01/2025', 'DD/MM/YYYY'), TO_DATE('31/03/2025', 'DD/MM/YYYY'));
INSERT INTO VongDau (MaVD, MaMG, TENVD, NgayBD, NgayKT) VALUES (2, 1, 'LUOT VE', TO_DATE('01/04/2025', 'DD/MM/YYYY'), TO_DATE('30/06/2025', 'DD/MM/YYYY'));

INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (1, 'Sân Mỹ Đình', 'Hà Nội', 40000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (2, 'Sân Thống Nhất', 'TP Hồ Chí Minh', 25000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (3, 'Sân Hàng Đẫy', 'Hà Nội', 20000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (4, 'Sân Lạch Tray', 'Hải Phòng', 30000);

INSERT INTO CLB (MaCLB, TenCLB, SanNha) VALUES (1, 'Hà Nội FC', 1);
INSERT INTO CLB (MaCLB, TenCLB, SanNha) VALUES (2, 'TP Hồ Chí Minh FC', 2);
INSERT INTO CLB (MaCLB, TenCLB, SanNha) VALUES (3, 'Viettel FC', 3);
INSERT INTO CLB (MaCLB, TenCLB, SanNha) VALUES (4, 'Hải Phòng FC', 4);

INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (1, 1);
INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (2, 1);
INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (3, 1);
INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (4, 1);

-- Lượt đi (MaVD=1)
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (1, 1, 2, 1, 1, TO_DATE('15/01/2025 19:00', 'DD/MM/YYYY HH24:MI')); -- Hà Nội FC vs TP HCM FC
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (2, 3, 4, 3, 1, TO_DATE('15/01/2025 19:00', 'DD/MM/YYYY HH24:MI')); -- Viettel FC vs Hải Phòng FC

-- Lượt về (MaVD=2)
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (3, 2, 1, 2, 2, TO_DATE('15/04/2025 19:00', 'DD/MM/YYYY HH24:MI')); -- TP HCM FC vs Hà Nội FC
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (4, 4, 3, 4, 2, TO_DATE('15/09/2025 19:00', 'DD/MM/YYYY HH24:MI')); -- Hải Phòng FC vs Viettel FC

UPDATE CLB SET LogoCLB = 'HNFC.jpg' WHERE MaCLB = 1; -- Hà Nội FC
UPDATE CLB SET LogoCLB = 'HCMFC.jpg' WHERE MaCLB = 2; -- TP HCM FC
UPDATE CLB SET LogoCLB = 'VTFC.jpg' WHERE MaCLB = 3; -- Viettel FC
UPDATE CLB SET LogoCLB = 'HPFC.jpg' WHERE MaCLB = 4;

SELECT
    c1.TenCLB AS TenCLB1,
    c1.LogoCLB AS LogoCLB1,
    c2.TenCLB AS TenCLB2,
    c2.LogoCLB AS LogoCLB2,
    TO_CHAR(td.ThoiGian, 'HH24:MI') AS GioThiDau,
    TO_CHAR(td.ThoiGian, 'DD/MM/YYYY') AS NgayThiDau,
    s.TenSan AS SanThiDau
FROM TranDau td
         JOIN CLB c1 ON td.MaCLB1 = c1.MaCLB
         JOIN CLB c2 ON td.MaCLB2 = c2.MaCLB
         JOIN SAN s ON td.MaSan = s.MaSan
         LEFT JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
WHERE kq.MaTD IS NULL;


COMMIT;
---------------------------------------TRIGGER--------------------------------
--QUY ĐỊNH VỀ ĐỘ TUỔI THAM GIA MÙA GIẢI CỦA CẦU THỦ
CREATE OR REPLACE TRIGGER trg_check_tuoi_cauthu_clb
BEFORE INSERT OR UPDATE ON CauThu_CLB
                            FOR EACH ROW
DECLARE
v_tuoi NUMBER;
    v_tuoi_toithieu NUMBER;
    v_tuoi_toida NUMBER ;
    v_ngay_khaimac DATE;
BEGIN
    -- Lấy ngày khai mạc mùa giải
SELECT NgayKhaiMac
INTO v_ngay_khaimac
FROM MuaGiai
WHERE MaMG = :NEW.MaMG;

-- Lấy quy định tuổi từ QuyDinh (nếu có)
BEGIN
SELECT NVL(TUOITOITHIEU, 16), NVL(TUOITOIDA, 40)
INTO v_tuoi_toithieu, v_tuoi_toida
FROM QuyDinh
WHERE MaMG = :NEW.MaMG;
END;

    -- Tính tuổi dựa trên ngày sinh và ngày khai mạc mùa giải
SELECT FLOOR(MONTHS_BETWEEN(v_ngay_khaimac, ct.NgaySinh) / 12)
INTO v_tuoi
FROM CauThu ct
WHERE ct.MaCT = :NEW.MaCT;

-- Kiểm tra tuổi theo quy định
IF v_tuoi < v_tuoi_toithieu OR v_tuoi > v_tuoi_toida THEN
        RAISE_APPLICATION_ERROR(-20006,
            'Tuổi cầu thủ phải từ ' || v_tuoi_toithieu || ' đến ' || v_tuoi_toida ||
            '. Tuổi hiện tại: ' || v_tuoi);
END IF;


EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20011,
            'Không tìm thấy thông tin mùa giải hoặc cầu thủ cho MaMG: ' || :NEW.MaMG);
END;


--------QUY ĐỊNH VỀ TỔNG SỐ LƯỢNG CẦU THỦ VÀ SỐ LƯỢNG CẦU THỦ NGOẠI CHO PHÉP THAM GIA MÙA GIẢI

CREATE OR REPLACE TRIGGER trg_check_so_cauthu_clb
BEFORE INSERT OR UPDATE OR DELETE ON CauThu_CLB
    FOR EACH ROW
DECLARE
v_total_players NUMBER;
    v_foreign_players NUMBER;
    v_soct_toithieu NUMBER;
    v_soct_toida NUMBER;
    v_soct_nuocngoai_toida NUMBER;
    v_loai_ct NUMBER;
BEGIN
    -- Lấy quy định từ bảng QuyDinh (nếu có)
BEGIN
SELECT NVL(SOCTTOITHIEU, 15), NVL(SOCTTOIDA, 22), NVL(SOCTNUOCNGOAITOIDA, 3)
INTO v_soct_toithieu, v_soct_toida, v_soct_nuocngoai_toida
FROM QuyDinh
WHERE MaMG = :NEW.MaMG;
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            -- Dùng giá trị mặc định nếu không tìm thấy quy định
            v_soct_toithieu := 15;
            v_soct_toida := 22;
            v_soct_nuocngoai_toida := 3;
END;

    -- Đếm tổng số cầu thủ của CLB trong mùa giải
SELECT COUNT(*)
INTO v_total_players
FROM CauThu_CLB
WHERE MaMG = :NEW.MaMG
  AND MaCLB = :NEW.MaCLB;

-- Đếm số cầu thủ nước ngoài của CLB trong mùa giải
SELECT COUNT(*)
INTO v_foreign_players
FROM CauThu_CLB ct_clb
         JOIN CauThu ct ON ct_clb.MaCT = ct.MaCT
WHERE ct_clb.MaMG = :NEW.MaMG
  AND ct_clb.MaCLB = :NEW.MaCLB
  AND ct.LoaiCT = 1;

-- Lấy loại cầu thủ của MaCT mới
SELECT LoaiCT
INTO v_loai_ct
FROM CauThu
WHERE MaCT = :NEW.MaCT;

-- Kiểm tra khi thêm cầu thủ mới
IF INSERTING OR UPDATING THEN
        -- Tổng số cầu thủ không được vượt quá tối đa
        IF v_total_players >= v_soct_toida THEN
            RAISE_APPLICATION_ERROR(-20007,
                'Số lượng cầu thủ của CLB trong mùa giải đã đạt tối đa (' || v_soct_toida || ').');
END IF;

        -- Kiểm tra số cầu thủ nước ngoài nếu cầu thủ mới là nước ngoài
        IF v_loai_ct = 1 AND v_foreign_players >= v_soct_nuocngoai_toida THEN
            RAISE_APPLICATION_ERROR(-20008,
                'Số cầu thủ nước ngoài của CLB đã đạt tối đa (' || v_soct_nuocngoai_toida || ').');
END IF;

    -- Kiểm tra khi cập nhật
    ELSIF DELETING THEN
        IF v_total_players < v_soct_toithieu THEN
        RAISE_APPLICATION_ERROR(-20008,
                'Cảnh báo: Số cầu thủ hiện tại (' || v_total_players ||
                             ') chưa đạt tối thiểu ' || v_soct_toithieu || '.');
END IF;
END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20012,
            'Không tìm thấy thông tin cầu thủ cho MaCT: ' || :NEW.MaCT);
END;


-----------PROCEDURE---------------------
CREATE OR REPLACE PROCEDURE GetMatchesByCondition(
    p_condition IN VARCHAR2 DEFAULT NULL,
    p_result OUT SYS_REFCURSOR
)
AS
    v_sql VARCHAR2(4000);
BEGIN
    -- Xây dựng truy vấn cơ bản
    v_sql := 'SELECT
                 td.MaTD as ID,
                 c1.TenCLB AS TenCLB1,
                 c1.LogoCLB AS LogoCLB1,
                 c2.TenCLB AS TenCLB2,
                 c2.LogoCLB AS LogoCLB2,
                 td.ThoiGian AS ThoiGian,
                 s.TenSan AS SanThiDau,
                 m.TenMG AS TenMuaGiai,
                 v.TENVD AS TenVD
              FROM TranDau td
              JOIN VongDau v ON td.MaVD = v.MaVD
              JOIN CLB c1 ON td.MaCLB1 = c1.MaCLB
              JOIN CLB c2 ON td.MaCLB2 = c2.MaCLB
              JOIN SAN s ON td.MaSan = s.MaSan
              JOIN MuaGiai m ON v.MaMG = m.MaMG
              LEFT JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
              WHERE kq.MaTD IS NULL';

    -- Thêm điều kiện nếu có
    IF p_condition IS NOT NULL THEN
        v_sql := v_sql || ' AND (' || p_condition || ')';
END IF;

    -- Thêm ORDER BY ở cuối
    v_sql := v_sql || ' ORDER BY td.ThoiGian';

    -- Mở cursor với truy vấn động
OPEN p_result FOR v_sql;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20020, 'Lỗi khi thực thi procedure: ' || SQLERRM);
END GetMatchesByCondition;
----------------Update Match--------------
CREATE OR REPLACE PROCEDURE UpdateMatch(
    p_maTD IN NUMBER,
    p_tenMuaGiai IN VARCHAR2,
    p_tenVD IN VARCHAR2,
    p_tenCLB1 IN VARCHAR2,
    p_tenCLB2 IN VARCHAR2,
    p_thoiGian IN DATE,
    p_tenSan IN VARCHAR2
)
AS
    v_maMG NUMBER;
    v_maVD NUMBER;
    v_maCLB1 NUMBER;
    v_maCLB2 NUMBER;
    v_maSan NUMBER;
BEGIN
    -- 1. Ánh xạ TenMuaGiai thành MaMG
SELECT MaMG INTO v_maMG
FROM MuaGiai
WHERE TenMG = p_tenMuaGiai;

-- 2. Tìm MaVD từ MaMG (giả sử lấy MaVD đầu tiên nếu có nhiều vòng đấu)
SELECT MaVD INTO v_maVD
FROM VongDau
WHERE MaMG = v_maMG and TENVD=p_TenVD;

-- 3. Ánh xạ TenCLB1 thành MaCLB1
SELECT MaCLB INTO v_maCLB1
FROM CLB
WHERE TenCLB = p_tenCLB1;

-- 4. Ánh xạ TenCLB2 thành MaCLB2
SELECT MaCLB INTO v_maCLB2
FROM CLB
WHERE TenCLB = p_tenCLB2;

-- 5. Ánh xạ TenSan thành MaSan
SELECT MaSan INTO v_maSan
FROM SAN
WHERE TenSan = p_tenSan;

-- 6. Cập nhật bảng TranDau
UPDATE TranDau
SET MaVD = v_maVD,
    MaCLB1 = v_maCLB1,
    MaCLB2 = v_maCLB2,
    ThoiGian = p_thoiGian,
    MaSan = v_maSan
WHERE MaTD = p_maTD;

-- Kiểm tra xem có cập nhật thành công không
IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Không tìm thấy trận đấu với MaTD = ' || p_maTD);
END IF;

    -- Commit giao dịch
COMMIT;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Không tìm thấy dữ liệu tương ứng (MuaGiai, CLB, SAN hoặc VongDau).');
WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20003, 'Lỗi khi cập nhật trận đấu: ' || SQLERRM);
END UpdateMatch;
/

-----------------------InsertMatch--------
CREATE OR REPLACE PROCEDURE InsertMatch(
    p_tenMuaGiai IN VARCHAR2,
    p_tenVD IN NVARCHAR2,
    p_tenCLB1 IN VARCHAR2,
    p_tenCLB2 IN VARCHAR2,
    p_thoiGian IN DATE,
    p_tenSan IN VARCHAR2,
    p_maTD OUT NUMBER
)
AS
    v_maTD NUMBER;
    v_maMG NUMBER;
    v_maVD NUMBER;
    v_maCLB1 NUMBER;
    v_maCLB2 NUMBER;
    v_maSan NUMBER;
BEGIN

    -- 1. Ánh xạ TenMuaGiai thành MaMG
SELECT MaMG INTO v_maMG
FROM MuaGiai
WHERE TenMG = p_tenMuaGiai;

-- 2. Tìm MaVD từ MaMG (lấy MaVD đầu tiên nếu có nhiều vòng đấu)
SELECT MaVD INTO v_maVD
FROM VongDau
WHERE MaMG = v_maMG and TENVD=p_tenVD;

-- 3. Ánh xạ TenCLB1 thành MaCLB1
SELECT MaCLB INTO v_maCLB1
FROM CLB
WHERE TenCLB = p_tenCLB1;

-- 4. Ánh xạ TenCLB2 thành MaCLB2
SELECT MaCLB INTO v_maCLB2
FROM CLB
WHERE TenCLB = p_tenCLB2;

-- 5. Ánh xạ TenSan thành MaSan
SELECT MaSan INTO v_maSan
FROM SAN
WHERE TenSan = p_tenSan;

--6. Lấy MaTD moi
Select max(MaTD)+1 INTO v_maTD FROM TranDau;

-- 7. Chèn vào TranDau và lấy MaTD tự động tăng (giả sử MaTD là sequence)
INSERT INTO TranDau (MaTD, MaVD, MaCLB1, MaCLB2, ThoiGian, MaSan)
VALUES (v_maTD, v_maVD, v_maCLB1, v_maCLB2, p_thoiGian, v_maSan)
    RETURNING MaTD INTO p_maTD;

-- Commit giao dịch
COMMIT;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Không tìm thấy dữ liệu tương ứng (MuaGiai, CLB, SAN hoặc VongDau).');
WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20003, 'Lỗi khi chèn trận đấu: ' || SQLERRM);
END InsertMatch;
/

----------------------DeleteMatch----------------------
CREATE OR REPLACE PROCEDURE DeleteMatch(
    p_maTD IN NUMBER
)
AS
    v_count NUMBER;
BEGIN
    -- Kiểm tra xem trận đấu có tồn tại không
SELECT COUNT(*) INTO v_count
FROM TranDau
WHERE MaTD = p_maTD;

IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Không tìm thấy trận đấu với MaTD = ' || p_maTD);
END IF;

    -- Xóa bản ghi
DELETE FROM TranDau
WHERE MaTD = p_maTD;

-- Kiểm tra xem có xóa thành công không
IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'Không thể xóa trận đấu với MaTD = ' || p_maTD);
END IF;

    -- Commit giao dịch
COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20006, 'Lỗi khi xóa trận đấu: ' || SQLERRM);
END DeleteMatch;
/
Commit