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
    TENVD NVARCHAR2(200),
    NgayBD DATE NOT NULL,
    NgayKT DATE NOT NULL
);
CREATE TABLE CLB (
    MaCLB NUMBER PRIMARY KEY,
    TenCLB NVARCHAR2(100) NOT NULL,
    LogoCLB NVARCHAR2(255),
    SanNha NUMBER NOT NULL,
    TenHLV NVARCHAR2(100),
    Email NVARCHAR2(255)
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
commit;
CREATE TABLE CauThu (
    MaCT NUMBER PRIMARY KEY,
    TenCT NVARCHAR2(50) NOT NULL,
    NgaySinh DATE,
    QuocTich NVARCHAR2(100),
    Avatar NVARCHAR2(100),
    CCCD NVARCHAR2(50),
    LoaiCT NUMBER NOT NULL,
    MaCLB NUMBER
);
CREATE TABLE ViTRiTD (
    MaVT NUMBER PRIMARY KEY,
    TenVT NVARCHAR2(200)
)
CREATE TABLE CAUTHU_CLB (
    MaMG NUMBER NOT NULL,
    MaCLB NUMBER NOT NULL,
    MaCT NUMBER NOT NULL,
    PRIMARY KEY (MaMG, MaCLB, MaCT),
    FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG),
    FOREIGN KEY (MaCLB) REFERENCES CLB(MaCLB),
    FOREIGN KEY (MaCT) REFERENCES CauThu(MaCT),
    CONSTRAINT chk_unique_cauthu_clb UNIQUE (MaCT, MaMG) 
);
Alter Table CAUTHU_CLB Drop Column SoAo;
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
    MaLoaiBT NUMBER NOT NULL
);
Create Table LoaiBanThang(
    MaLoaiBT NUMBER PRIMARY KEY,
    TenLoaiBT NVARCHAR2(100)
);
CREATE TABLE QuyDinh (
    MaQD NUMBER PRIMARY KEY,
    MaMG NUMBER,
    TUOITOITHIEU NUMBER DEFAULT 16,
    TUOITOIDA NUMBER DEFAULT 40,
    SOCTTOITHIEU NUMBER DEFAULT 15,
    SOCTTOIDA NUMBER DEFAULT 22,
    SOCTNUOCNGOAITOIDA NUMBER DEFAULT 3,
    DiemThang NUMBER DEFAULT 3,
    DiemHoa   NUMBER DEFAULT 1,
    DiemThua  NUMBER DEFAULT 0,
    PhutGhiBanToiDa NUMBER DEFAULT 90,
    PhutGhiBanToiThieu NUMBER DEFAULT 1
);

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
    PRIMARY KEY (MaMG, MaCLB)
);
CREATE TABLE BANGXEPHANG_BANTHANG (
    MaMG NUMBER NOT NULL,
    MaCT NUMBER NOT NULL,
    SoBanThang NUMBER DEFAULT 0,
    XepHang NUMBER UNIQUE,
    Penalty NUMBER DEFAULT 0,
    PRIMARY KEY (MaMG, MaCT)
);
CREATE TABLE THUTU_UUTIEN (
    MaTTU NUMBER PRIMARY KEY,
    MaMG NUMBER NOT NULL,
    TieuChi NVARCHAR2(50) NOT NULL,
    DoUuTien NUMBER NOT NULL,
    CONSTRAINT fk_thutu_mamg FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG),
    CONSTRAINT chk_tieuchi CHECK (TieuChi IN ('Diem', 'HieuSo', 'SoTran', 'Thang', 'Hoa', 'Thua')),
    CONSTRAINT chk_douutien CHECK (DoUuTien >= 1)
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
ALTER TABLE BANTHANG ADD CONSTRAINT Fk_LBT_BT FOREIGN KEY(MaLoaiBT) REFERENCES LoaiBanThang(MaLoaiBT);

--QUYDINH
ALTER TABLE QUYDINH ADD CONSTRAINT Fk_QDMG FOREIGN KEY(MaMG) REFERENCES MUAGIAI(MaMG);

--BANGXEPHANG_CLB
ALTER TABLE BANGXEPHANG_CLB ADD CONSTRAINT fk_maMG FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG);
ALTER TABLE BANGXEPHANG_CLB ADD CONSTRAINT fk_chitietbxh_madoi FOREIGN KEY (MaCLB) REFERENCES CLB(MaCLB);

--BANGXEPHANG_BANTHANG
ALTER TABLE BANGXEPHANG_BANTHANG ADD CONSTRAINT fk_maMG_bxhbt FOREIGN KEY (MaMG) REFERENCES MuaGiai(MaMG);
ALTER TABLE BANGXEPHANG_BANTHANG ADD CONSTRAINT fk_chitietbxh_maCT FOREIGN KEY (MaCT) REFERENCES CAUTHU(MaCT);

--CAUTHU
ALTER TABLE CAUTHU ADD CONSTRAINT fk_CT1 FOREIGN KEY(MaCLB) REFERENCES CLB(MaCLB);
SELECT 
    uc.constraint_name,
    uc.constraint_type,
    uc.status,
    ucc.column_name,
    uc.r_constraint_name,
    uc.search_condition
FROM 
    user_constraints uc
LEFT JOIN 
    user_cons_columns ucc ON uc.constraint_name = ucc.constraint_name
WHERE 
    uc.table_name = 'CAUTHU'
ORDER BY 
    uc.constraint_name, ucc.position;
-------------MienGiaTri----------------
--VongDau
ALTER TABLE VongDau ADD CONSTRAINT chk_TenVD CHECK (TENVD IN ('Lượt đi', 'Lượt về', 'Vòng 1', 'Vòng 16 đội', 'Tứ kết', 'Bán kết', 'Chung kết' ));

--Cauthu
ALTER TABLE CauThu ADD CONSTRAINT chk_LoaiCT CHECK (LoaiCT IN (0,1));

--LoaiBanThang
ALTER TABLE LoaiBanThang ADD CONSTRAINT chk_LoaiBT CHECK (TenLoaiBT IN ('Bàn thắng thường','Phạt đền','Đá phạt','Phản lưới nhà'));

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
    IF :NEW.ThoiGian < v_ngay_bd OR :NEW.ThoiGian > v_ngay_kt THEN
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
------------QUY ĐỊNH VỀ ĐỘ TUỔI THAM GIA MÙA GIẢI CỦA CẦU THỦ
CREATE OR REPLACE TRIGGER trg_check_tuoi_cauthu_clb
BEFORE INSERT OR UPDATE ON CauThu_CLB
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


--------QUY ĐỊNH VỀ TỔNG SỐ LƯỢNG CẦU THỦ VÀ SỐ LƯỢNG CẦU THỦ NGOẠI CHO PHÉP THAM GIA MÙA GIẢI  
CREATE OR REPLACE TRIGGER trg_check_so_cauthu_clb
BEFORE INSERT OR UPDATE ON CauThu_CLB
FOR EACH ROW
DECLARE
    v_total_players NUMBER;
    v_foreign_players NUMBER;
    v_soct_toithieu NUMBER;
    v_soct_toida NUMBER;
    v_soct_nuocngoai_toida NUMBER;
    v_loai_ct NUMBER;
    v_old_total NUMBER;
    v_old_foreign NUMBER;
BEGIN
    -- Lấy quy định từ bảng QuyDinh (nếu có)
    BEGIN
        SELECT NVL(SOCTTOITHIEU, 15), NVL(SOCTTOIDA, 22), NVL(SOCTNUOCNGOAITOIDA, 3)
        INTO v_soct_toithieu, v_soct_toida, v_soct_nuocngoai_toida
        FROM QuyDinh
        WHERE MaMG = NVL(:NEW.MaMG, :OLD.MaMG);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_soct_toithieu := 15;
            v_soct_toida := 22;
            v_soct_nuocngoai_toida := 3;
    END;

    -- Đếm tổng số cầu thủ trước khi thay đổi
    SELECT COUNT(*)
    INTO v_old_total
    FROM CauThu_CLB
    WHERE MaMG = NVL(:NEW.MaMG, :OLD.MaMG)
    AND MaCLB = NVL(:NEW.MaCLB, :OLD.MaCLB);

    -- Đếm số cầu thủ nước ngoài trước khi thay đổi
    SELECT COUNT(*)
    INTO v_old_foreign
    FROM CauThu_CLB ct_clb
    JOIN CauThu ct ON ct_clb.MaCT = ct.MaCT
    WHERE ct_clb.MaMG = NVL(:NEW.MaMG, :OLD.MaMG)
    AND ct_clb.MaCLB = NVL(:NEW.MaCLB, :OLD.MaCLB)
    AND ct.LoaiCT = 1;

    -- Kiểm tra khi thêm hoặc cập nhật
    IF INSERTING OR UPDATING THEN
        -- Lấy loại cầu thủ của MaCT mới
        SELECT LoaiCT
        INTO v_loai_ct
        FROM CauThu
        WHERE MaCT = :NEW.MaCT;

        -- Đếm tổng số cầu thủ sau khi thêm (dự kiến)
        v_total_players := v_old_total + 1;
        -- Đếm số cầu thủ nước ngoài sau khi thêm (dự kiến)
        v_foreign_players := v_old_foreign + CASE WHEN v_loai_ct = 1 THEN 1 ELSE 0 END;

        -- Kiểm tra tổng số cầu thủ
        IF v_total_players > v_soct_toida THEN
            RAISE_APPLICATION_ERROR(-20007, 
                'Số lượng cầu thủ của CLB trong mùa giải sẽ vượt quá tối đa (' || v_soct_toida || ').');
        END IF;

        -- Kiểm tra số cầu thủ nước ngoài
        IF v_loai_ct = 1 AND v_foreign_players > v_soct_nuocngoai_toida THEN
            RAISE_APPLICATION_ERROR(-20008, 
                'Số cầu thủ nước ngoài của CLB sẽ vượt quá tối đa (' || v_soct_nuocngoai_toida || ').');
        END IF;

    -- Kiểm tra khi xóa
--    ELSIF DELETING THEN
--        -- Lấy loại cầu thủ của MaCT bị xóa
--        SELECT LoaiCT
--        INTO v_loai_ct
--        FROM CauThu
--        WHERE MaCT = :OLD.MaCT;
--
--        -- Đếm lại tổng số cầu thủ và cầu thủ nước ngoài sau khi xóa
--        v_total_players := v_old_total - 1;
--        v_foreign_players := v_old_foreign - CASE WHEN v_loai_ct = 1 THEN 1 ELSE 0 END;
--
--         Kiểm tra số lượng tối thiểu
--        IF v_total_players < v_soct_toithieu THEN
--            RAISE_APPLICATION_ERROR(-20009, 
--                'Số lượng cầu thủ của CLB trong mùa giải sẽ dưới mức tối thiểu (' || v_soct_toithieu || ').');
--        END IF;
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20010, 
            'Không tìm thấy thông tin cầu thủ cho MaCT: ' || NVL(:NEW.MaCT, :OLD.MaCT) || 
            ' hoặc quy định cho MaMG: ' || NVL(:NEW.MaMG, :OLD.MaMG));
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20011, 'Lỗi không xác định khi kiểm tra số lượng cầu thủ: ' || SQLERRM);
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
--------------------------------------CAPNHAT_TENVONGDAU----------------------------
CREATE OR REPLACE TRIGGER trg_update_tenvd
AFTER INSERT OR UPDATE OF MaMG, TenVD, NgayBD, NgayKT ON VongDau
FOR EACH ROW
DECLARE
    v_tenMG NVARCHAR2(100);
BEGIN
    SELECT TenMG INTO v_tenMG
    FROM MuaGiai
    WHERE MaMG = :NEW.MaMG;

    -- Tạo tên vòng đấu mới với tên mùa giải trong ngoặc
    DECLARE
        v_newTenVD NVARCHAR2(200) := TRIM(:NEW.TenVD) || ' (' || TRIM(v_tenMG) || ')';
    BEGIN
        IF LENGTH(v_newTenVD) > 200 THEN
            RAISE_APPLICATION_ERROR(-20015, 'Tên vòng đấu sau khi ghép vượt quá 200 ký tự.');
        END IF;

        -- Cập nhật TenVD
        :NEW.TenVD := v_newTenVD;
    END;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20013, 'Mã mùa giải (MaMG = ' || :NEW.MaMG || ') không tồn tại.');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20016, 'Lỗi khi cập nhật tên vòng đấu: ' || SQLERRM);
END;
/
----------------------------------------------------------**********PROCEDURE**********---------------------

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
    TYPE club_ranking_t IS TABLE OF BANGXEPHANG_CLB%ROWTYPE INDEX BY PLS_INTEGER;
    v_clubs club_ranking_t;
    v_temp_clubs club_ranking_t;
    v_index PLS_INTEGER;
    v_order_by_clause VARCHAR2(4000);
    v_tieu_chi NVARCHAR2(50);
    v_do_uu_tien NUMBER;
    v_found BOOLEAN := FALSE;
BEGIN
    -- Lấy dữ liệu từ BANGXEPHANG_CLB vào mảng
    SELECT * BULK COLLECT INTO v_clubs
    FROM BANGXEPHANG_CLB
    WHERE MaMG = p_maMG;

    -- Kiểm tra xem có dữ liệu trong THUTU_UUTIEN không
    FOR priority_rec IN (
        SELECT TieuChi, DoUuTien
        FROM THUTU_UUTIEN
        WHERE MaMG = p_maMG
        ORDER BY DoUuTien
    )
    LOOP
        v_found := TRUE;
        v_tieu_chi := priority_rec.TieuChi;
        v_do_uu_tien := priority_rec.DoUuTien;

        -- Sắp xếp mảng v_clubs theo tiêu chí hiện tại
        FOR i IN 1 .. v_clubs.COUNT - 1 LOOP
            FOR j IN i + 1 .. v_clubs.COUNT LOOP
                DECLARE
                    v_swap BOOLEAN := FALSE;
                BEGIN
                    IF v_tieu_chi = 'Diem' THEN
                        IF v_clubs(i).Diem < v_clubs(j).Diem THEN
                            v_swap := TRUE;
                        ELSIF v_clubs(i).Diem = v_clubs(j).Diem THEN
                            -- Nếu bằng nhau, giữ nguyên để tiêu chí tiếp theo xử lý
                            CONTINUE;
                        END IF;
                    ELSIF v_tieu_chi = 'HieuSo' THEN
                        IF v_clubs(i).Diem = v_clubs(j).Diem AND v_clubs(i).HieuSo < v_clubs(j).HieuSo THEN
                            v_swap := TRUE;
                        ELSIF v_clubs(i).HieuSo = v_clubs(j).HieuSo THEN
                            CONTINUE;
                        END IF;
                    ELSIF v_tieu_chi = 'Thang' THEN
                        IF v_clubs(i).Diem = v_clubs(j).Diem AND v_clubs(i).HieuSo = v_clubs(j).HieuSo 
                           AND v_clubs(i).Thang < v_clubs(j).Thang THEN
                            v_swap := TRUE;
                        ELSIF v_clubs(i).Thang = v_clubs(j).Thang THEN
                            CONTINUE;
                        END IF;
                    ELSIF v_tieu_chi = 'SoTran' THEN
                        IF v_clubs(i).Diem = v_clubs(j).Diem AND v_clubs(i).HieuSo = v_clubs(j).HieuSo 
                           AND v_clubs(i).Thang = v_clubs(j).Thang AND v_clubs(i).SoTran < v_clubs(j).SoTran THEN
                            v_swap := TRUE;
                        ELSIF v_clubs(i).SoTran = v_clubs(j).SoTran THEN
                            CONTINUE;
                        END IF;
                    ELSIF v_tieu_chi = 'Hoa' THEN
                        IF v_clubs(i).Diem = v_clubs(j).Diem AND v_clubs(i).HieuSo = v_clubs(j).HieuSo 
                           AND v_clubs(i).Thang = v_clubs(j).Thang AND v_clubs(i).SoTran = v_clubs(j).SoTran 
                           AND v_clubs(i).Hoa < v_clubs(j).Hoa THEN
                            v_swap := TRUE;
                        ELSIF v_clubs(i).Hoa = v_clubs(j).Hoa THEN
                            CONTINUE;
                        END IF;
                    ELSIF v_tieu_chi = 'Thua' THEN
                        IF v_clubs(i).Diem = v_clubs(j).Diem AND v_clubs(i).HieuSo = v_clubs(j).HieuSo 
                           AND v_clubs(i).Thang = v_clubs(j).Thang AND v_clubs(i).SoTran = v_clubs(j).SoTran 
                           AND v_clubs(i).Hoa = v_clubs(j).Hoa AND v_clubs(i).Thua < v_clubs(j).Thua THEN
                            v_swap := TRUE;
                        END IF;
                    END IF;

                    -- Hoán đổi nếu cần
                    IF v_swap THEN
                        v_temp_clubs(i) := v_clubs(i);
                        v_clubs(i) := v_clubs(j);
                        v_clubs(j) := v_temp_clubs(i);
                    END IF;
                END;
            END LOOP;
        END LOOP;
    END LOOP;

    -- Nếu không có dữ liệu trong THUTU_UUTIEN, sắp xếp mặc định theo Diem và HieuSo
    IF NOT v_found THEN
        FOR i IN 1 .. v_clubs.COUNT - 1 LOOP
            FOR j IN i + 1 .. v_clubs.COUNT LOOP
                IF v_clubs(i).Diem < v_clubs(j).Diem THEN
                    v_temp_clubs(i) := v_clubs(i);
                    v_clubs(i) := v_clubs(j);
                    v_clubs(j) := v_temp_clubs(i);
                ELSIF v_clubs(i).Diem = v_clubs(j).Diem AND v_clubs(i).HieuSo < v_clubs(j).HieuSo THEN
                    v_temp_clubs(i) := v_clubs(i);
                    v_clubs(i) := v_clubs(j);
                    v_clubs(j) := v_temp_clubs(i);
                END IF;
            END LOOP;
        END LOOP;
    END IF;

    -- Cập nhật thứ hạng vào BANGXEPHANG_CLB
    FOR i IN 1 .. v_clubs.COUNT LOOP
        UPDATE BANGXEPHANG_CLB
        SET Hang = i
        WHERE MaMG = p_maMG AND MaCLB = v_clubs(i).MaCLB;
    END LOOP;

    -- Commit giao dịch
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20004, 'Lỗi khi tính lại thứ hạng: ' || SQLERRM);
END RecalculateRankingPositions;
/
-----------------UpdateRanking--------------
CREATE OR REPLACE PROCEDURE UpdateRanking(
    p_maTD IN NUMBER
)
AS
    v_maCLB1 NUMBER;
    v_maCLB2 NUMBER;
    v_maMG NUMBER;
    v_diemThang NUMBER;
    v_diemHoa NUMBER;
    v_diemThua NUMBER;
    v_thang1 NUMBER := 0;
    v_hoa1 NUMBER := 0;
    v_thua1 NUMBER := 0;
    v_diem1 NUMBER := 0;
    v_thang2 NUMBER := 0;
    v_hoa2 NUMBER := 0;
    v_thua2 NUMBER := 0;
    v_diem2 NUMBER := 0;
    v_hieuSo1 NUMBER := 0;
    v_hieuSo2 NUMBER := 0;
    v_banThang1 NUMBER:=0;
    v_banThang2 NUMBER:=0;
BEGIN
    -- Lấy thông tin hai CLB và MaMG từ TranDau
    SELECT MaCLB1, MaCLB2, (SELECT MaMG FROM VongDau WHERE MaVD = td.MaVD)
    INTO v_maCLB1, v_maCLB2, v_maMG
    FROM TranDau td
    WHERE MaTD = p_maTD;

    -- Lấy điểm thắng, hòa, thua từ QuyDinh
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

    -- Cập nhật BXH 
    -- Tính số bàn thắng và bàn thua
    SELECT DiemCLB1
    INTO v_banThang1
    FROM KetQuaTD kq
    WHERE kq.MaTD = p_maTD;

    SELECT DiemCLB2
    INTO v_banThang2
    FROM KetQuaTD kq
    WHERE kq.MaTD = p_maTD;

    v_hieuSo1 := v_banThang1 - v_banThang2;
    v_hieuSo2 := v_banThang2 - v_banThang1;
    
    -- Tính trạng thái trận đấu và điểm số
    IF v_banThang1 > v_banThang2 THEN
        v_thang1 := 1;
        v_diem1 := v_diemThang;
        v_thua2 :=1;
        v_diem2 :=v_diemThua;
    ELSIF v_banThang1 = v_banThang2 THEN
        v_hoa1 := 1;
        v_diem1 := v_diemHoa;
        v_hoa2 := 1;
        v_diem2 := v_diemHoa;
    ELSE
        v_thua1 := 1;
        v_diem1 := v_diemThua;
        v_thang2 := 1;
        v_diem2 := v_diemThang;
    END IF;
    
    -- Cập nhật BXH cho CLB1
    UPDATE BANGXEPHANG_CLB
    SET SoTran = SoTran+1,
        Thang = Thang+ v_thang1,
        Hoa = Hoa +v_hoa1,
        Thua = Thua +v_thua1,
        Diem = Diem+v_diem1,
        HieuSo = HieuSo+v_hieuSo1
    WHERE MaMG = v_maMG
    AND MaCLB = v_maCLB1;

   -- Cập nhật BXH cho CLB2
    UPDATE BANGXEPHANG_CLB
    SET SoTran = SoTran+1,
        Thang = Thang+v_thang2,
        Hoa = Hoa +v_hoa2,
        Thua = Thua +v_thua2,
        Diem = Diem+v_diem2,
        HieuSo = HieuSo+v_hieuSo2
    WHERE MaMG = v_maMG
    AND MaCLB = v_maCLB2;

    -- Commit giao dịch
    COMMIT;
    RecalculateRankingPositions(v_maMG);
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20002, 'Không tìm thấy thông tin trận đấu hoặc quy định cho MaTD: ' || p_maTD);
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20003, 'Lỗi khi cập nhật kết quả và BXH: ' || SQLERRM);
END UpdateRanking;
/

----------------------------------------------------MUAGIAI------------------------------
----------------------RegisterClubForSeason--------------------
CREATE OR REPLACE PROCEDURE RegisterClubForSeason(
    p_maCLB IN NUMBER,
    p_maMG IN NUMBER
)
AS
    v_count NUMBER;
BEGIN
    -- Kiểm tra xem CLB đã đăng ký mùa giải này chưa
    SELECT COUNT(*) INTO v_count
    FROM CLB_THAMGIAMUAGIAI
    WHERE MaCLB = p_maCLB AND MaMG = p_maMG;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20015, 'CLB với MaCLB = ' || p_maCLB || ' đã đăng ký mùa giải MaMG = ' || p_maMG);
    END IF;

    -- Đăng ký CLB vào mùa giải (chèn bản ghi mới)
    INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG)
    VALUES (p_maCLB, p_maMG);
    
    InsertInitialRanking(p_maMG,p_maCLB);
    -- Lưu thay đổi
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20016, 'Lỗi khi đăng ký CLB: ' || SQLERRM);
END RegisterClubForSeason;
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
    -- Mở cursor với truy vấn lấy các trận đấu có ngày thi đấu >= ngày hiện tại
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
                v.TenVD AS TenVD
            FROM TranDau td
            JOIN VongDau v ON td.MaVD = v.MaVD
            JOIN CLB c1 ON td.MaCLB1 = c1.MaCLB
            JOIN CLB c2 ON td.MaCLB2 = c2.MaCLB
            JOIN SAN s ON td.MaSan = s.MaSan
            JOIN MuaGiai m ON v.MaMG = m.MaMG
            LEFT JOIN KetQuaTD kq ON td.MaTD = kq.MaTD
            WHERE td.ThoiGian <= TRUNC(SYSDATE)  -- Ngày thi đấu đã qua
            AND kq.MaTD IS NULL                -- Chưa có kết quả
            ORDER BY td.ThoiGian ;

END GetPendingMatches;
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
    WHERE TenMG = p_tenMuaGiai;

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
    UpdateRanking(p_maTD);
    -- Commit giao dịch
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
------------------UpdateMatchResult---------------------
CREATE OR REPLACE PROCEDURE UpdateMatchResult(
    p_maTD IN NUMBER,
    p_diemCLB1 IN NUMBER,
    p_diemCLB2 IN NUMBER
)
AS
    v_count NUMBER;
BEGIN
    -- Kiểm tra xem bản ghi có tồn tại không
    SELECT COUNT(*) INTO v_count
    FROM KetQuaTD
    WHERE MaTD = p_maTD;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Không tìm thấy kết quả trận đấu với MaTD = ' || p_maTD);
    END IF;

    -- Kiểm tra giá trị điểm hợp lệ (không âm)
    IF p_diemCLB1 < 0 OR p_diemCLB2 < 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Điểm số không được âm.');
    END IF;

    -- Cập nhật bản ghi
    UPDATE KetQuaTD
    SET DiemCLB1 = p_diemCLB1,
        DiemCLB2 = p_diemCLB2
    WHERE MaTD = p_maTD;

    -- Kiểm tra xem cập nhật thành công không
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Không thể cập nhật kết quả trận đấu với MaTD = ' || p_maTD);
    END IF;

    COMMIT;
    UpdateRanking(p_maTD);
    -- Commit giao dịch
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20004, 'Lỗi khi cập nhật kết quả trận đấu: ' || SQLERRM);
END UpdateMatchResult;
/
----------------------------------------------------CAUTHU------------------------------
------------------InsertCauThu---------------------
CREATE OR REPLACE PROCEDURE InsertCauThu(
    p_maCT OUT NUMBER,
    p_tenCT IN NVARCHAR2,
    p_ngaySinh IN DATE,
    p_quocTich IN NVARCHAR2,
    p_avatar IN NVARCHAR2,
    p_cccd IN NVARCHAR2,
    p_loaiCT IN NUMBER
)
AS
    v_maxMaCT NUMBER;
BEGIN
    -- Tạo MaCT mới (MAX + 1)
    SELECT NVL(MAX(MaCT), 0) + 1 INTO v_maxMaCT
    FROM CauThu;

    -- Thêm cầu thủ mới
    INSERT INTO CauThu (MaCT, TenCT, NgaySinh, QuocTich, Avatar, CCCD, LoaiCT)
    VALUES (v_maxMaCT, p_tenCT, p_ngaySinh, p_quocTich, p_avatar, p_cccd, p_loaiCT);

    -- Trả về MaCT vừa tạo
    p_maCT := v_maxMaCT;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20017, 'Lỗi khi thêm cầu thủ: ' || SQLERRM);
END InsertCauThu;
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
            cclb.MaMG,
            bt.MaCT,
            COUNT(*) AS SoBanThang,
            SUM(CASE WHEN lbt.TenLoaiBT LIKE '%Phạt đền%' THEN 1 ELSE 0 END) AS Penalty
        FROM BANTHANG bt
        JOIN CAUTHU_CLB cclb ON bt.MaCT = cclb.MaCT AND cclb.MaMG = p_maMG
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
    p_maLoaiBT IN NUMBER
)
AS
    v_maBT NUMBER;
    v_count NUMBER;
    v_MaMG NUMBER;
BEGIN
    -- Sinh mã MaBT mới = MAX(MaBT) + 1
    SELECT NVL(MAX(MaBT), 0) + 1 INTO v_maBT
    FROM BANTHANG;

    -- Kiểm tra giá trị PhutGhiBan hợp lệ (không âm, trigger sẽ kiểm tra giới hạn tối đa)
    IF p_phutGhiBan < 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Thời điểm ghi bàn không được âm.');
    END IF;

    -- Kiểm tra xem MaCT, MaTD, và MaLoaiBT có tồn tại không
    SELECT COUNT(*) INTO v_count
    FROM CAUTHU
    WHERE MaCT = p_maCT;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Mã cầu thủ (MaCT = ' || p_maCT || ') không tồn tại.');
    END IF;

    SELECT COUNT(*) INTO v_count
    FROM TranDau
    WHERE MaTD = p_maTD;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Mã trận đấu (MaTD = ' || p_maTD || ') không tồn tại.');
    END IF;

    SELECT COUNT(*) INTO v_count
    FROM LOAIBANTHANG
    WHERE MaLoaiBT = p_maLoaiBT;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'Mã loại bàn thắng (MaLoaiBT = ' || p_maLoaiBT || ') không tồn tại.');
    END IF;

    -- Thêm bản ghi mới với MaBT vừa sinh
    INSERT INTO BANTHANG (MaBT, MaCT, MaTD, PhutGhiBan, MaLoaiBT)
    VALUES (v_maBT, p_maCT, p_maTD, p_phutGhiBan, p_maLoaiBT);
    
    --Cập nhật BXH_BANTHANG
    Select vd.MaMG INTO v_MaMG 
    FROM TRANDAU td JOIN VONGDAU vd ON td.MaVD=vd.MaVD
    WHERE td.MaTD=p_maTD;
    UpdateBXH_BanThang(v_MaMG);
    -- Commit giao dịch
    COMMIT;

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
    v_count NUMBER;
    v_maMG NUMBER;
    v_maVD NUMBER;
BEGIN
   UPDATE BANTHANG
    SET MaCT = p_maCT,
        MaTD = p_maTD,
        PhutGhiBan = p_phutGhiBan,
        MaLoaiBT = p_maLoaiBT
    WHERE MaBT = p_maBT;

    -- Gọi procedure để cập nhật bảng xếp hạng
    SELECT vd.MaMG INTO v_maMG
    FROM TranDau td
    JOIN VongDau vd ON td.MaVD = vd.MaVD
    WHERE td.MaTD = p_maTD;

    UpdateBXH_BanThang(v_maMG);

    -- Lưu thay đổi
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

--------------------------------------------------------INSERT--------------------------------------------
INSERT INTO TaiKhoan VALUES ('admin', 'admin123', 'A');
/
-------------- MuaGiai------------
INSERT INTO MuaGiai (MaMG, TenMG, NgayKhaiMac, NgayBeMac, LogoMG) VALUES (1, 'V-League 2025', TO_DATE('01/01/2025', 'DD/MM/YYYY'), TO_DATE('30/06/2025', 'DD/MM/YYYY'), 'logo.png');
/
-------------- VongDau------------
INSERT INTO VongDau (MaVD, MaMG, TENVD, NgayBD, NgayKT) VALUES (1, 1, 'Lượt đi', TO_DATE('01/01/2025', 'DD/MM/YYYY'), TO_DATE('31/03/2025', 'DD/MM/YYYY'));
INSERT INTO VongDau (MaVD, MaMG, TENVD, NgayBD, NgayKT) VALUES (2, 1, 'Lượt về', TO_DATE('01/04/2025', 'DD/MM/YYYY'), TO_DATE('30/06/2025', 'DD/MM/YYYY'));
/
-------------- SAN------------
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (1, 'Sân Mỹ Đình', 'Hà Nội', 40000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (2, 'Sân Thống Nhất', 'TP Hồ Chí Minh', 25000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (3, 'Sân Hàng Đẫy', 'Hà Nội', 20000);
INSERT INTO SAN (MaSan, TenSan, DiaChi, Succhua) VALUES (4, 'Sân Lạch Tray', 'Hải Phòng', 30000);
/
-------------- CLB------------
INSERT INTO CLB (MaCLB, TenCLB, SanNha, LogoCLB) VALUES (1, 'Hà Nội FC', 1, 'HNFC.png');
INSERT INTO CLB (MaCLB, TenCLB, SanNha, LogoCLB) VALUES (2, 'TP Hồ Chí Minh FC', 2, 'HCMFC.png');
INSERT INTO CLB (MaCLB, TenCLB, SanNha, LogoCLB) VALUES (3, 'Viettel FC', 3, 'VTFC.png');
INSERT INTO CLB (MaCLB, TenCLB, SanNha, LogoCLB) VALUES (4, 'Hải Phòng FC', 4,'HPFC.png');
/
-------------- CLB_THAMGIAMUAGIAI------------
INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (1, 1);
INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (2, 1);
INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (3, 1);
INSERT INTO CLB_THAMGIAMUAGIAI (MaCLB, MaMG) VALUES (4, 1);
/
DECLARE
    CURSOR cur_thamgia IS
        SELECT MaCLB, MaMG FROM CLB_THAMGIAMUAGIAI;

    v_MaCLB CLB_THAMGIAMUAGIAI.MaCLB%TYPE;
    v_MaMG  CLB_THAMGIAMUAGIAI.MaMG%TYPE;
BEGIN
    OPEN cur_thamgia;
    LOOP
        FETCH cur_thamgia INTO v_MaCLB, v_MaMG;
        EXIT WHEN cur_thamgia%NOTFOUND;

        -- Gọi procedure cho mỗi dòng
        InsertInitialRanking(v_MaMG, v_MaCLB);
    END LOOP;
    CLOSE cur_thamgia;
END;
/

-------------- TranDau------------
-- Lượt đi (MaVD=1)
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (1, 1, 2, 1, 1, TO_DATE('15/01/2025 19:00', 'DD/MM/YYYY HH24:MI')); -- Hà Nội FC vs TP HCM FC
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (2, 3, 4, 3, 1, TO_DATE('15/01/2025 19:00', 'DD/MM/YYYY HH24:MI')); -- Viettel FC vs Hải Phòng FC
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (3, 1, 4, 1, 1, TO_DATE('15/02/2025 20:00', 'DD/MM/YYYY HH24:MI')); -- Hà Nội FC vs Hải Phòng FC
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (4, 3, 2, 3, 1, TO_DATE('15/02/2025 19:30', 'DD/MM/YYYY HH24:MI')); -- Viettel FC vs TP HCM FC
-- Lượt về (MaVD=2
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (7, 2, 1, 2, 2, TO_DATE('15/04/2025 19:00', 'DD/MM/YYYY HH24:MI')); -- TP HCM FC vs Hà Nội FC
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (8, 4, 3, 4, 2, TO_DATE('15/04/2025 19:00', 'DD/MM/YYYY HH24:MI')); -- Hải Phòng FC vs Viettel FC
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (5, 4, 1, 4, 2, TO_DATE('30/05/2025 18:45', 'DD/MM/YYYY HH24:MI')); -- Hải Phòng FC vs Hà Nội FC
INSERT INTO TranDau (MaTD, MaCLB1, MaCLB2, MaSan, MaVD, ThoiGian) VALUES (6, 2, 3, 2, 2, TO_DATE('30/05/2025 18:15', 'DD/MM/YYYY HH24:MI')); -- TP HCM FC vs Viettel FC

/
-------------- KetQuaTD------------
Begin
    InsertMatchResult(1,2,1);-- Kết quả cho trận MaTD = 1 (Hà Nội FC vs TP HCM FC)
    InsertMatchResult(2,1,0);-- Kết quả cho trận MaTD = 2 (Viettel FC vs Hải Phòng FC)
END;
/
-------------- LOAIBANTHANG------------
INSERT INTO LOAIBANTHANG (MaLoaiBT, TenLoaiBT) VALUES (1, 'Bàn thắng thường');
INSERT INTO LOAIBANTHANG (MaLoaiBT, TenLoaiBT) VALUES (2, 'Phạt đền');
INSERT INTO LOAIBANTHANG (MaLoaiBT, TenLoaiBT) VALUES (3, 'Đá phạt');
INSERT INTO LOAIBANTHANG (MaLoaiBT, TenLoaiBT) VALUES (4, 'Phản lưới nhà');
/
--------------CAUTHU--------------
-- Hà Nội FC (MaCLB = 1)
INSERT INTO CAUTHU (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (9, 'Nguyễn Văn A', TO_DATE('01/01/1995', 'DD/MM/YYYY'), 0); -- Nội, tuổi 30
INSERT INTO CAUTHU (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (10, 'John Smith', TO_DATE('15/03/1990', 'DD/MM/YYYY'), 1); -- Ngoại, tuổi 35
-- TP Hồ Chí Minh FC (MaCLB = 2)
INSERT INTO CAUTHU (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (11, 'Trần Văn B', TO_DATE('10/05/1998', 'DD/MM/YYYY'), 0); -- Nội, tuổi 27
INSERT INTO CAUTHU (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (12, 'Carlos Lopez', TO_DATE('20/07/1989', 'DD/MM/YYYY'), 1); -- Ngoại, tuổi 36
-- Viettel FC (MaCLB = 3)
INSERT INTO CAUTHU (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (13, 'Lê Văn C', TO_DATE('25/12/1996', 'DD/MM/YYYY'), 0); -- Nội, tuổi 29
INSERT INTO CAUTHU (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (14, 'Pedro Silva', TO_DATE('30/09/1992', 'DD/MM/YYYY'), 1); -- Ngoại, tuổi 33
-- Hải Phòng FC (MaCLB = 4)
INSERT INTO CAUTHU (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (15, 'Phạm Văn D', TO_DATE('05/06/1999', 'DD/MM/YYYY'), 0); -- Nội, tuổi 26
INSERT INTO CAUTHU (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (16, 'Miguel Torres', TO_DATE('12/11/1991', 'DD/MM/YYYY'), 1); -- Ngoại, tuổi 34
-------------------CAUTHU_CLB-------------
-- Hà Nội FC (MaCLB = 1)
INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (1, 1, 9); -- Nguyễn Văn A
INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (1, 1, 10); -- John Smith
-- TP Hồ Chí Minh FC (MaCLB = 2)
INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (1, 2, 11); -- Trần Văn B
INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (1, 2, 12); -- Carlos Lopez
-- Viettel FC (MaCLB = 3)
INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (1, 3, 13); -- Lê Văn C
INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (1, 3, 14); -- Pedro Silva
-- Hải Phòng FC (MaCLB = 4)
INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (1, 4, 15); -- Phạm Văn D
INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (1, 4, 16); -- Miguel Torres

----------BANTHANG------------
Begin
    InsertGoal(14, 2, 12, 3); -- Bàn thắng 1: Hà Nội FC (cầu thủ Nguyễn Văn A, MaCT = 9)
    InsertGoal(10, 1, 60, 2); -- Bàn thắng 2: Hà Nội FC (cầu thủ John Smith, MaCT = 10)
    InsertGoal(11, 1, 75, 1); -- Bàn thắng 3: TP Hồ Chí Minh FC (cầu thủ Trần Văn B, MaCT = 11)
end;
/