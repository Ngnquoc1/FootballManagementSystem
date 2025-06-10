-- DROP TABLE THUTU_UUTIEN CASCADE CONSTRAINTS;
-- DROP TABLE BANGXEPHANG_BANTHANG CASCADE CONSTRAINTS;
-- DROP TABLE BANGXEPHANG_CLB CASCADE CONSTRAINTS;
-- DROP TABLE QuyDinh CASCADE CONSTRAINTS;
-- DROP TABLE BanThang CASCADE CONSTRAINTS;
-- DROP TABLE KetQuaTD CASCADE CONSTRAINTS;
-- DROP TABLE TranDau CASCADE CONSTRAINTS;
-- DROP TABLE CAUTHU_THAMGIAMUAGIAI CASCADE CONSTRAINTS;
-- DROP TABLE CauThu CASCADE CONSTRAINTS;
-- DROP TABLE LoaiCauThu CASCADE CONSTRAINTS;
-- DROP TABLE ViTriTD CASCADE CONSTRAINTS;
-- DROP TABLE CLB_THAMGIAMUAGIAI CASCADE CONSTRAINTS;
-- DROP TABLE CLB CASCADE CONSTRAINTS;
-- DROP TABLE SAN CASCADE CONSTRAINTS;
-- DROP TABLE VongDau CASCADE CONSTRAINTS;
-- DROP TABLE MuaGiai CASCADE CONSTRAINTS;
-- DROP TABLE LoaiBanThang CASCADE CONSTRAINTS;
-- DROP TABLE TaiKhoan CASCADE CONSTRAINTS;

CREATE TABLE MuaGiai (
                         MaMG NUMBER PRIMARY KEY,
                         TenMG NVARCHAR2(100) NOT NULL,
                         NgayKhaiMac DATE NOT NULL,
                         NgayBeMac DATE NOT NULL,
                         LogoMG NVARCHAR2(100)
);
/
CREATE TABLE VongDau (
                         MaVD NUMBER PRIMARY KEY,
                         MaMG NUMBER NOT NULL,
                         TENVD NVARCHAR2(200),
                         NgayBD DATE NOT NULL,
                         NgayKT DATE NOT NULL,
                         FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG)
);
/
CREATE TABLE SAN (
                     MaSan NUMBER PRIMARY KEY,
                     TenSan NVARCHAR2(100) NOT NULL,
                     DiaChi NVARCHAR2(100) NOT NULL,
                     Succhua NUMBER NOT NULL
);
/
CREATE TABLE CLB (
                     MaCLB NUMBER PRIMARY KEY,
                     TenCLB NVARCHAR2(100) NOT NULL,
                     LogoCLB NVARCHAR2(255),
                     SanNha NUMBER NOT NULL,
                     TenHLV NVARCHAR2(100),
                     Email NVARCHAR2(255),
                     FOREIGN KEY (SanNha) REFERENCES SAN(MaSan)
);
/
CREATE TABLE CLB_THAMGIAMUAGIAI (
                                    MaCLB NUMBER,
                                    MaMG NUMBER,
                                    PRIMARY KEY (MaCLB, MaMG),
                                    FOREIGN KEY (MaCLB) REFERENCES CLB(MaCLB),
                                    FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG)
);
/
CREATE TABLE ViTriTD (
                         MaVT NUMBER PRIMARY KEY,
                         TenVT NVARCHAR2(200)
);
/
CREATE TABLE LoaiCauThu (
                            MaLoaiCT NUMBER PRIMARY KEY,
                            TenLoaiCT NVARCHAR2(200) NOT NULL
);
/
CREATE TABLE LoaiBanThang (
                              MaLoaiBT NUMBER PRIMARY KEY,
                              TenLoaiBT NVARCHAR2(100)
);
/
CREATE TABLE CauThu(
                       MaCT NUMBER PRIMARY KEY,
                       TenCT NVARCHAR2(50) NOT NULL,
                       NgaySinh DATE,
                       QuocTich NVARCHAR2(100),
                       Avatar NVARCHAR2(100),
                       SoAo NUMBER,
                       LoaiCT NUMBER NOT NULL,
                       MaCLB NUMBER,
                       MaVT NUMBER,
                       FOREIGN KEY (MaCLB) REFERENCES CLB(MaCLB),
                       FOREIGN KEY (LoaiCT) REFERENCES LoaiCauThu(MaLoaiCT),
                       FOREIGN KEY (MaVT) REFERENCES ViTriTD(MaVT)
);

/
CREATE TABLE CAUTHU_THAMGIAMUAGIAI (
                            MaMG NUMBER NOT NULL,
                            MaCLB NUMBER NOT NULL,
                            MaCT NUMBER NOT NULL,
                            PRIMARY KEY (MaMG, MaCLB, MaCT),
                            FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG),
                            FOREIGN KEY (MaCT) REFERENCES CauThu(MaCT),
                            CONSTRAINT chk_unique_CAUTHU_THAMGIAMUAGIAI UNIQUE (MaCT, MaMG)
);
/
CREATE TABLE TranDau (
                         MaTD NUMBER PRIMARY KEY,
                         MaCLB1 NUMBER NOT NULL,
                         MaCLB2 NUMBER NOT NULL,
                         MaSan NUMBER NOT NULL,
                         MaVD NUMBER NOT NULL,
                         ThoiGian DATE NOT NULL,
                         FOREIGN KEY (MaCLB1) REFERENCES CLB(MaCLB),
                         FOREIGN KEY (MaCLB2) REFERENCES CLB(MaCLB),
                         FOREIGN KEY (MaSan) REFERENCES SAN(MaSan),
                         FOREIGN KEY (MaVD) REFERENCES VongDau(MaVD)
);
/
CREATE TABLE KetQuaTD (
                          MaTD NUMBER PRIMARY KEY,
                          DiemCLB1 NUMBER,
                          DiemCLB2 NUMBER,
                          FOREIGN KEY (MaTD) REFERENCES TranDau(MaTD)
);
/
CREATE TABLE BanThang (
                          MaBT NUMBER PRIMARY KEY,
                          MaCT NUMBER NOT NULL,
                          MaTD NUMBER NOT NULL,
                          PhutGhiBan NUMBER NOT NULL,
                          MaLoaiBT NUMBER NOT NULL,
                          FOREIGN KEY (MaTD) REFERENCES TranDau(MaTD),
                          FOREIGN KEY (MaCT) REFERENCES CauThu(MaCT),
                          FOREIGN KEY (MaLoaiBT) REFERENCES LoaiBanThang(MaLoaiBT)
);
/

CREATE TABLE QuyDinh (
                         MaMG NUMBER PRIMARY KEY,
                         TUOITOITHIEU NUMBER DEFAULT 16,
                         TUOITOIDA NUMBER DEFAULT 40,
                         SOCTTOITHIEU NUMBER DEFAULT 15,
                         SOCTTOIDA NUMBER DEFAULT 22,
                         SOCTNUOCNGOAITOIDA NUMBER DEFAULT 3,
                         DiemThang NUMBER DEFAULT 3,
                         DiemHoa NUMBER DEFAULT 1,
                         DiemThua NUMBER DEFAULT 0,
                         PhutGhiBanToiDa NUMBER DEFAULT 90,
                         FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG)
);
/
CREATE TABLE BANGXEPHANG_CLB (
                                 MaMG NUMBER NOT NULL,
                                 MaCLB NUMBER NOT NULL,
                                 SoTran NUMBER DEFAULT 0,
                                 Thang NUMBER DEFAULT 0,
                                 Hoa NUMBER DEFAULT 0,
                                 Thua NUMBER DEFAULT 0,
                                 HieuSo NUMBER DEFAULT 0,
                                 Diem NUMBER DEFAULT 0,
                                 Hang NUMBER,
                                 PRIMARY KEY (MaMG, MaCLB),
                                 FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG),
                                 FOREIGN KEY (MaCLB) REFERENCES CLB(MaCLB)
);
/
CREATE TABLE BANGXEPHANG_BANTHANG (
                                      MaMG NUMBER NOT NULL,
                                      MaCT NUMBER NOT NULL,
                                      SoBanThang NUMBER DEFAULT 0,
                                      XepHang NUMBER UNIQUE,
                                      Penalty NUMBER DEFAULT 0,
                                      PRIMARY KEY (MaMG, MaCT),
                                      FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG),
                                      FOREIGN KEY (MaCT) REFERENCES CauThu(MaCT)
);
/
CREATE TABLE THUTU_UUTIEN (
                              MaMG NUMBER NOT NULL,
                              TieuChi NVARCHAR2(50) NOT NULL ,
                              DoUuTien NUMBER NOT NULL ,
                                PRIMARY KEY (MaMG, TieuChi),
                              FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG),
                              CONSTRAINT chk_tieuchi CHECK (TieuChi IN ('Diem', 'HieuSo', 'SoTran', 'Thang', 'Hoa', 'Thua')),
                              CONSTRAINT chk_douutien CHECK (DoUuTien >= 1)
);
/
CREATE TABLE VaiTro(
                       MaVT NUMBER PRIMARY KEY,
                       TenVaiTro VARCHAR2(50)
);
/
CREATE TABLE TaiKhoan (
                          TenDangNhap VARCHAR2(50) PRIMARY KEY,
                          MatKhau VARCHAR2(100) NOT NULL,
                          MaVT NUMBER,
                          FOREIGN KEY (MaVT) REFERENCES VaiTro(MaVT)
);
/



---------------------------------------TRIGGER--------------------------------
---------- Ngày bắt đầu và kết thúc của vòng đấu phải nằm trong khoảng thời gian của mùa giải.

CREATE OR REPLACE TRIGGER trg_check_ngay_vongdau
BEFORE INSERT OR UPDATE ON VongDau
                            FOR EACH ROW
DECLARE
v_ngay_khaimac DATE;
    v_ngay_bemac DATE;
BEGIN
    -- Lấy ngày khai mạc và bế mạc của mùa giải tương ứng
    SELECT NgayKhaiMac, NgayBeMac
    INTO v_ngay_khaimac, v_ngay_bemac
    FROM MuaGiai
    WHERE MaMG = :NEW.MaMG;

-- Kiểm tra ràng buộc thời gian
    IF :NEW.NgayBD < v_ngay_khaimac OR :NEW.NgayKT > v_ngay_bemac THEN
        RAISE_APPLICATION_ERROR(-20001,
                                'Ngày bắt đầu và kết thúc của vòng đấu phải nằm trong khoảng thời gian của mùa giải.');
    END IF;

    -- Kiểm tra NgayBD <= NgayKT
    IF :NEW.NgayBD > :NEW.NgayKT THEN
        RAISE_APPLICATION_ERROR(-20002,
                                'Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.');
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20003,
                                'Không tìm thấy mùa giải tương ứng với MaMG.');
END;
/
------------Thời gian trận đấu phải nằm trong khoảng thời gian diễn ra vòng đấu
CREATE OR REPLACE TRIGGER trg_check_thoigian_trandau
    BEFORE INSERT OR UPDATE ON TranDau
    FOR EACH ROW
DECLARE
    v_ngay_bd DATE;
    v_ngay_kt DATE;
BEGIN
    -- Lấy ngày bắt đầu và kết thúc của vòng đấu tương ứng
    SELECT NgayBD, NgayKT
    INTO v_ngay_bd, v_ngay_kt
    FROM VongDau
    WHERE MaVD = :NEW.MaVD;

-- Kiểm tra xem ThoiGian có nằm trong khoảng NgayBD và NgayKT hay không
    IF TRUNC(:NEW.ThoiGian) < TRUNC(v_ngay_bd) OR TRUNC(:NEW.ThoiGian) > TRUNC(v_ngay_kt) THEN
        RAISE_APPLICATION_ERROR(-20004,
                                'Thời gian trận đấu phải nằm trong khoảng thời gian diễn ra vòng đấu (' ||
                                TO_CHAR(v_ngay_bd, 'DD-MON-YYYY') || ' đến ' ||
                                TO_CHAR(v_ngay_kt, 'DD-MON-YYYY') || ').');
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20005,
                                'Không tìm thấy vòng đấu tương ứng với MaVD: ' || :NEW.MaVD);
END;
/
commit;
------------QUY ĐỊNH VỀ ĐỘ TUỔI THAM GIA MÙA GIẢI CỦA CẦU THỦ
CREATE OR REPLACE TRIGGER trg_check_tuoi_CAUTHU_THAMGIAMUAGIAI
    BEFORE INSERT OR UPDATE ON CAUTHU_THAMGIAMUAGIAI
    FOR EACH ROW
DECLARE
    v_tuoi NUMBER;
    v_tuoi_toithieu NUMBER;
    v_tuoi_toida NUMBER;
    v_ngay_khaimac DATE;
    v_ngay_sinh DATE;
BEGIN
    -- Lấy ngày khai mạc mùa giải
    SELECT NgayKhaiMac
    INTO v_ngay_khaimac
    FROM MuaGiai
    WHERE MaMG = :NEW.MaMG;

-- Lấy ngày sinh của cầu thủ
    SELECT NgaySinh
    INTO v_ngay_sinh
    FROM CauThu
    WHERE MaCT = :NEW.MaCT;

-- Kiểm tra ngày sinh không NULL và không trong tương lai
    IF v_ngay_sinh IS NULL THEN
        RAISE_APPLICATION_ERROR(-20013, 'Ngày sinh của cầu thủ không được để trống.');
    END IF;
    IF v_ngay_sinh > v_ngay_khaimac THEN
        RAISE_APPLICATION_ERROR(-20014, 'Ngày sinh không được lớn hơn ngày khai mạc mùa giải.');
    END IF;

    -- Lấy quy định tuổi từ QuyDinh (nếu có)
    BEGIN
        SELECT NVL(TUOITOITHIEU, 16), NVL(TUOITOIDA, 40)
        INTO v_tuoi_toithieu, v_tuoi_toida
        FROM QuyDinh
        WHERE MaMG = :NEW.MaMG;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_tuoi_toithieu := 16;
            v_tuoi_toida := 40;
END;

    -- Tính tuổi chính xác hơn (cân nhắc năm và tháng)
    v_tuoi := FLOOR(MONTHS_BETWEEN(v_ngay_khaimac, v_ngay_sinh) / 12);
    IF ADD_MONTHS(v_ngay_sinh, v_tuoi * 12) > v_ngay_khaimac THEN
        v_tuoi := v_tuoi - 1;
    END IF;

    -- Kiểm tra tuổi theo quy định
    IF v_tuoi < v_tuoi_toithieu OR v_tuoi > v_tuoi_toida THEN
        RAISE_APPLICATION_ERROR(-20006,
                                'Tuổi cầu thủ phải từ ' || v_tuoi_toithieu || ' đến ' || v_tuoi_toida ||
                                '. Tuổi hiện tại: ' || v_tuoi);
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20011,
                                'Không tìm thấy thông tin mùa giải cho MaMG: ' || :NEW.MaMG ||
                                ' hoặc cầu thủ cho MaCT: ' || :NEW.MaCT);
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20015, 'Lỗi không xác định khi kiểm tra tuổi: ' || SQLERRM);
END;
/

--------------------CHECK PHUT GHI BAN TOI DA----------------
CREATE OR REPLACE TRIGGER trg_check_phut_ghi_ban_toi_da
    BEFORE INSERT OR UPDATE OF PhutGhiBan ON BANTHANG
    FOR EACH ROW
DECLARE
v_phut_toi_da NUMBER;
    v_ma_mg NUMBER;
BEGIN
    -- Lấy MaMG từ TranDau qua VongDau
SELECT v.MaMG
INTO v_ma_mg
FROM VongDau v
         JOIN TranDau t ON v.MaVD = t.MaVD
WHERE t.MaTD = :NEW.MaTD;

-- Lấy giá trị PhutGhiBanToiDa từ QUYDINH
BEGIN
SELECT NVL(PhutGhiBanToiDa, 90)
INTO v_phut_toi_da
FROM QUYDinh
WHERE MaMG = v_ma_mg;
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_phut_toi_da := 90; -- Giá trị mặc định nếu không tìm thấy
END;

    -- Kiểm tra PhutGhiBan
    IF :NEW.PhutGhiBan IS NULL THEN
        RAISE_APPLICATION_ERROR(-20016, 'Thời điểm ghi bàn không được để trống.');
END IF;
    IF :NEW.PhutGhiBan < 0 THEN
        RAISE_APPLICATION_ERROR(-20017, 'Thời điểm ghi bàn không được âm.');
END IF;
    IF :NEW.PhutGhiBan > v_phut_toi_da THEN
        RAISE_APPLICATION_ERROR(-20018,
                                'Thời điểm ghi bàn (' || :NEW.PhutGhiBan || ') vượt quá giới hạn tối đa (' || v_phut_toi_da || ') phút.');
END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20019,
                                'Không tìm thấy thông tin mùa giải hoặc trận đấu cho MaTD: ' || :NEW.MaTD);
WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20020, 'Lỗi không xác định khi kiểm tra thời điểm ghi bàn: ' || SQLERRM);
END trg_check_phut_ghi_ban_toi_da;
/
-------------------Check So AO-----------------------
CREATE OR REPLACE TRIGGER trg_check_soao_unique_clb
    BEFORE INSERT ON CauThu
    FOR EACH ROW
DECLARE
v_count NUMBER;
BEGIN
    -- Kiểm tra xem số áo đã tồn tại cho cùng MaCLB chưa
SELECT COUNT(*)
INTO v_count
FROM CauThu
WHERE MaCLB = :NEW.MaCLB
  AND SoAo = :NEW.SoAo
  AND MaCT != :NEW.MaCT; -- Loại trừ chính bản ghi đang chèn (nếu là UPDATE)

-- Nếu số áo đã tồn tại, ném lỗi
IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20033, 'Số áo ' || :NEW.SoAo || ' đã tồn tại cho CLB có MaCLB = ' || :NEW.MaCLB || '.');
END IF;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20034, 'Lỗi khi kiểm tra số áo: ' || SQLERRM);
END trg_check_soao_unique_clb;
/
----------------------------------------------------------**********PROCEDURE**********---------------------
----------------------------------------------------Tournament------------------------------------------
-----------------DeleteTournament-------------------
CREATE OR REPLACE PROCEDURE DeleteTournament(
    p_maMG IN NUMBER
)
AS
    v_count NUMBER;
BEGIN
    -- Check if the tournament exists
    SELECT COUNT(*) INTO v_count FROM QuyDinh WHERE MaMG = p_maMG;
    IF v_count != 0 THEN
        DELETE FROM QuyDinh WHERE MaMG = p_maMG;
    END IF;

    SELECT COUNT(*) INTO v_count FROM THUTU_UUTIEN WHERE MaMG = p_maMG;
    IF v_count != 0 THEN
        DELETE FROM THUTU_UUTIEN WHERE MaMG = p_maMG;
    END IF;

    -- Check if the tournament has any matches
    SELECT COUNT(*) INTO v_count FROM TranDau WHERE MaVD IN (SELECT MaVD FROM VongDau WHERE MaMG = p_maMG);
    IF v_count = 0 THEN
        -- If no matches, delete all related data
        DELETE FROM BANGXEPHANG_CLB WHERE MaMG = p_maMG;
        DELETE FROM BANGXEPHANG_BANTHANG WHERE MaMG = p_maMG;
        DELETE FROM CAUTHU_THAMGIAMUAGIAI WHERE MaMG = p_maMG;
        DELETE FROM CLB_THAMGIAMUAGIAI WHERE MaMG = p_maMG;
        DELETE FROM VongDau WHERE MaMG = p_maMG;
        DELETE FROM KetQuaTD WHERE MaTD IN (SELECT MaTD FROM TranDau WHERE MaVD IN (SELECT MaVD FROM VongDau WHERE MaMG = p_maMG));
        DELETE FROM BanThang WHERE MaTD IN (SELECT MaTD FROM TranDau WHERE MaVD IN (SELECT MaVD FROM VongDau WHERE MaMG = p_maMG));
    END IF;
    -- Delete the tournament itself
    DELETE FROM MuaGiai WHERE MaMG = p_maMG;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20011, 'Error deleting tournament: ' || SQLERRM);
END DeleteTournament;
/
----------------------------------------------------BXH_CLB------------------------------
-----------------InsertInitialRanking--------------
CREATE OR REPLACE PROCEDURE InsertInitialRanking(
    p_maMG IN NUMBER,
    p_maCLB IN NUMBER
)
AS
    v_count NUMBER;
BEGIN
    -- Kiểm tra xem bản ghi đã tồn tại chưa (dựa trên MaMG và MaCLB)
SELECT COUNT(*)
INTO v_count
FROM BANGXEPHANG_CLB
WHERE MaMG = p_maMG AND MaCLB = p_maCLB;

IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'Bản ghi với MaMG: ' || p_maMG || ' và MaCLB: ' || p_maCLB || ' đã tồn tại.');
END IF;

    -- Chèn bản ghi mới với các giá trị khởi tạo
INSERT INTO BANGXEPHANG_CLB (MaMG, MaCLB, SoTran, Thang, Hoa, Thua, Diem, HieuSo, Hang)
VALUES (p_maMG, p_maCLB, 0, 0, 0, 0, 0, 0, 0);

-- Commit giao dịch
COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20010, 'Lỗi khi chèn dữ liệu vào BANGXEPHANG_CLB: ' || SQLERRM);
END InsertInitialRanking;
/
-----------------RecalculateRankingPositions----
CREATE OR REPLACE PROCEDURE RecalculateRankingPositions(
    p_maMG IN NUMBER
)
AS
    v_order_by_clause VARCHAR2(1000) := '';
    v_found BOOLEAN := FALSE;
BEGIN
    -- Kiểm tra xem có dữ liệu trong THUTU_UUTIEN không và xây dựng chuỗi ORDER BY
    FOR priority_rec IN (
        SELECT TieuChi, DoUuTien
        FROM THUTU_UUTIEN
        WHERE MaMG = p_maMG
        ORDER BY DoUuTien
        )
        LOOP
            v_found := TRUE;
            IF v_order_by_clause IS NOT NULL THEN
                v_order_by_clause := v_order_by_clause || ', ';
            END IF;
            v_order_by_clause := v_order_by_clause || priority_rec.TieuChi || ' DESC';
        END LOOP;

    -- Nếu không có dữ liệu trong THUTU_UUTIEN, sắp xếp mặc định theo Diem và HieuSo
    IF NOT v_found THEN
        v_order_by_clause := 'Diem DESC, HieuSo DESC, Thang DESC, Hoa DESC, Thua ASC, SoTran DESC';
    END IF;

    -- Sử dụng MERGE để cập nhật thứ hạng dựa trên thứ tự đã sắp xếp
    EXECUTE IMMEDIATE '
        MERGE INTO BANGXEPHANG_CLB bxh
        USING (
            SELECT MaCLB,
                   ROW_NUMBER() OVER (ORDER BY ' || v_order_by_clause || ') AS new_hang
            FROM BANGXEPHANG_CLB
            WHERE MaMG = :1
        ) ranked
        ON (bxh.MaMG = :2 AND bxh.MaCLB = ranked.MaCLB)
        WHEN MATCHED THEN
            UPDATE SET bxh.Hang = ranked.new_hang
    '
        USING p_maMG, p_maMG;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        -- Nếu không có dữ liệu trong THUTU_UUTIEN, đã xử lý ở trên, nên không cần ném lỗi
        NULL;
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20004, 'Lỗi khi tính lại thứ hạng: ' || SQLERRM);
END RecalculateRankingPositions;
/
-----------------UpdateRanking--------------
CREATE OR REPLACE PROCEDURE UpdateRanking(
    p_maTD IN NUMBER,
    p_diemCLB1_old IN NUMBER DEFAULT 0,
    p_diemCLB2_old IN NUMBER DEFAULT 0,
    p_diemCLB1_new IN NUMBER DEFAULT 0,
    p_diemCLB2_new IN NUMBER DEFAULT 0,
    p_action IN VARCHAR2 DEFAULT 'INSERT'
)
AS
    TYPE num_arr IS VARRAY(2) OF NUMBER;
    v_maCLB num_arr;
    v_diem_old num_arr;
    v_diem_new num_arr;
    v_maMG NUMBER;
    v_diemThang NUMBER;
    v_diemHoa NUMBER;
    v_diemThua NUMBER;
    v_diem_old_calc num_arr := num_arr(0, 0);
    v_diem_new_calc num_arr := num_arr(0, 0);
BEGIN
    v_maCLB := num_arr(0, 0);
    -- Get match and season info with exception handling
    BEGIN
        SELECT MaCLB1, MaCLB2, MaMG
        INTO v_maCLB(1), v_maCLB(2), v_maMG
        FROM TranDau td
                 JOIN VongDau vd ON td.MaVD = vd.MaVD
        WHERE td.MaTD = p_maTD;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20004, 'Không tìm thấy trận đấu với MaTD = ' || p_maTD);
    END;

    -- Get scoring rules with default values
    BEGIN
        SELECT NVL(DiemThang, 3), NVL(DiemHoa, 1), NVL(DiemThua, 0)
        INTO v_diemThang, v_diemHoa, v_diemThua
        FROM QuyDinh
        WHERE MaMG = v_maMG;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_diemThang := 3;
            v_diemHoa := 1;
            v_diemThua := 0;
    END;

    -- Initialize arrays
    v_diem_old := num_arr(NVL(p_diemCLB1_old, 0), NVL(p_diemCLB2_old, 0));
    v_diem_new := num_arr(NVL(p_diemCLB1_new, 0), NVL(p_diemCLB2_new, 0));

    -- Calculate points for old and new scores
    FOR i IN 1..2 LOOP
            -- Calculate old points
            IF p_action='INSERT' THEN
                v_diem_old_calc(i) := 0;
            ELSE
                IF v_diem_old(i) IS NOT NULL AND v_diem_old(3-i) IS NOT NULL THEN
                    IF v_diem_old(i) > v_diem_old(3-i) THEN
                        v_diem_old_calc(i) := v_diemThang;
                    ELSIF v_diem_old(i) = v_diem_old(3-i) THEN
                        v_diem_old_calc(i) := v_diemHoa;
                    ELSIF v_diem_old(i) < v_diem_old(3-i) THEN
                        v_diem_old_calc(i) := v_diemThua;
                    END IF;
                END IF;
            END IF;

            -- Calculate new points
            IF p_action='DELETE' THEN
                v_diem_new_calc(i) := 0;
            ELSE
                IF v_diem_new(i) IS NOT NULL AND v_diem_new(3-i) IS NOT NULL THEN
                    IF v_diem_new(i) > v_diem_new(3-i) THEN
                        v_diem_new_calc(i) := v_diemThang;
                    ELSIF v_diem_new(i) = v_diem_new(3-i) THEN
                        v_diem_new_calc(i) := v_diemHoa;
                    ELSIF v_diem_new(i) < v_diem_new(3-i) THEN
                        v_diem_new_calc(i) := v_diemThua;
                    END IF;
                END IF;
            END IF;
    END LOOP;

    FOR i IN 1..2 LOOP
        IF p_action = 'INSERT' THEN
            UPDATE BANGXEPHANG_CLB
            SET SoTran = SoTran + 1,
                Thang = Thang + (CASE WHEN v_diem_new_calc(i)=v_diemThang THEN 1 ELSE 0 END),
                Hoa = Hoa + (CASE WHEN v_diem_new_calc(i)=v_diemHoa THEN 1 ELSE 0 END),
                Thua = Thua + (CASE WHEN v_diem_new_calc(i)=v_diemThua THEN 1 ELSE 0 END),
                HieuSo = HieuSo + (NVL(v_diem_new(i), 0) - NVL(v_diem_new(3-i), 0)),
                Diem = Diem + NVL(v_diem_new_calc(i), 0)
            WHERE MaMG = v_maMG AND MaCLB = v_maCLB(i);
        ELSIF p_action = 'DELETE' THEN
            UPDATE BANGXEPHANG_CLB
            SET SoTran = SoTran - 1,
                Thang = Thang - (CASE WHEN v_diem_old_calc(i)=v_diemThang THEN 1 ELSE 0 END),
                Hoa = Hoa - (CASE WHEN v_diem_old_calc(i)=v_diemHoa THEN 1 ELSE 0 END),
                Thua = Thua - (CASE WHEN v_diem_old_calc(i)=v_diemThua THEN 1 ELSE 0 END),
                HieuSo = HieuSo - (NVL(v_diem_old(i), 0) - NVL(v_diem_old(3-i), 0)),
                Diem = Diem - NVL(v_diem_old_calc(i), 0)
            WHERE MaMG = v_maMG AND MaCLB = v_maCLB(i);
        ELSIF p_action = 'UPDATE' THEN
            UPDATE BANGXEPHANG_CLB
            SET SoTran = SoTran,
                Thang = Thang + (CASE WHEN v_diem_new_calc(i) > v_diem_new_calc(3-i) THEN 1 WHEN v_diem_old_calc(i) > v_diem_old_calc(3-i) THEN -1 ELSE 0 END),
                Hoa = Hoa + (CASE WHEN v_diem_new_calc(i) = v_diem_new_calc(3-i) AND v_diem_new_calc(i) IS NOT NULL AND p_action!='DELETE' THEN 1
                                  WHEN v_diem_old_calc(i) = v_diem_old_calc(3-i) AND v_diem_old_calc(i)  IS NOT NULL AND p_action!='INSERT' THEN -1
                                  ELSE 0 END),
                Thua = Thua + (CASE WHEN v_diem_new_calc(i) < v_diem_new_calc(3-i) THEN 1 WHEN v_diem_old_calc(i) < v_diem_old_calc(3-i) THEN -1 ELSE 0 END),
                HieuSo = HieuSo + (NVL(v_diem_new(i), 0) - NVL(v_diem_new(3-i), 0)) - (NVL(v_diem_old(i), 0) - NVL(v_diem_old(3-i), 0)),
                Diem = Diem + NVL(v_diem_new_calc(i), 0) - NVL(v_diem_old_calc(i), 0)
            WHERE MaMG = v_maMG AND MaCLB = v_maCLB(i);
        END IF;
    END LOOP;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20003, 'Error in UpdateRanking: ' || SQLERRM);
END UpdateRanking;
/
-----------------Trigger UpdateBXH_OnKetQuaTD--------------
CREATE OR REPLACE TRIGGER UpdateBXH_OnKetQuaTD
    AFTER INSERT OR UPDATE OR DELETE ON KetQuaTD
    FOR EACH ROW
DECLARE
    v_action VARCHAR2(10);
    v_maTD NUMBER;
    v_maMG NUMBER;
BEGIN
    -- Xác định hành động
    IF INSERTING THEN
        v_maTD := :NEW.MaTD;
        v_action := 'INSERT';
        UpdateRanking(:NEW.MaTD, 0, 0, :NEW.DiemCLB1, :NEW.DiemCLB2, v_action);
    ELSIF UPDATING THEN
        v_maTD := :NEW.MaTD;
        v_action := 'UPDATE';
        UpdateRanking(:NEW.MaTD, :OLD.DiemCLB1, :OLD.DiemCLB2, :NEW.DiemCLB1, :NEW.DiemCLB2, v_action);
    ELSIF DELETING THEN
        v_maTD := :OLD.MaTD;
        v_action := 'DELETE';
        UpdateRanking(:OLD.MaTD, :OLD.DiemCLB1, :OLD.DiemCLB2, 0, 0, v_action);
    END IF;

    -- Lấy MaMG và gọi RecalculateRankingPositions
    BEGIN
        SELECT MaMG
        INTO v_maMG
        FROM TranDau td JOIN VongDau VD on VD.MaVD = td.MaVD
        WHERE td.MaTD = v_maTD;
        RecalculateRankingPositions(v_maMG);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20002, 'Không tìm thấy mùa giải cho trận đấu MaTD = ' || v_maTD);
    END;

EXCEPTION
    WHEN OTHERS THEN
        -- Ghi log lỗi chi tiết
        RAISE_APPLICATION_ERROR(-20001, 'Lỗi trong UpdateBXH_OnKetQuaTD trigger: ' || SQLERRM || ' tại MaTD = ' || NVL(v_maTD, 'NULL'));
END UpdateBXH_OnKetQuaTD;
/

----------------------------------------------------MUAGIAI------------------------------
-----------InsertDefaultRules---------------------------------
CREATE OR REPLACE PROCEDURE InsertQuyDinhForMuaGiai(
    p_maMG IN NUMBER
)
AS
    TYPE arr_tieuchi IS VARRAY(6) OF NVARCHAR2(50);
    arr arr_tieuchi := arr_tieuchi('Diem', 'HieuSo', 'Thang', 'Hoa', 'Thua', 'SoTran');
    v_douutien NUMBER := 1;
    v_count_quydinh NUMBER;
    v_count_thutu NUMBER;
BEGIN
    -- Check if QuyDinh exists
    SELECT COUNT(*) INTO v_count_quydinh FROM QuyDinh WHERE MaMG = p_maMG;
    IF v_count_quydinh = 0 THEN
        INSERT INTO QuyDinh (MaMG) VALUES (p_maMG);
    END IF;

    -- Check if THUTU_UUTIEN exists
    SELECT COUNT(*) INTO v_count_thutu FROM THUTU_UUTIEN WHERE MaMG = p_maMG;
    IF v_count_thutu = 0 THEN
        FOR i IN 1 .. arr.COUNT LOOP
                INSERT INTO THUTU_UUTIEN(MaMG, TieuChi, DoUuTien)
                VALUES (p_maMG, arr(i), v_douutien);
                v_douutien := v_douutien + 1;
            END LOOP;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20035, 'Lỗi khi thêm quy định cho mùa giải: ' || SQLERRM);
END InsertQuyDinhForMuaGiai;
/

----------------------------------------------------MATCH------------------------------
------------------GetUpComingMatchesByCondition-------------------
CREATE OR REPLACE PROCEDURE GetUpComingMatchesByCondition(
    p_condition IN VARCHAR2 DEFAULT NULL,
    p_result OUT SYS_REFCURSOR
)
AS
    v_sql VARCHAR2(4000);
BEGIN
    -- Xây dựng truy vấn cơ bản
    v_sql := 'SELECT
            td.MaTD AS ID,
            v.TenVD AS TenVD,
            c1.TenCLB AS TenCLB1,
            c1.LogoCLB AS LogoCLB1,
            c2.TenCLB AS TenCLB2,
            c2.LogoCLB AS LogoCLB2,
            td.ThoiGian AS ThoiGian,
            s.TenSan AS SanThiDau,
            m.TenMG AS TenMuaGiai,
            kq.DiemCLB1 as Score1,
            kq.DiemCLB2 as Score2
        FROM TranDau td
        JOIN VongDau v ON td.MaVD = v.MaVD
        JOIN CLB c1 ON td.MaCLB1 = c1.MaCLB
        JOIN CLB c2 ON td.MaCLB2 = c2.MaCLB
        JOIN SAN s ON td.MaSan = s.MaSan
        JOIN MuaGiai m ON v.MaMG = m.MaMG
        LEFT JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
        WHERE td.ThoiGian >= TRUNC(SYSDATE)';

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
END GetUpComingMatchesByCondition;
/
----------------GetUpComingMatches---------
CREATE OR REPLACE PROCEDURE GetUpcomingMatches(
    p_result OUT SYS_REFCURSOR
)
AS
BEGIN
    -- Mở cursor với truy vấn lấy các trận đấu có ngày thi đấu >= ngày hiện tại
OPEN p_result FOR
SELECT
    td.MaTD AS ID,
    v.TenVD AS TenVD,
    c1.TenCLB AS TenCLB1,
    c1.LogoCLB AS LogoCLB1,
    c2.TenCLB AS TenCLB2,
    c2.LogoCLB AS LogoCLB2,
    td.ThoiGian AS ThoiGian,
    s.TenSan AS SanThiDau,
    m.TenMG AS TenMuaGiai,
    kq.DiemCLB1 as Score1,
    kq.DiemCLB2 as Score2
FROM TranDau td
         JOIN VongDau v ON td.MaVD = v.MaVD
         JOIN CLB c1 ON td.MaCLB1 = c1.MaCLB
         JOIN CLB c2 ON td.MaCLB2 = c2.MaCLB
         JOIN SAN s ON td.MaSan = s.MaSan
         JOIN MuaGiai m ON v.MaMG = m.MaMG
         LEFT JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
WHERE td.ThoiGian >= TRUNC(SYSDATE)-- Lấy các trận từ ngày hiện tại trở đi
ORDER BY td.ThoiGian;
END GetUpcomingMatches;
/
----------------GetResultedMatches---------
CREATE OR REPLACE PROCEDURE GetResultedMatches(
    p_result OUT SYS_REFCURSOR
)
AS
BEGIN
    -- Mở cursor với truy vấn lấy các trận đấu có ngày thi đấu >= ngày hiện tại
OPEN p_result FOR
SELECT
    td.MaTD AS ID,
    v.TenVD AS TenVD,
    c1.TenCLB AS TenCLB1,
    c1.LogoCLB AS LogoCLB1,
    c2.TenCLB AS TenCLB2,
    c2.LogoCLB AS LogoCLB2,
    td.ThoiGian AS ThoiGian,
    s.TenSan AS SanThiDau,
    m.TenMG AS TenMuaGiai,
    kq.DiemCLB1 as Score1,
    kq.DiemCLB2 as Score2
FROM TranDau td
         JOIN VongDau v ON td.MaVD = v.MaVD
         JOIN CLB c1 ON td.MaCLB1 = c1.MaCLB
         JOIN CLB c2 ON td.MaCLB2 = c2.MaCLB
         JOIN SAN s ON td.MaSan = s.MaSan
         JOIN MuaGiai m ON v.MaMG = m.MaMG
         JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
ORDER BY td.ThoiGian DESC;

END GetResultedMatches;
/
----------------GetPendingMatches---------
CREATE OR REPLACE PROCEDURE GetPendingMatches(
    p_result OUT SYS_REFCURSOR
)
AS
BEGIN
    -- Mở cursor với truy vấn lấy các trận đấu có ngày thi đấu <= ngày hiện tại
OPEN p_result FOR
SELECT
    td.MaTD AS ID,
    c1.TenCLB AS TenCLB1,
    c1.LogoCLB AS LogoCLB1,
    c2.TenCLB AS TenCLB2,
    c2.LogoCLB AS LogoCLB2,
    td.ThoiGian AS ThoiGian,
    s.TenSan AS SanThiDau,
    m.TenMG AS TenMuaGiai,
    v.TenVD AS TenVD,
    kq.DiemCLB1 as Score1,
    kq.DiemCLB2 as Score2
FROM TranDau td
         JOIN VongDau v ON td.MaVD = v.MaVD
         JOIN CLB c1 ON td.MaCLB1 = c1.MaCLB
         JOIN CLB c2 ON td.MaCLB2 = c2.MaCLB
         JOIN SAN s ON td.MaSan = s.MaSan
         JOIN MuaGiai m ON v.MaMG = m.MaMG
         LEFT JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
WHERE td.ThoiGian <= SYSDATE
  AND kq.MaTD IS NULL                -- Chưa có kết quả
ORDER BY td.ThoiGian ;

END GetPendingMatches;
/
commit;
-----------------InsertMatch--------
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
WHERE LOWER(TenMG) = LOWER(p_tenMuaGiai);

-- 2. Tìm MaVD từ MaMG
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
Select NVL(MAX(MaTD), 0) + 1 INTO v_maTD FROM TranDau;

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
------------------InsertMatchResult---------------------
CREATE OR REPLACE PROCEDURE InsertMatchResult(
    p_maTD IN NUMBER,
    p_diemCLB1 IN NUMBER,
    p_diemCLB2 IN NUMBER
)
AS
    v_count NUMBER;
BEGIN
    -- Kiểm tra xem MaTD đã tồn tại chưa (khóa chính không được trùng)
    SELECT COUNT(*) INTO v_count
    FROM KetQuaTD
    WHERE MaTD = p_maTD;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Kết quả trận đấu với MaTD = ' || p_maTD || ' đã tồn tại.');
    END IF;

    -- Kiểm tra giá trị điểm hợp lệ (không âm)
    IF p_diemCLB1 < 0 OR p_diemCLB2 < 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Điểm số không được âm.');
    END IF;

    -- Thêm bản ghi mới
    INSERT INTO KetQuaTD (MaTD, DiemCLB1, DiemCLB2)
    VALUES (p_maTD, p_diemCLB1, p_diemCLB2);
    COMMIT;
    UpdateRanking(p_maTD);
    -- Commit giao dịch
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20003, 'Lỗi khi thêm kết quả trận đấu: ' || SQLERRM);
END InsertMatchResult;
/
------------------DeleteMatchResult---------------------
CREATE OR REPLACE PROCEDURE DeleteMatchResultAndGoals(
    p_maTD IN NUMBER
)
AS
    v_count NUMBER;
BEGIN
    -- Kiểm tra xem MaTD có tồn tại trong KetQuaTD không
SELECT COUNT(*) INTO v_count
FROM KetQuaTD
WHERE MaTD = p_maTD;

IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Kết quả trận đấu với MaTD = ' || p_maTD || ' không tồn tại.');
END IF;

    -- Xóa tất cả bàn thắng liên quan đến trận đấu trong BANTHANG
DELETE FROM BANTHANG
WHERE MaTD = p_maTD;

-- Xóa kết quả trận đấu trong KetQuaTD
DELETE FROM KetQuaTD
WHERE MaTD = p_maTD;

COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20002, 'Lỗi khi xóa kết quả trận đấu và bàn thắng: ' || SQLERRM);
END DeleteMatchResultAndGoals;
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

    -- Kiểm tra và xóa kết quả trận đấu (KetQuaTD) nếu tồn tại
SELECT COUNT(*) INTO v_count
FROM KetQuaTD
WHERE MaTD = p_maTD;

IF v_count > 0 THEN
        DeleteMatchResultAndGoals(p_maTD);
END IF;

    -- Xóa bản ghi trong TranDau
DELETE FROM TranDau
WHERE MaTD = p_maTD;

-- Commit giao dịch
COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20006, 'Lỗi khi xóa trận đấu: ' || SQLERRM);
END DeleteMatch;
/
----------------------------------------------------BXH_BANTHANG------------------------------
--------------UpdateBXH_BanThang------
CREATE OR REPLACE PROCEDURE UpdateBXH_BanThang(
    p_maMG IN NUMBER
)
AS
BEGIN
    -- Cập nhật hoặc chèn dữ liệu vào BANGXEPHANG_BANTHANG
MERGE INTO BANGXEPHANG_BANTHANG bxh
    USING (
        SELECT
            cclb.MaMG,bt.MaCT,COUNT(*) AS SoBanThang,
            SUM(CASE WHEN lbt.TenLoaiBT LIKE '%Phạt đền%' THEN 1 ELSE 0 END) AS Penalty
        FROM BANTHANG bt
                 JOIN CAUTHU_THAMGIAMUAGIAI cclb ON bt.MaCT = cclb.MaCT AND cclb.MaMG = 1
                 JOIN LoaiBanThang lbt ON bt.MaLoaiBT = lbt.MaLoaiBT
        GROUP BY cclb.MaMG, bt.MaCT
    ) src
    ON (bxh.MaMG = src.MaMG AND bxh.MaCT = src.MaCT)
    WHEN MATCHED THEN
        UPDATE SET
            bxh.SoBanThang = src.SoBanThang,
            bxh.Penalty = src.Penalty
    WHEN NOT MATCHED THEN
        INSERT (MaMG, MaCT, SoBanThang, Penalty, XepHang)
            VALUES (src.MaMG, src.MaCT, src.SoBanThang, src.Penalty, NULL);

    -- Delete records not present in src
    DELETE FROM BANGXEPHANG_BANTHANG bxh
    WHERE bxh.MaMG = p_maMG
      AND NOT EXISTS (
        SELECT 1
        FROM (
                 SELECT cclb.MaMG, bt.MaCT
                 FROM BANTHANG bt
                          JOIN CAUTHU_THAMGIAMUAGIAI cclb ON bt.MaCT = cclb.MaCT AND cclb.MaMG = p_maMG
                 GROUP BY cclb.MaMG, bt.MaCT
             ) src
        WHERE bxh.MaMG = src.MaMG AND bxh.MaCT = src.MaCT
    );
-- Cập nhật thứ hạng (XepHang)
MERGE INTO BANGXEPHANG_BANTHANG bxh
    USING (
        SELECT MaMG, MaCT,
               ROW_NUMBER() OVER (ORDER BY SoBanThang DESC, MaCT) AS new_xephang
        FROM BANGXEPHANG_BANTHANG
        WHERE MaMG = p_maMG
    ) src
    ON (bxh.MaMG = src.MaMG AND bxh.MaCT = src.MaCT)
    WHEN MATCHED THEN
        UPDATE SET bxh.XepHang = src.new_xephang;

-- Lưu thay đổi
COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20007, 'Lỗi khi cập nhật bảng xếp hạng bàn thắng: ' || SQLERRM);
END UpdateBXH_BanThang;
/

----------------------------------------------------GOAL------------------------------
-----------------InsertGoal--------------
CREATE OR REPLACE PROCEDURE InsertGoal(
    p_maCT IN NUMBER,
    p_maTD IN NUMBER,
    p_phutGhiBan IN NUMBER,
    p_maLoaiBT IN NUMBER,
    p_maBT OUT NUMBER
)
AS
    v_maBT NUMBER;
    v_MaMG NUMBER;
BEGIN
    -- Sinh mã MaBT mới = MAX(MaBT) + 1
    SELECT NVL(MAX(MaBT), 0) + 1 INTO v_maBT
    FROM BANTHANG;

    -- Thêm bản ghi mới với MaBT vừa sinh
    INSERT INTO BANTHANG (MaBT, MaCT, MaTD, PhutGhiBan, MaLoaiBT)
    VALUES (v_maBT, p_maCT, p_maTD, p_phutGhiBan, p_maLoaiBT);
    COMMIT;
    --Cập nhật BXH_BANTHANG
    Select vd.MaMG INTO v_MaMG
    FROM TRANDAU td JOIN VONGDAU vd ON td.MaVD=vd.MaVD
    WHERE td.MaTD=p_maTD;

    UpdateBXH_BanThang(v_MaMG);
    -- Commit giao dịch
    COMMIT;
    -- Trả về MaBT vừa tạo
    p_maBT := v_maBT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20006, 'Lỗi khi thêm bàn thắng: ' || SQLERRM);
END InsertGoal;
/
-----------------UpdateGoal--------------
CREATE OR REPLACE PROCEDURE UpdateGoal(
    p_maBT IN NUMBER,
    p_maCT IN NUMBER,
    p_maTD IN NUMBER,
    p_phutGhiBan IN NUMBER,
    p_maLoaiBT IN NUMBER
)
AS
    v_maMG NUMBER;
BEGIN
UPDATE BANTHANG
SET MaCT = p_maCT,
    MaTD = p_maTD,
    PhutGhiBan = p_phutGhiBan,
    MaLoaiBT = p_maLoaiBT
WHERE MaBT = p_maBT;
COMMIT;

-- Gọi procedure để cập nhật bảng xếp hạng
SELECT vd.MaMG INTO v_maMG
FROM TranDau td
         JOIN VongDau vd ON td.MaVD = vd.MaVD
WHERE td.MaTD = p_maTD;
-- Lưu thay đổi
UpdateBXH_BanThang(v_maMG);
COMMIT;


EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20010, 'Lỗi khi cập nhật bàn thắng: ' || SQLERRM);
END UpdateGoal;
/
-----------------DeleteGoal--------------
CREATE OR REPLACE PROCEDURE DeleteGoal(
    p_maBT IN NUMBER
)
AS
    v_maMG NUMBER;
    v_maTD NUMBER;
BEGIN
    -- Lấy MaTD từ BANTHANG để xác định MaMG
SELECT MaTD INTO v_maTD
FROM BANTHANG
WHERE MaBT = p_maBT;

-- Lấy MaMG từ TranDau và VongDau
SELECT vd.MaMG INTO v_maMG
FROM TranDau td
         JOIN BanThang bt ON td.MaTD=bt.MaTD
         JOIN VongDau vd ON td.MaVD = vd.MaVD
WHERE bt.MaBT=p_maBT;

-- Xóa bản ghi trong BANTHANG
DELETE FROM BANTHANG
WHERE MaBT = p_maBT;
    COMMIT;

-- Gọi procedure để cập nhật bảng xếp hạng
UpdateBXH_BanThang(v_maMG);

    -- Lưu thay đổi
COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20012, 'Lỗi khi xóa bàn thắng: ' || SQLERRM);
END DeleteGoal;
/
----------------------------------------------------STADIUM------------------------------
CREATE OR REPLACE PROCEDURE InsertSan(
    p_tenSan IN NVARCHAR2,
    p_diaChi IN NVARCHAR2,
    p_sucChua IN NUMBER,
    p_maSan OUT NUMBER
)
AS
    v_maxMaSan NUMBER;
BEGIN
    -- Lấy giá trị lớn nhất của MaSan hiện tại và tăng 1
SELECT NVL(MAX(MaSan), 0) + 1 INTO v_maxMaSan
FROM SAN;

-- Chèn dữ liệu vào bảng SAN
INSERT INTO SAN (MaSan, TenSan, DiaChi, SucChua)
VALUES (v_maxMaSan, p_tenSan, p_diaChi, p_sucChua);

-- Trả về giá trị MaSan vừa tạo
p_maSan := v_maxMaSan;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20027, 'Lỗi khi thêm sân: ' || SQLERRM);
END InsertSan;
/
----------------------------------------------------CLUB------------------------------
CREATE OR REPLACE PROCEDURE InsertCLB(
    p_tenCLB IN NVARCHAR2,
    p_logoCLB IN NVARCHAR2,
    p_sanNha IN NUMBER,
    p_tenHLV IN NVARCHAR2,
    p_email IN NVARCHAR2,
    p_maCLB OUT NUMBER
)
AS
    v_maxMaCLB NUMBER;
BEGIN
    -- Lấy giá trị lớn nhất của MaCLB hiện tại và tăng 1
SELECT NVL(MAX(MaCLB), 0) + 1 INTO v_maxMaCLB
FROM CLB;

-- Chèn dữ liệu vào bảng CLB
INSERT INTO CLB (MaCLB, TenCLB, LogoCLB, SanNha, TenHLV, Email)
VALUES (v_maxMaCLB, p_tenCLB, p_logoCLB, p_sanNha, p_tenHLV, p_email);

-- Trả về giá trị MaCLB vừa tạo
p_maCLB := v_maxMaCLB;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20028, 'Lỗi khi thêm CLB: ' || SQLERRM);
END InsertCLB;
/
----------------------------------------------------PLAYER----------------------------
CREATE OR REPLACE PROCEDURE InsertPlayer(
    p_tenCT IN NVARCHAR2,
    p_ngaySinh IN DATE,
    p_quocTich IN NVARCHAR2,
    p_avatar IN NVARCHAR2,
    p_soAo IN NUMBER,
    p_loaiCT IN NUMBER,
    p_maCLB IN NUMBER,
    p_maVT IN NUMBER,
    p_maCT OUT NUMBER
)
AS
    v_maxMaCT NUMBER;
BEGIN
    -- Lấy giá trị lớn nhất của MaCT hiện tại và tăng 1
SELECT NVL(MAX(MaCT), 0) + 1 INTO v_maxMaCT
FROM CauThu;

-- Chèn dữ liệu vào bảng CauThu
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT)
VALUES (v_maxMaCT, p_tenCT, p_ngaySinh, p_quocTich,p_avatar, p_soAo, p_loaiCT, p_maCLB, p_maVT);

-- Trả về giá trị MaCT vừa tạo
p_maCT := v_maxMaCT;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20029, 'Lỗi khi thêm cầu thủ: ' || SQLERRM);
END InsertPlayer;
/
----------------------------------------------------REGISTRATION------------------------------
----------------------RegisterClubForSeason--------------------
CREATE OR REPLACE PROCEDURE RegisterforSeason(
    p_maCLB IN NUMBER,
    p_maMG IN NUMBER,
    p_player_ids IN SYS.ODCINUMBERLIST
)
AS
    v_count NUMBER;
    v_foreign_count NUMBER;
    v_min NUMBER;
    v_max NUMBER;
    v_max_foreign NUMBER;
BEGIN
    -- Insert into CLB_THAMGIAMUAGIAI
    -- Kiểm tra xem CLB đã đăng ký mùa giải này chưa
    SELECT COUNT(*) INTO v_count
    FROM CLB_THAMGIAMUAGIAI
    WHERE MaCLB = p_maCLB AND MaMG = p_maMG;

    IF v_count <= 0 THEN
        -- Đăng ký CLB vào mùa giải (chèn bản ghi mới)
        INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (p_maCLB, p_maMG);

        InsertInitialRanking(p_maMG,p_maCLB);
    END IF;
    -- Insert new players if not exists
    FOR i IN 1 .. p_player_ids.COUNT LOOP
            BEGIN
                INSERT INTO CAUTHU_THAMGIAMUAGIAI (MaCT, MaCLB, MaMG)
                SELECT p_player_ids(i), p_maCLB, p_maMG
                FROM DUAL
                WHERE NOT EXISTS (
                    SELECT 1 FROM CAUTHU_THAMGIAMUAGIAI
                    WHERE MaCT = p_player_ids(i) AND MaCLB = p_maCLB AND MaMG = p_maMG
                );
            EXCEPTION
                WHEN DUP_VAL_ON_INDEX THEN NULL;
            END;
        END LOOP;

    -- Delete players not in the input list
    DELETE FROM CAUTHU_THAMGIAMUAGIAI
    WHERE MaCLB = p_maCLB
      AND MaMG = p_maMG
      AND (p_player_ids IS NULL OR MaCT NOT IN (SELECT COLUMN_VALUE FROM TABLE(p_player_ids)));

    -- Check constraints after DML
    SELECT NVL(SOCTTOITHIEU, 15), NVL(SOCTTOIDA, 22), NVL(SOCTNUOCNGOAITOIDA, 3)
    INTO v_min, v_max, v_max_foreign
    FROM QuyDinh
    WHERE MaMG = p_maMG;

    SELECT COUNT(*) INTO v_count
    FROM CAUTHU_THAMGIAMUAGIAI
    WHERE MaCLB = p_maCLB AND MaMG = p_maMG;

    SELECT COUNT(*)
    INTO v_foreign_count
    FROM CAUTHU_THAMGIAMUAGIAI ct_clb
             JOIN CauThu ct ON ct_clb.MaCT = ct.MaCT
    WHERE ct_clb.MaCLB = p_maCLB AND ct_clb.MaMG = p_maMG AND ct.LoaiCT = 1;

    IF v_count < v_min OR v_count > v_max THEN
        RAISE_APPLICATION_ERROR(-20011, 'Số lượng cầu thủ không hợp lệ');
    END IF;
    IF v_foreign_count > v_max_foreign THEN
        RAISE_APPLICATION_ERROR(-20012, 'Vượt quá số lượng cầu thủ ngoại cho phép');
    END IF;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END RegisterforSeason;
/
----------------------------------------------------CANCEL_REGISTRATION------------------------------
CREATE OR REPLACE PROCEDURE CancelClubRegistration(
    p_maCLB IN NUMBER,
    p_maMG IN NUMBER
)
AS
    v_count NUMBER;
BEGIN
    -- Check if the registration exists
    SELECT COUNT(*) INTO v_count
    FROM CLB_THAMGIAMUAGIAI
    WHERE MaCLB = p_maCLB AND MaMG = p_maMG;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20030, 'No registration found for this club in the given season.');
    END IF;

    -- Delete from BANGXEPHANG_CLB
    DELETE FROM BANGXEPHANG_CLB
    WHERE MaCLB = p_maCLB AND MaMG = p_maMG;

    -- Delete from CAUTHU_THAMGIAMUAGIAI
    DELETE FROM CAUTHU_THAMGIAMUAGIAI
    WHERE MaCLB = p_maCLB AND MaMG = p_maMG;

    -- Delete from CLB_THAMGIAMUAGIAI
    DELETE FROM CLB_THAMGIAMUAGIAI
    WHERE MaCLB = p_maCLB AND MaMG = p_maMG;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20031, 'Error when canceling club registration: ' || SQLERRM);
END CancelClubRegistration;
/
CREATE OR REPLACE PROCEDURE GetTotalGoalsOfClubInTournament(
    p_maCLB IN NUMBER,
    p_maMG IN NUMBER,
    p_totalGoals OUT NUMBER
)
AS
BEGIN
    SELECT NVL(SUM(
                       CASE
                           WHEN td.MaCLB1 = p_maCLB THEN kq.DiemCLB1
                           WHEN td.MaCLB2 = p_maCLB THEN kq.DiemCLB2
                           ELSE 0
                           END
               ), 0)
    INTO p_totalGoals
    FROM TranDau td
             JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
             JOIN VongDau vd ON td.MaVD = vd.MaVD
    WHERE vd.MaMG = p_maMG
      AND (td.MaCLB1 = p_maCLB OR td.MaCLB2 = p_maCLB);
END GetTotalGoalsOfClubInTournament;
/
CREATE OR REPLACE PROCEDURE GetTotalConcededOfClubInTournament(
    p_maCLB IN NUMBER,
    p_maMG IN NUMBER,
    p_totalConceded OUT NUMBER
)
AS
BEGIN
    SELECT NVL(SUM(
                       CASE
                           WHEN td.MaCLB1 = p_maCLB THEN kq.DiemCLB2
                           WHEN td.MaCLB2 = p_maCLB THEN kq.DiemCLB1
                           ELSE 0
                           END
               ), 0)
    INTO p_totalConceded
    FROM TranDau td
             JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
             JOIN VongDau vd ON td.MaVD = vd.MaVD
    WHERE vd.MaMG = p_maMG
      AND (td.MaCLB1 = p_maCLB OR td.MaCLB2 = p_maCLB);
END GetTotalConcededOfClubInTournament;
/
----------------------------------------------------GoalsPopUpScene------------------------------
----------------------GoalsPopUpScene--------------------
CREATE OR REPLACE PROCEDURE GetGoalsOfMatch(
    v_id IN NUMBER, v_clubname IN NVARCHAR2, p_result OUT SYS_REFCURSOR
)
AS
BEGIN
    -- Mở cursor với truy vấn lấy các trận đấu có ngày thi đấu >= ngày hiện tại
OPEN p_result FOR
SELECT
    bt.PhutGhiBan AS PhutGhiBan,
    ct.TenCT AS TenCauThu
FROM BanThang bt
         JOIN CAUTHU ct on ct.MaCT = bt.MaCT
         JOIN CLB c on c.MaCLB = ct.MaCLB
WHERE bt.MaTD = v_id
  AND c.TenCLB = v_clubname
ORDER BY PhutGHIBan;

END GetGoalsOfMatch;


select * from MuaGiai;
select * from VongDau;
select * from CLB;
-----------------------------INSERT------------------------
INSERT INTO VaiTro (MaVT, TenVaiTro) VALUES (1, 'Ban tổ chức giải đấu');
INSERT INTO VaiTro (MaVT, TenVaiTro) VALUES (2, 'Ban quản lý câu lạc bộ');
INSERT INTO VaiTro (MaVT, TenVaiTro) VALUES (3, 'Ban tổ chức thi đấu');
INSERT INTO VaiTro (MaVT, TenVaiTro) VALUES (4, 'Ban phân tích và tổng hợp kết quả');
INSERT INTO VaiTro (MaVT, TenVaiTro) VALUES (5, 'Khách hàng');
INSERT INTO VaiTro (MaVT, TenVaiTro) VALUES (6, 'admin');

INSERT INTO TaiKhoan VALUES ('0', '0', 6);
INSERT INTO TaiKhoan VALUES ('admin', '123', 1);
INSERT INTO TaiKhoan VALUES ('admin4', '1234', 2);
INSERT INTO TaiKhoan VALUES ('admin45', '12345', 3);
INSERT INTO TaiKhoan VALUES ('admin456', '123456', 4);

INSERT INTO LOAIBANTHANG (MaLoaiBT, TenLoaiBT) VALUES (1, 'Bàn thắng thường');
INSERT INTO LOAIBANTHANG (MaLoaiBT, TenLoaiBT) VALUES (2, 'Phạt đền');
INSERT INTO LOAIBANTHANG (MaLoaiBT, TenLoaiBT) VALUES (3, 'Đá phạt');
INSERT INTO LOAIBANTHANG (MaLoaiBT, TenLoaiBT) VALUES (4, 'Phản lưới nhà');

INSERT INTO ViTriTD (MaVT, TenVT) VALUES (1, 'Forward');
INSERT INTO ViTriTD (MaVT, TenVT) VALUES (2, 'Midfielder');
INSERT INTO ViTriTD (MaVT, TenVT) VALUES (3, 'Defender');
INSERT INTO ViTriTD (MaVT, TenVT) VALUES (4, 'Goalkeeper');

INSERT INTO LoaiCauThu (MaLoaiCT, TenLoaiCT) VALUES (0, 'Cầu thủ nội');
INSERT INTO LoaiCauThu (MaLoaiCT, TenLoaiCT) VALUES (1, 'Cầu thủ ngoại');

INSERT INTO MuaGiai (MaMG, TenMG, NgayKhaiMac, NgayBeMac, LogoMG)
VALUES (1, 'V-League 2025', TO_DATE('2025-05-21', 'YYYY-MM-DD'), TO_DATE('2026-05-21', 'YYYY-MM-DD'), 'Vleague2025.png');

Insert Into QuyDinh(MaMG) VALUES (1);

INSERT INTO VongDau (MaVD, TenVD, MaMG, NgayBD, NgayKT)
VALUES (1, 'Lượt đi', 1, TO_DATE('2025-05-22', 'YYYY-MM-DD'), TO_DATE('2025-11-22', 'YYYY-MM-DD'));

INSERT INTO VongDau (MaVD, TenVD, MaMG, NgayBD, NgayKT)
VALUES (2, 'Lượt về', 1, TO_DATE('2025-12-01', 'YYYY-MM-DD'), TO_DATE('2026-05-21', 'YYYY-MM-DD'));

INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (1, 'Lạch Tray', 'Sân Lạch Tray, Hải Phòng', 50000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (2, 'Mỹ Đình', 'Sân Mỹ Đình, Hà Nội', 100000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (3, 'Hàng Đẫy', 'Sân Hàng Đẫy, Hà Nội', 100000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (4, 'Thống Nhất', 'Sân Thống Nhất, Thành phố Hồ Chí Minh', 100000);

INSERT INTO CLB(MaCLB, TenCLB, LogoCLB, SanNha, TenHLV, Email) VALUES (1, 'Hải Phòng FC', 'HPFC.png', 1, 'Nguyễn Văn A', 'hpfc@gmail.com');
INSERT INTO CLB(MaCLB, TenCLB, LogoCLB, SanNha, TenHLV, Email) VALUES (2, 'Hà Nội FC', 'HNFC.png', 2, 'Trần Văn B', 'hnfc@gmai.com');
INSERT INTO CLB(MaCLB, TenCLB, LogoCLB, SanNha, TenHLV, Email) VALUES (3, 'Viettel FC', 'VTFC.png', 3, 'Lê Văn C', 'vtfc@gmail.com');
INSERT INTO CLB(MaCLB, TenCLB, LogoCLB, SanNha, TenHLV, Email) VALUES (4, 'Tp Hồ Chí Minh FC', 'HCMFC.png', 4, 'Phạm Văn D', 'hcmfc@gmail.com');

-- Hải Phòng FC (MaCLB = 1)
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (1, 'Nguyen Van 1', TO_DATE('2000-01-01','YYYY-MM-DD'), 'Vietnam', 'hp1.png', 1, 0, 1, 1);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (2, 'Nguyen Van 2', TO_DATE('1999-02-02','YYYY-MM-DD'), 'Vietnam', 'hp2.png', 2, 0, 1, 2);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (3, 'Nguyen Van 3', TO_DATE('1998-03-03','YYYY-MM-DD'), 'Vietnam', 'hp3.png', 3, 0, 1, 3);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (4, 'Nguyen Van 4', TO_DATE('1997-04-04','YYYY-MM-DD'), 'Vietnam', 'hp4.png', 4, 0, 1, 4);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (5, 'Nguyen Van 5', TO_DATE('2001-05-05','YYYY-MM-DD'), 'Vietnam', 'hp5.png', 5, 0, 1, 1);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (6, 'Nguyen Van 6', TO_DATE('2002-06-06','YYYY-MM-DD'), 'Vietnam', 'hp6.png', 6, 0, 1, 2);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (7, 'Nguyen Van 7', TO_DATE('2000-07-07','YYYY-MM-DD'), 'Vietnam', 'hp7.png', 7, 0, 1, 3);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (8, 'Nguyen Van 8', TO_DATE('1999-08-08','YYYY-MM-DD'), 'Vietnam', 'hp8.png', 8, 0, 1, 4);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (9, 'Nguyen Van 9', TO_DATE('1998-09-09','YYYY-MM-DD'), 'Vietnam', 'hp9.png', 9, 0, 1, 1);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (10, 'Nguyen Van 10', TO_DATE('1997-10-10','YYYY-MM-DD'), 'Vietnam', 'hp10.png', 10, 0, 1, 2);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (11, 'Nguyen Van 11', TO_DATE('2001-11-11','YYYY-MM-DD'), 'Vietnam', 'hp11.png', 11, 0, 1, 3);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (12, 'Nguyen Van 12', TO_DATE('2002-12-12','YYYY-MM-DD'), 'Vietnam', 'hp12.png', 12, 0, 1, 4);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (13, 'Nguyen Van 13', TO_DATE('2000-01-13','YYYY-MM-DD'), 'Vietnam', 'hp13.png', 13, 0, 1, 1);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (14, 'Nguyen Van 14', TO_DATE('1999-02-14','YYYY-MM-DD'), 'Vietnam', 'hp14.png', 14, 0, 1, 2);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (15, 'Nguyen Van 15', TO_DATE('1998-03-15','YYYY-MM-DD'), 'Vietnam', 'hp15.png', 15, 0, 1, 3);

-- Hà Nội FC (MaCLB = 2)
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (16, 'Tran Van 1', TO_DATE('2000-01-01','YYYY-MM-DD'), 'Vietnam', 'hn1.png', 1, 0, 2, 1);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (17, 'Tran Van 2', TO_DATE('1999-02-02','YYYY-MM-DD'), 'Vietnam', 'hn2.png', 2, 0, 2, 2);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (18, 'Tran Van 3', TO_DATE('1998-03-03','YYYY-MM-DD'), 'Vietnam', 'hn3.png', 3, 0, 2, 3);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (19, 'Tran Van 4', TO_DATE('1997-04-04','YYYY-MM-DD'), 'Vietnam', 'hn4.png', 4, 0, 2, 4);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (20, 'Tran Van 5', TO_DATE('2001-05-05','YYYY-MM-DD'), 'Vietnam', 'hn5.png', 5, 0, 2, 1);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (21, 'Tran Van 6', TO_DATE('2002-06-06','YYYY-MM-DD'), 'Vietnam', 'hn6.png', 6, 0, 2, 2);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (22, 'Tran Van 7', TO_DATE('2000-07-07','YYYY-MM-DD'), 'Vietnam', 'hn7.png', 7, 0, 2, 3);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (23, 'Tran Van 8', TO_DATE('1999-08-08','YYYY-MM-DD'), 'Vietnam', 'hn8.png', 8, 0, 2, 4);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (24, 'Tran Van 9', TO_DATE('1998-09-09','YYYY-MM-DD'), 'Vietnam', 'hn9.png', 9, 0, 2, 1);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (25, 'Tran Van 10', TO_DATE('1997-10-10','YYYY-MM-DD'), 'Vietnam', 'hn10.png', 10, 0, 2, 2);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (26, 'Tran Van 11', TO_DATE('2001-11-11','YYYY-MM-DD'), 'Vietnam', 'hn11.png', 11, 0, 2, 3);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (27, 'Tran Van 12', TO_DATE('2002-12-12','YYYY-MM-DD'), 'Vietnam', 'hn12.png', 12, 0, 2, 4);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (28, 'Tran Van 13', TO_DATE('2000-01-13','YYYY-MM-DD'), 'Vietnam', 'hn13.png', 13, 0, 2, 1);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (29, 'Tran Van 14', TO_DATE('1999-02-14','YYYY-MM-DD'), 'Vietnam', 'hn14.png', 14, 0, 2, 2);
INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, SoAo, LoaiCT, MaCLB, MaVT) VALUES (30, 'Tran Van 15', TO_DATE('1998-03-15','YYYY-MM-DD'), 'Vietnam', 'hn15.png', 15, 0, 2, 3);


commit;
-- select * from MuaGiai;
-- select * from QuyDinh;
-- select * from VongDau;
-- select * from THUTU_UUTIEN;
-- select * from SAN;
-- select * from CLB;
-- select * from CauThu;
-- select * from LoaiCauThu;
-- select * from CLB_THAMGIAMUAGIAI;
-- select * from BANGXEPHANG_CLB ;
-- select * from TranDau;
-- select * from KETQUATD;
-- select * from CAUTHU_THAMGIAMUAGIAI ;
-- select * from BANTHANG;
-- select * from BANGXEPHANG_BANTHANG;
-- delete from MuaGiai;
-- delete from QuyDinh ;
-- delete from VongDau ;
-- delete from THUTU_UUTIEN;
-- delete from TranDau;
-- delete from KetQuaTD;
-- delete from CLB_THAMGIAMUAGIAI ;
-- delete from CAUTHU_THAMGIAMUAGIAI;
-- delete from BANGXEPHANG_CLB ;
-- delete from BANGXEPHANG_BANTHANG;