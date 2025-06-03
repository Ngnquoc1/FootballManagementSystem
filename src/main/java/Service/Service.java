package Service;

import Controller.Connection.DatabaseConnection;
import Model.*;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {
    private Connection conn = null;

    public Service() {
        try {
            conn = DatabaseConnection.getInstance().getConnectionn();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //    USER
    public MODEL_USER Login(String username, String password) {
        ResultSet rs = null;
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Bạn có thể mã hóa nếu cần
            rs = pstmt.executeQuery();
            MODEL_USER user = null;
            if (rs.next()) {
                user = new MODEL_USER();
                user.setUserName(rs.getString("TenDangNhap"));
                user.setPassWord(rs.getString("MatKhau"));
                user.setVaiTro(rs.getInt("MaVT"));
                return user;
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public MODEL_USER getUserByUsername(String username) {
        MODEL_USER user = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Không thể lấy kết nối Database");
                return null;
            }
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new MODEL_USER();
                user.setUserName(rs.getString("TenDangNhap"));
                user.setPassWord(rs.getString("MatKhau"));
                user.setVaiTro(rs.getInt("MaVT"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public MODEL_VAITRO getRoleById(int roleId) {
        MODEL_VAITRO role = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM VaiTro WHERE MaVT = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                role = new MODEL_VAITRO();
                role.setMaVaiTro(rs.getInt("MaVT"));
                role.setTenVaiTro(rs.getString("TenVaiTro"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return role;
    }

    //    MATCH-View
    public Match getMatchViewFromRs(ResultSet rs) throws SQLException {
        int maTD = rs.getInt("ID");
        String tenCLB1 = rs.getString("TenCLB1");
        String logoCLB1 = rs.getString("LogoCLB1");
        String tenCLB2 = rs.getString("TenCLB2");
        String logoCLB2 = rs.getString("LogoCLB2");
        String sanThiDau = rs.getString("SanThiDau");
        String tenMuaGiai = rs.getString("TenMuaGiai");
        String tenVD = rs.getString("TenVD");
        // Lấy ThoiGian và chuyển thành LocalDate, LocalTime
        Timestamp timestamp = rs.getTimestamp("ThoiGian");
        LocalDate ngayThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime gioThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        int scoreCLB1 = rs.getInt("Score1");
        int scoreCLB2 = rs.getInt("Score2");

        return new Match(maTD, tenMuaGiai, tenVD, tenCLB1, tenCLB2, gioThiDau, ngayThiDau, sanThiDau, logoCLB1, logoCLB2, scoreCLB1, scoreCLB2);
    }

    public Map<LocalDate, List<Match>> getUpcomingMatchs() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = new HashMap<>();
        ResultSet rs = null;
        try (CallableStatement cstmt = conn.prepareCall("{call GetUpComingMatches(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR); // Đăng ký tham số đầu ra là cursor
            cstmt.execute();
            // Lấy ResultSet từ cursor
            rs = (ResultSet) cstmt.getObject(1);

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("ThoiGian");
                LocalDate ngayThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Match match = getMatchViewFromRs(rs);
                matchesByDate.computeIfAbsent(ngayThiDau, k -> new ArrayList<>()).add(match);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return matchesByDate;
    }

    public List<Match> getResultedMatchList() throws SQLException {
        List<Match> matchList = new ArrayList<>();
        ResultSet rs = null;
        try (CallableStatement cstmt = conn.prepareCall("{call GetResultedMatches(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            rs = (ResultSet) cstmt.getObject(1);
            while (rs.next()) {
                Match match = getMatchViewFromRs(rs);
                matchList.add(match);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return matchList;
    }

    public Map<LocalDate, List<Match>> getResultedMatchs() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = new HashMap<>();
        List<Match> matchList = this.getResultedMatchList(); // Gọi lại hàm đầu tiên
        for (Match match : matchList) {
            LocalDate ngayThiDau = match.getNgayThiDau();
            matchesByDate.computeIfAbsent(ngayThiDau, k -> new ArrayList<>()).add(match);
        }
        return matchesByDate;
    }

    public List<Match> getPendingMatchList() throws SQLException {
        List<Match> matchList = new ArrayList<>();
        ResultSet rs = null;
        try (CallableStatement cstmt = conn.prepareCall("{call GetPendingMatches(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            rs = (ResultSet) cstmt.getObject(1);
            while (rs.next()) {
                Match match = getMatchViewFromRs(rs);
                matchList.add(match);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return matchList;
    }

    public ArrayList<Match> getMatchViewByCondition(String condition) {
        ResultSet rs = null;
        ArrayList<Match> ds = new ArrayList<>();
        String sql = "{call GetUpComingMatchesByCondition(?, ?)}";

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            // Gọi procedure
            // Nếu không có điều kiện, truyền NULL
            if (condition == null || condition.trim().isEmpty()) {
                cstmt.setNull(1, Types.VARCHAR);
            } else {
                cstmt.setString(1, condition);
            }
            cstmt.registerOutParameter(2, Types.REF_CURSOR); // Đăng ký cursor
            cstmt.execute();

            // Lấy ResultSet từ cursor
            rs = (ResultSet) cstmt.getObject(2);
            while (rs.next()) {
                ds.add(getMatchViewFromRs(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ds;
    }

    public Match selectByID(int id) {
        List<Match> matches = getMatchViewByCondition("td.MaTD = " + id);
        return matches.isEmpty() ? null : matches.getFirst();
    }

    //MATCH
    public MODEL_TRANDAU getMatchFromRs(ResultSet rs) throws Exception {
        MODEL_TRANDAU td = new MODEL_TRANDAU();
        td.setMaTD(rs.getInt("maTD"));
        td.setMaCLB1(rs.getInt("maCLB1"));
        td.setMaCLB2(rs.getInt("maCLB2"));
        td.setMaVD(rs.getInt("maVD"));
        td.setMaSan(rs.getInt("maSan"));
        td.setThoiGian(rs.getDate("thoiGian"));
        return td;
    }

    public MODEL_TRANDAU getMatchByID(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM TranDau WHERE maTD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getMatchFromRs(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int deleteMatch(Match model) throws SQLException {
        if (model == null || model.getId() <= 0) {
            throw new SQLException("Dữ liệu không hợp lệ: Match hoặc ID không hợp lệ.");
        }
        String sql = "{call DeleteMatchAndGoals(?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, model.getId());

            cstmt.execute();
            return 1;
        } catch (SQLException e) {
            throw e;
            // Ném lại ngoại lệ để giao diện xử lý
        }
    }

    public void insertMatch(Match model) throws SQLException {
        int maTD;
        String tenMuaGiai = model.getTenMuaGiai();
        String tenVD = model.getTenVongDau();
        String tenCLB1 = model.getTenCLB1();
        String tenCLB2 = model.getTenCLB2();
        String tenSan = model.getSanThiDau();
        LocalDateTime newDateTime = LocalDateTime.of(model.getNgayThiDau(), model.getGioThiDau());

        String sql = "{call InsertMatch(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, tenMuaGiai);
            cstmt.setString(2, tenVD);
            cstmt.setString(3, tenCLB1);
            cstmt.setString(4, tenCLB2);
            cstmt.setTimestamp(5, Timestamp.valueOf(newDateTime));
            cstmt.setString(6, tenSan);
            cstmt.registerOutParameter(7, Types.INTEGER); // Đăng ký để nhận MaTD

            cstmt.execute();
            maTD = cstmt.getInt(7); // Lấy MaTD trả về
            model.setId(maTD); // Cập nhật MaTD vào model

        } catch (SQLException e) {
            throw e;
        }
    }

    public void updateMatch(Match model) throws SQLException {
        int maTD = model.getId();
        String tenVD = model.getTenVongDau();
        String tenMuaGiai = model.getTenMuaGiai();
        String tenCLB1 = model.getTenCLB1();
        String tenCLB2 = model.getTenCLB2();
        String tenSan = model.getSanThiDau();
        LocalDateTime newDateTime = LocalDateTime.of(model.getNgayThiDau(), model.getGioThiDau());

        String sql = "{call UpdateMatch(?, ?,?, ?, ?, ?, ?)}";

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maTD);
            cstmt.setString(2, tenMuaGiai);
            cstmt.setString(3, tenVD);
            cstmt.setString(4, tenCLB1);
            cstmt.setString(5, tenCLB2);
            cstmt.setTimestamp(6, Timestamp.valueOf(newDateTime));
            cstmt.setString(7, tenSan);

            cstmt.execute();
        } catch (SQLException e) {
            throw e;
        }
    }

    //  MATCH RESULT
    public void insertResult(MODEL_KETQUATD model) throws SQLException {
        String sql = "INSERT INTO KetQuaTD (MaTD, DiemCLB1, DiemCLB2) VALUES (?, ?, ?)";

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, model.getMaTD());
            cstmt.setInt(2, model.getDiemCLB1());
            cstmt.setInt(3, model.getDiemCLB2());
            cstmt.execute();
        } catch (SQLException e) {
            String sql1 = "MaTD = " + model.getMaTD();
            List<MODEL_BANTHANG> allGoals = this.getGoalByCondition(sql1);
            for (MODEL_BANTHANG goal : allGoals) {
                try {
                    this.deleteGoal(goal);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Lỗi khi thêm kết quả trận đấu: " + e.getMessage());
        }
    }

    public void updateResult(MODEL_KETQUATD model) throws SQLException {
        String sql = "UPDATE KetQuaTD SET DiemCLB1 = ?, DiemCLB2 = ? WHERE MaTD = ?";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(3, model.getMaTD());
            cstmt.setInt(1, model.getDiemCLB1());
            cstmt.setInt(2, model.getDiemCLB2());
            cstmt.execute();

        } catch (SQLException e) {
            throw e;
        }
    }

    public int deleteResult(MODEL_KETQUATD model) throws SQLException {
        if (model == null || model.getMaTD() <= 0) {
            throw new SQLException("Dữ liệu không hợp lệ: Match hoặc ID không hợp lệ.");
        }
        String sql = "{call DeleteMatchResultAndGoals(?)}";
        int maTD = model.getMaTD();
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maTD);
            cstmt.execute();
            return 1; // Trả về 1 nếu xóa thành công
        } catch (SQLException e) {
            throw e; // Ném lại ngoại lệ để giao diện xử lý
        }
    }

    public MODEL_KETQUATD getResultFromRs(ResultSet rs) throws Exception {
        int maTD = rs.getInt("MaTD");
        int score1 = rs.getInt("DiemCLB1");
        int score2 = rs.getInt("DiemCLB2");
        return new MODEL_KETQUATD(maTD, score1, score2);
    }

    public ArrayList<MODEL_KETQUATD> getResultByCondition(String Condition) {
        ResultSet rs = null;
        ArrayList<MODEL_KETQUATD> ds = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM KetQuaTD WHERE " + Condition;
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ds.add(getResultFromRs(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ds;
    }

    public MODEL_KETQUATD getResultByID(int id) {
        List<MODEL_KETQUATD> res = getResultByCondition("MaTD = " + id);
        return res.isEmpty() ? null : res.getFirst();
    }


    //    CLUB
    public MODEL_CLB findClubByName(String searchTerm) {
        String sql = "SELECT * FROM CLB WHERE LOWER(TENCLB) LIKE LOWER(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MODEL_CLB club = new MODEL_CLB();
                club.setMaCLB(rs.getInt("MACLB"));
                club.setTenCLB(rs.getString("TENCLB"));
                club.setTenHLV(rs.getString("TENHLV"));
                club.setEmail(rs.getString("EMAIL"));
                club.setLogoCLB(rs.getString("LOGOCLB"));
                club.setMaSan(rs.getInt("SANNHA"));
                return club;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateClub(MODEL_CLB clb) throws Exception {
        String sql = "UPDATE CLB SET TenCLB = ?, LogoCLB = ?, TenHLV = ?, Email = ?, SanNha = ? WHERE MaCLB = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clb.getTenCLB());
            pstmt.setString(2, clb.getLogoCLB());
            pstmt.setString(3, clb.getTenHLV());
            pstmt.setString(4, clb.getEmail());
            pstmt.setInt(5, clb.getMaSan());
            pstmt.setInt(6, clb.getMaCLB());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addClub(MODEL_CLB newClub) throws Exception {
        String query = "{CALL InsertCLB(?, ?, ?, ?, ?, ?)}";
        int newClubId = -1;

        try (CallableStatement cstmt = conn.prepareCall(query)) {
            // Set the IN parameters
            cstmt.setString(1, newClub.getTenCLB());
            cstmt.setString(2, newClub.getLogoCLB());
            cstmt.setInt(3, newClub.getMaSan());
            cstmt.setString(4, newClub.getTenHLV());
            cstmt.setString(5, newClub.getEmail());

            // Register the OUT parameter for MaCLB
            cstmt.registerOutParameter(6, Types.NUMERIC);

            // Execute the procedure
            cstmt.execute();

            // Retrieve the generated MaCLB
            newClubId = cstmt.getInt(6);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error while adding club: " + e.getMessage());
        }

        return newClubId;
    }

    public void removeClub(int maCLB) {
        String sql = "DELETE FROM CLB WHERE MaCLB = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCLB);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MODEL_CLB> getAllClubs() {
        List<MODEL_CLB> clubs = new ArrayList<>();
        String sql = "SELECT * FROM CLB";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MODEL_CLB club = new MODEL_CLB();
                club.setMaCLB(rs.getInt("MaCLB"));
                club.setTenCLB(rs.getString("TenCLB"));
                club.setLogoCLB(rs.getString("LogoCLB"));
                club.setTenHLV(rs.getString("TenHLV"));
                club.setEmail(rs.getString("Email"));
                club.setMaSan(rs.getInt("SanNha"));
                clubs.add(club);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }

    public MODEL_CLB getClbByCondition(String s) {
        String sql = "SELECT * FROM CLB WHERE " + s;
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                MODEL_CLB club = new MODEL_CLB();
                club.setMaCLB(rs.getInt("MaCLB"));
                club.setTenCLB(rs.getString("TenCLB"));
                club.setLogoCLB(rs.getString("LogoCLB"));
                club.setTenHLV(rs.getString("TenHLV"));
                club.setEmail(rs.getString("Email"));
                club.setMaSan(rs.getInt("SanNha"));
                return club;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCLBIDFromGoal(int maCT, int maTD) throws SQLException {
        String sql = "SELECT cclb.MaCLB " +
                "FROM BANTHANG bt " +
                "JOIN TranDau td ON bt.MaTD = td.MaTD " +
                "JOIN VongDau vd ON td.MaVD = vd.MaVD " +
                "JOIN CAUTHU_THAMGIAMUAGIAI cclb ON bt.MaCT = cclb.MaCT AND cclb.MaMG = vd.MaMG " +
                "WHERE bt.MaCT = ? AND bt.MaTD = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, maCT); // Thay bằng giá trị thực tế (ví dụ: từ thuộc tính)
            stmt.setLong(2, maTD); // Thay bằng giá trị thực tế (ví dụ: từ thuộc tính)

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MaCLB");
            } else {
                throw new SQLException("Không tìm thấy MaCLB cho MaCT và MaTD đã cho.");
            }
        }
    }

    private boolean isClubExists(int maCLB) throws SQLException {
        String query = "SELECT COUNT(*) FROM CLB WHERE MaCLB = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, maCLB);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public MODEL_CLB getCLBByID(int maCLB) {
        String sql = "SELECT * FROM CLB WHERE MaCLB = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maCLB);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                MODEL_CLB clb = new MODEL_CLB();
                clb.setMaCLB(rs.getInt("MaCLB"));
                clb.setTenCLB(rs.getString("TenCLB"));
                clb.setLogoCLB(rs.getString("LogoCLB"));
                clb.setTenHLV(rs.getString("TenHLV"));
                clb.setEmail(rs.getString("Email"));
                clb.setMaSan(rs.getInt("SanNha"));
                return clb;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MODEL_CLB getCLBByMaSan(int maSan) {
        String sql = "SELECT * FROM CLB WHERE SanNha = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSan);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                MODEL_CLB clb = new MODEL_CLB();
                clb.setMaCLB(rs.getInt("MaCLB"));
                clb.setTenCLB(rs.getString("TenCLB"));
                clb.setLogoCLB(rs.getString("LogoCLB"));
                clb.setTenHLV(rs.getString("TenHLV"));
                clb.setEmail(rs.getString("Email"));
                clb.setMaSan(rs.getInt("SanNha"));
                return clb;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    Tournament
    public MODEL_MUAGIAI getTournamentFromRs(ResultSet rs) throws Exception {

        int id = Integer.parseInt(rs.getString("MaMG"));
        String clbName = (rs.getString("TenMG"));
        LocalDate startDate = rs.getDate("NgayKhaiMac").toLocalDate();
        LocalDate endDate = rs.getDate("NgayBeMac").toLocalDate();
        String clbLogo = rs.getString("LogoMG");
        return new MODEL_MUAGIAI(id, clbName, startDate, endDate, clbLogo);
    }

    public List<MODEL_MUAGIAI> getAllTournament() throws SQLException {
        ResultSet rs = null;
        ArrayList<MODEL_MUAGIAI> ds = new ArrayList<>();

        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM MUAGIAI";

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ds.add(getTournamentFromRs(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ds;
    }

    public List<MODEL_MUAGIAI> getAllActiveTournaments() {
        List<MODEL_MUAGIAI> list = new ArrayList<>();
        String sql = "SELECT * FROM MUAGIAI WHERE NGAYBEMAC >= SYSDATE";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int MaMG = (rs.getInt("MaMG"));
                String TenMG = (rs.getString("TenMG"));
                LocalDate NgayBD = (rs.getDate("NgayKhaiMac").toLocalDate());
                LocalDate NgayKT = (rs.getDate("NgayBeMac").toLocalDate());
                String LogoFileName = (rs.getString("LogoMG"));
                list.add(new MODEL_MUAGIAI(MaMG, TenMG, NgayBD, NgayKT, LogoFileName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public MODEL_MUAGIAI getTournamentByName(String value) {
        String sql = "SELECT * FROM MUAGIAI WHERE LOWER(TenMG) = LOWER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int MaMG = (rs.getInt("MaMG"));
                String TenMG = (rs.getString("TenMG"));
                LocalDate NgayBD = (rs.getDate("NgayKhaiMac").toLocalDate());
                LocalDate NgayKT = (rs.getDate("NgayBeMac").toLocalDate());
                String LogoFileName = (rs.getString("LogoMG"));
                return new MODEL_MUAGIAI(MaMG, TenMG, NgayBD, NgayKT, LogoFileName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MODEL_MUAGIAI getTournamentByID(int maMG) {
        String sql = "SELECT * FROM MUAGIAI WHERE MaMG = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maMG);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int MaMG = (rs.getInt("MaMG"));
                String TenMG = (rs.getString("TenMG"));
                LocalDate NgayBD = (rs.getDate("NgayKhaiMac").toLocalDate());
                LocalDate NgayKT = (rs.getDate("NgayBeMac").toLocalDate());
                String LogoFileName = (rs.getString("LogoMG"));
                return new MODEL_MUAGIAI(MaMG, TenMG, NgayBD, NgayKT, LogoFileName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int deleteTournament(MODEL_MUAGIAI modelMuagiai) throws SQLException {
        String sql = "DELETE FROM MuaGiai WHERE MaMG = ?";
        String sql1 = "DELETE FROM QuyDinh WHERE MaMG = ?";
        String sql2 = "DELETE FROM THUTU_UUTIEN WHERE MaMG = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)
             ; PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2)) {
            ps.setInt(1, modelMuagiai.getMaMG());
            ps1.setInt(1, modelMuagiai.getMaMG());
            ps2.setInt(1, modelMuagiai.getMaMG());
            return ps.executeUpdate() + ps1.executeUpdate() > 2 ? 1 : 0;

        }
    }

    public void updateTournament(MODEL_MUAGIAI modelMuagiai) throws SQLException {
        String sql = "UPDATE MuaGiai SET TenMG = ?, NgayKhaiMac = ?, NgayBeMac = ?, LogoMG = ? " +
                "WHERE MaMG = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(5, modelMuagiai.getMaMG());
            ps.setString(1, modelMuagiai.getTenMG());
            ps.setDate(2, Date.valueOf(modelMuagiai.getNgayBD()));
            ps.setDate(3, Date.valueOf(modelMuagiai.getNgayKT()));
            ps.setString(4, modelMuagiai.getLogoFileName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertTournament(MODEL_MUAGIAI modelMuagiai) throws SQLException {
        String sql1 = "SELECT NVL(MAX(MaMG),0) FROM MuaGiai";
        int nextId = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql1)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nextId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Lỗi khi lấy ID tiếp theo cho MuaGiai: " + e.getMessage());
        }

        String sql = "INSERT INTO MuaGiai (MaMG, TenMG, NgayKhaiMac, NgayBeMac, LogoMG) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nextId + 1);
            ps.setString(2, modelMuagiai.getTenMG());
            ps.setDate(3, Date.valueOf(modelMuagiai.getNgayBD()));
            ps.setDate(4, Date.valueOf(modelMuagiai.getNgayKT()));
            ps.setString(5, modelMuagiai.getLogoFileName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextId + 1; // Trả về ID mới đã được chèn
    }

    //    ROUND
    public List<MODEL_VONGDAU> getAllRoundByTournament(int maMG) {
        List<MODEL_VONGDAU> list = new ArrayList<>();
        String sql = "SELECT * FROM VONGDAU WHERE MAMG = ? ORDER BY MAVD";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maMG);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MODEL_VONGDAU vd = new MODEL_VONGDAU();
                vd.setMaVD(rs.getInt("MAVD"));
                vd.setTenVD(rs.getString("TENVD"));  // Đã thay đổi từ getInt sang getString
                vd.setMaMG(rs.getInt("MAMG"));
                vd.setNgayBD(rs.getDate("NGAYBD"));
                vd.setNgayKT(rs.getDate("NGAYKT"));
                list.add(vd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getNextIdVD() {
        String sql = "SELECT MAX(MAVD) FROM VONGDAU";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean insertVD(MODEL_VONGDAU vd) {
        int newId=this.getNextIdVD();
        String sql = "INSERT INTO VONGDAU (MAVD, TENVD, MAMG, NGAYBD, NGAYKT) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newId);
            stmt.setString(2, vd.getTenVD());
            stmt.setInt(3, vd.getMaMG());
            stmt.setDate(4, vd.getNgayBD());
            stmt.setDate(5, vd.getNgayKT());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateVD(MODEL_VONGDAU vd) {
        String sql = "UPDATE VONGDAU SET TENVD = ?, NGAYBD = ?, NGAYKT = ? WHERE MAVD = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vd.getTenVD());  // Đã thay đổi từ setInt sang setString
            stmt.setDate(2, vd.getNgayBD());
            stmt.setDate(3, vd.getNgayKT());
            stmt.setInt(4, vd.getMaVD());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVD(int maVD) {
        String sql = "DELETE FROM VONGDAU WHERE MAVD = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maVD);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //    STADIUM
    public List<MODEL_SAN> getAllStadiums() {
        List<MODEL_SAN> list = new ArrayList<>();
        String sql = "SELECT * FROM SAN";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MODEL_SAN san = new MODEL_SAN();
                san.setMaSan(rs.getInt("MaSan"));
                san.setTenSan(rs.getString("TenSAN"));
                san.setDiaChi(rs.getString("DiaChi"));
                san.setSucChua(rs.getInt("SucChua"));
                list.add(san);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int addStadium(MODEL_SAN newStadium) throws Exception {
        String query = "{CALL InsertSan(?, ?, ?, ?)}";
        int newStadiumId = -1;

        try (CallableStatement cstmt = conn.prepareCall(query)) {
            // Set the IN parameters
            cstmt.setString(1, newStadium.getTenSan());
            cstmt.setString(2, newStadium.getDiaChi());
            cstmt.setLong(3, newStadium.getSucChua());

            // Register the OUT parameter for MaSan
            cstmt.registerOutParameter(4, Types.NUMERIC);

            // Execute the procedure
            cstmt.execute();

            // Retrieve the generated MaSan
            newStadiumId = cstmt.getInt(4);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error while adding stadium: " + e.getMessage());
        }

        return newStadiumId;
    }

    //    POSITION
    public List<MODEL_VITRITD> getAllPositions() {
        String sql = "SELECT * FROM VITRITD";
        List<MODEL_VITRITD> positions = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                MODEL_VITRITD position = new MODEL_VITRITD();
                position.setMaViTri(rs.getInt("MAVT"));
                position.setTenViTri(rs.getString("TENVT"));
                positions.add(position);
            }
            return positions;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPositionById(int maVT) {
        String sql = "SELECT TenVT FROM VITRITD WHERE MAVT = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maVT);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("TenVT");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPositionIdByName(String value) {
        String sql = "SELECT MAVT FROM VITRITD WHERE TenVT = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MAVT");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    private boolean isPositionExists(int maVT) throws SQLException {
        String query = "SELECT COUNT(*) FROM VITRITD WHERE MaVT = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, maVT);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    //    PLAYER
    public MODEL_CAUTHU getPlayerFromRs(ResultSet rs) throws Exception {
        MODEL_CAUTHU player = new MODEL_CAUTHU();
        player.setMaCT(rs.getInt("MaCT"));
        player.setTenCT(rs.getString("TenCT"));
        player.setNgaysinh(rs.getDate("NgaySinh"));
        player.setQuocTich(rs.getString("QuocTich"));
        player.setSoAo(rs.getInt("SoAo"));
        player.setAvatar(rs.getString("Avatar"));
        player.setLoaiCT(rs.getInt("LoaiCT"));
        player.setMaCLB(rs.getInt("MaCLB"));
        player.setMaVT(rs.getInt("MaVT"));
        return player;
    }

    public MODEL_CAUTHU getPlayerById(int playerId) throws Exception {
        MODEL_CAUTHU player = null;
        String query = "SELECT * FROM CAUTHU WHERE MaCT = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, playerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    player = getPlayerFromRs(rs);
                }
            }
        }
        return player;
    }

    public List<MODEL_CAUTHU> getPlayersByClubId(int clubId) {
        List<MODEL_CAUTHU> players = new ArrayList<>();
        String sql = "SELECT * FROM CAUTHU " +
                "WHERE MaCLB = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MODEL_CAUTHU player = getPlayerFromRs(rs);
                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return players;
    }

    public List<MODEL_CAUTHU> getAllPlayers() {
        List<MODEL_CAUTHU> players = new ArrayList<>();
        String sql = "SELECT * FROM CAUTHU";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                MODEL_CAUTHU player = getPlayerFromRs(rs);
                players.add(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return players;

    }

    public int addPlayer(MODEL_CAUTHU player) throws Exception {
        // Validate MaCLB
        if (!isClubExists(player.getMaCLB())) {
            throw new Exception("Club with ID " + player.getMaCLB() + " does not exist.");
        }

        // Validate MaVT
        if (!isPositionExists(player.getMaVT())) {
            throw new Exception("Position with ID " + player.getMaVT() + " does not exist.");
        }
        String query = "{CALL InsertPlayer(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        int newPlayerId = -1;

        try (CallableStatement cstmt = conn.prepareCall(query)) {
            // Set the IN parameters
            cstmt.setString(1, player.getTenCT());
            cstmt.setDate(2, new Date(player.getNgaysinh().getTime()));
            cstmt.setString(3, player.getQuocTich());
            cstmt.setString(4, player.getAvatar());
            cstmt.setInt(5, player.getSoAo());
            cstmt.setInt(6, player.getLoaiCT());
            cstmt.setInt(7, player.getMaCLB());
            cstmt.setInt(8, player.getMaVT());

            // Register the OUT parameter for MaCT
            cstmt.registerOutParameter(9, Types.NUMERIC);

            // Execute the procedure
            cstmt.execute();

            // Retrieve the generated MaCT
            newPlayerId = cstmt.getInt(9);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error while adding player: " + e.getMessage());
        }

        return newPlayerId;
    }

    public void updatePlayer(MODEL_CAUTHU player) throws Exception {
        String query = "UPDATE CAUTHU SET TenCT = ?, NgaySinh = ?, QuocTich = ?, SoAo = ?, MaVT=?,Avatar=? WHERE MaCT = ?";

        try (
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getTenCT());
            pstmt.setDate(2, new Date(player.getNgaysinh().getTime()));
            pstmt.setString(3, player.getQuocTich());
            pstmt.setInt(4, player.getSoAo());
            pstmt.setInt(5, player.getMaVT());
            pstmt.setString(6, player.getAvatar());
            pstmt.setInt(7, player.getMaCT());

            pstmt.executeUpdate();
        }
    }

    public void removePlayer(int maCT) {
        String sql = "DELETE FROM CAUTHU WHERE MaCT = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maCT);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //    QUYDINH
    public void insertDefaultQD(int maMG) {
        String sql = "{Call InsertQuyDinhForMuaGiai(?)}";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maMG);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MODEL_QUYDINH getQDByMaMG(int maMG) {
        String sql = "SELECT * FROM QUYDINH WHERE MaMG = ?";

        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maMG);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MODEL_QUYDINH quyDinh = new MODEL_QUYDINH();
                    quyDinh.setMaMG(rs.getInt("MaMG"));
                    quyDinh.setTuoiToiThieu(rs.getInt("TuoiToiThieu"));
                    quyDinh.setTuoiToiDa(rs.getInt("TuoiToiDa"));
                    quyDinh.setSoCTToiThieu(rs.getInt("SoCTToiThieu"));
                    quyDinh.setSoCTToiDa(rs.getInt("SoCTToiDa"));
                    quyDinh.setSoCTNuocNgoaiToiDa(rs.getInt("SoCTNuocNgoaiToiDa"));
                    quyDinh.setPhutGhiBanToiDa(rs.getInt("PhutGhiBanToiDa"));

                    return quyDinh;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateQD(MODEL_QUYDINH quyDinh) {
        String sql = "UPDATE QUYDINH SET TuoiToiThieu = ?, TuoiToiDa = ?, SoCTToiThieu = ?, SoCTToiDa = ?, SoCTNuocNgoaiToiDa = ?, PhutGhiBanToiDa = ? WHERE MaMG = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quyDinh.getTuoiToiThieu());
            pstmt.setInt(2, quyDinh.getTuoiToiDa());
            pstmt.setInt(3, quyDinh.getSoCTToiThieu());
            pstmt.setInt(4, quyDinh.getSoCTToiDa());
            pstmt.setInt(5, quyDinh.getSoCTNuocNgoaiToiDa());
            pstmt.setInt(6, quyDinh.getPhutGhiBanToiDa());
            pstmt.setInt(7, quyDinh.getMaMG());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addQD(MODEL_QUYDINH quyDinh) {
        String sql = "INSERT INTO QUYDINH (MaMG, TuoiToiThieu, TuoiToiDa, SoCTToiThieu, SoCTToiDa, SoCTNuocNgoaiToiDa, PhutGhiBanToiDa) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quyDinh.getMaMG());
            pstmt.setInt(2, quyDinh.getTuoiToiThieu());
            pstmt.setInt(3, quyDinh.getTuoiToiDa());
            pstmt.setInt(4, quyDinh.getSoCTToiThieu());
            pstmt.setInt(5, quyDinh.getSoCTToiDa());
            pstmt.setInt(6, quyDinh.getSoCTNuocNgoaiToiDa());
            pstmt.setInt(7, quyDinh.getPhutGhiBanToiDa());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //    REGISTRATION
    public boolean checkRegistration(int maCLB, int maMG) {
        String sql = "SELECT COUNT(*) FROM CLB_THAMGIAMUAGIAI WHERE MaCLB = ? AND MaMG = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maCLB);
            pstmt.setInt(2, maMG);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addRegistration(int maCLB, int maMG, List<Integer> playerIds) throws SQLException {
        String sql = "{call RegisterforSeason(?, ?, ?)}";
        CallableStatement cstmt = null;
        Array playerArray = null;
        try {
            OracleConnection oracleConn = conn.unwrap(OracleConnection.class);
            Integer[] playerIdArr = playerIds.toArray(new Integer[0]);
            playerArray = oracleConn.createOracleArray("SYS.ODCINUMBERLIST", playerIdArr);

            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, maCLB);
            cstmt.setInt(2, maMG);
            cstmt.setArray(3, playerArray);

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cstmt != null) cstmt.close();
            if (playerArray != null) playerArray.free();
        }
    }

    public List<MODEL_CAUTHUTHAMGIA_GIAIDAU> getRegistedPlayers(int maCLB, int maMG) {
        List<MODEL_CAUTHUTHAMGIA_GIAIDAU> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM CAUTHU_THAMGIAMUAGIAI WHERE MaCLB = ? AND MaMG = ?";
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maCLB);
            pstmt.setInt(2, maMG);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MODEL_CAUTHUTHAMGIA_GIAIDAU cauThu = new MODEL_CAUTHUTHAMGIA_GIAIDAU();
                    cauThu.setMaMG(rs.getInt("MaMG"));
                    cauThu.setMaCLB(rs.getInt("MaCLB"));
                    cauThu.setMaCT(rs.getInt("MaCT"));
                    danhSach.add(cauThu);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    public boolean removeRegistration(int maCLB, int maMG) {


        // Xóa cầu thủ tham gia
        String sqlCauThu = "{Call CancelClubRegistration(?,?)}";
        try (CallableStatement pstmtCauThu = conn.prepareCall(sqlCauThu)) {
            pstmtCauThu.setInt(1, maCLB);
            pstmtCauThu.setInt(2, maMG);
            pstmtCauThu.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<MODEL_CAUTHUTHAMGIA_GIAIDAU> getRegistedPlayersByCondition(String s) {
        List<MODEL_CAUTHUTHAMGIA_GIAIDAU> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM CAUTHU_THAMGIAMUAGIAI WHERE " + s;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MODEL_CAUTHUTHAMGIA_GIAIDAU cauThu = new MODEL_CAUTHUTHAMGIA_GIAIDAU();
                cauThu.setMaMG(rs.getInt("MaMG"));
                cauThu.setMaCLB(rs.getInt("MaCLB"));
                cauThu.setMaCT(rs.getInt("MaCT"));
                danhSach.add(cauThu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public List<Integer> getRegistedClubIdsByTournament(int maMG) {
        List<Integer> clubIds = new ArrayList<>();
        String sql = "SELECT MaCLB FROM CLB_THAMGIAMUAGIAI WHERE MaMG = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maMG);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clubIds.add(rs.getInt("MaCLB"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubIds;
    }

    //    GOAL
    public MODEL_BANTHANG getGoalFromRs(ResultSet rs) throws Exception {
        MODEL_BANTHANG modelBanthang = new MODEL_BANTHANG();
        modelBanthang.setMaBT(rs.getInt("MaBT"));
        modelBanthang.setMaCT(rs.getInt("MaCT"));
        modelBanthang.setMaTD(rs.getInt("MaTD"));
        modelBanthang.setPhutGhiBan(rs.getInt("PhutGhiBan"));
        modelBanthang.setmaLoaiBT(rs.getInt("MaLoaiBT"));
        return modelBanthang;
    }

    public List<MODEL_BANTHANG> getGoalByCondition(String s) {
        String sql = "SELECT * FROM BANTHANG WHERE " + s;
        List<MODEL_BANTHANG> danhSach = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSach.add(getGoalFromRs(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return danhSach;
    }

    public int deleteGoal(MODEL_BANTHANG modelBanthang) throws SQLException {
        String sql = "{call DeleteGoal(?)}";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelBanthang.getMaBT());
            return ps.executeUpdate();
        }
    }

    public void updateGoal(MODEL_BANTHANG modelBanthang) throws SQLException {
        String sql = "{call UpdateGoal(?,?,?,?,?)}";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelBanthang.getMaCT());
            ps.setInt(2, modelBanthang.getMaTD());
            ps.setInt(3, modelBanthang.getPhutGhiBan());
            ps.setInt(4, modelBanthang.getmaLoaiBT());
            ps.setInt(5, modelBanthang.getMaBT());
            ps.executeUpdate();
        }
    }

    public int insertGoal(MODEL_BANTHANG modelBanthang) throws SQLException {
        String sql = "{call InsertGoal (?, ?, ?, ?, ?)}";
        int newGoalId = -1;
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, modelBanthang.getMaCT());
            cstmt.setInt(2, modelBanthang.getMaTD());
            cstmt.setInt(3, modelBanthang.getPhutGhiBan());
            cstmt.setInt(4, modelBanthang.getmaLoaiBT());
            cstmt.registerOutParameter(5, Types.INTEGER); // Đăng ký để nhận MaBT
            cstmt.executeUpdate();
            newGoalId = cstmt.getInt(5); // Lấy MaBT từ OUT parameter
        }
        return newGoalId;
    }


    public MODEL_LOAIBANTHANG getGoalTypeFromId(int maLoaiBT) {
        String sql = "SELECT * FROM LOAIBANTHANG WHERE MaLoaiBT = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maLoaiBT);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                MODEL_LOAIBANTHANG loaiBanThang = new MODEL_LOAIBANTHANG();
                loaiBanThang.setMaLoaiBT(rs.getInt("MaLoaiBT"));
                loaiBanThang.setTenLoaiBT(rs.getString("TenLoaiBT"));
                return loaiBanThang;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MODEL_LOAIBANTHANG> getGoalTypesByName(String name) {
        String sql = "SELECT * FROM LOAIBANTHANG WHERE LOWER(TenLoaiBT) LIKE LOWER(?)";
        List<MODEL_LOAIBANTHANG> goalTypes = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MODEL_LOAIBANTHANG goalType = new MODEL_LOAIBANTHANG();
                goalType.setMaLoaiBT(rs.getInt("MaLoaiBT"));
                goalType.setTenLoaiBT(rs.getString("TenLoaiBT"));
                goalTypes.add(goalType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goalTypes;
    }

    //    RANKING
    public MODEL_BXH_CLB getBxhFromRs(ResultSet rs) throws Exception {
        MODEL_BXH_CLB modelBxhClb = new MODEL_BXH_CLB();
        modelBxhClb.setMaMG(rs.getInt("MaMG"));
        modelBxhClb.setMaCLB(rs.getInt("MaCLB"));
        modelBxhClb.setHang(rs.getInt("Hang"));
        modelBxhClb.setSoTran(rs.getInt("SoTran"));
        modelBxhClb.setThang(rs.getInt("Thang"));
        modelBxhClb.setHoa(rs.getInt("Hoa"));
        modelBxhClb.setThua(rs.getInt("Thua"));
        modelBxhClb.setHieuSo(rs.getInt("HieuSo"));
        modelBxhClb.setDiem(rs.getInt("Diem"));
        return modelBxhClb;
    }

    public List<MODEL_BXH_CLB> getBxhCLBByTournamentId(int maMG) {
        List<MODEL_BXH_CLB> bxhClbList = new ArrayList<>();
        String sql = "SELECT * FROM BANGXEPHANG_CLB WHERE MaMG = ? ORDER BY Hang ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maMG);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MODEL_BXH_CLB bxhClb = getBxhFromRs(rs);
                bxhClbList.add(bxhClb);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return bxhClbList;
    }

    public MODEL_BXH_CLB getBxhCLBByCLBName(String clbName) {
        String sql = "SELECT bxh.* FROM BANGXEPHANG_CLB bxh " +
                "JOIN CLB clb on bxh.MaCLB = clb.MaCLB " +
                "WHERE TenCLB = ? ";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clbName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return getBxhFromRs(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public List<MODEL_BXH_BANTHANG> getBxhBanThangByTournamentId(int maMG) {
        List<MODEL_BXH_BANTHANG> bxhBanThangList = new ArrayList<>();
        String sql = "SELECT * FROM BANGXEPHANG_BANTHANG WHERE MaMG = ? ORDER BY XepHang ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maMG);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MODEL_BXH_BANTHANG modelBxhCT = new MODEL_BXH_BANTHANG();
                modelBxhCT.setMaMG(rs.getInt("MaMG"));
                modelBxhCT.setMaCT(rs.getInt("MaCT"));
                modelBxhCT.setHang(rs.getInt("XepHang"));
                modelBxhCT.setSoBanThang(rs.getInt("SoBanThang"));
                modelBxhCT.setPenalty(rs.getInt("Penalty"));
                bxhBanThangList.add(modelBxhCT);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bxhBanThangList;
    }

    public ArrayList<MODEL_BXH_CLB> getAllBxhCLB() {
        ArrayList<MODEL_BXH_CLB> bxhClbList = new ArrayList<>();
        String sql = "SELECT * FROM BANGXEPHANG_CLB ORDER BY Hang";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                MODEL_BXH_CLB bxhClb = getBxhFromRs(rs);
                bxhClbList.add(bxhClb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bxhClbList;
    }


    public MODEL_SAN getStadiumById(int maSan) {
        String sql = "SELECT * FROM SAN WHERE MaSan = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSan);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                MODEL_SAN san = new MODEL_SAN();
                san.setMaSan(rs.getInt("MaSan"));
                san.setTenSan(rs.getString("TenSAN"));
                san.setDiaChi(rs.getString("DiaChi"));
                san.setSucChua(rs.getInt("SucChua"));
                return san;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MODEL_SAN getStadiumByName(String tenSan) {
        String sql = "SELECT * FROM SAN WHERE TenSan = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenSan);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                MODEL_SAN san = new MODEL_SAN();
                san.setMaSan(rs.getInt("MaSan"));
                san.setTenSan(rs.getString("TenSAN"));
                san.setDiaChi(rs.getString("DiaChi"));
                san.setSucChua(rs.getInt("SucChua"));
                return san;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getPLayerTypeById(int maLoaiCT) {
        String sql = "SELECT TenLoaiCT FROM LOAICAUTHU WHERE MaLoaiCT = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maLoaiCT);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("TenLoaiCT");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }


    public MODEL_VONGDAU getRoundByName(String value) {
        String sql = "SELECT * FROM VONGDAU WHERE LOWER(TENVD) = LOWER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                MODEL_VONGDAU vd = new MODEL_VONGDAU();
                vd.setMaVD(rs.getInt("MAVD"));
                vd.setTenVD(rs.getString("TENVD"));
                vd.setMaMG(rs.getInt("MAMG"));
                vd.setNgayBD(rs.getDate("NGAYBD"));
                vd.setNgayKT(rs.getDate("NGAYKT"));
                return vd;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MODEL_THUTU_UUTIEN> getPriorityOrderByTournament(int maMG) {
        List<MODEL_THUTU_UUTIEN> list = new ArrayList<>();
        String sql = "SELECT TieuChi, DoUuTien FROM THUTU_UUTIEN WHERE MaMG = ? ORDER BY DoUuTien ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maMG);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tenTTUT = rs.getString("TieuChi");
                int thuTu = rs.getInt("DoUuTien");
                list.add(new MODEL_THUTU_UUTIEN(maMG, tenTTUT, thuTu));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean savePriorityOrder(int maMG, List<MODEL_THUTU_UUTIEN> list) {
        String updateSql = "UPDATE THUTU_UUTIEN SET DoUuTien = ? WHERE MaMG = ? AND TieuChi = ?";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                for (MODEL_THUTU_UUTIEN item : list) {
                    updatePs.setInt(1, item.getThuTu());
                    updatePs.setInt(2, maMG);
                    updatePs.setString(3, item.getTenTTUT());
                    updatePs.addBatch();
                }
                updatePs.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
