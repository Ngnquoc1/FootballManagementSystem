package Service;

import Controller.Connection.DatabaseConnection;
import Controller.RegistrationController;
import DAO.DAO_MUAGIAI;
import DAO.DAO_Match;
import Model.*;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.time.LocalDate;
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
                user.setVaiTro(rs.getString("VaiTro"));
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

    //    MATCH
    public Map<LocalDate, List<Match>> getUpcomingMatchs() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = new HashMap<>();
        ResultSet rs = null;
        DAO_Match daoMatch = new DAO_Match();
        try (CallableStatement cstmt = conn.prepareCall("{call GetUpComingMatches(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR); // Đăng ký tham số đầu ra là cursor
            cstmt.execute();
            // Lấy ResultSet từ cursor
            rs = (ResultSet) cstmt.getObject(1);

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("ThoiGian");
                LocalDate ngayThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Match match = daoMatch.getFromRs(rs);
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
        DAO_Match daoMatch = new DAO_Match();
        try (CallableStatement cstmt = conn.prepareCall("{call GetResultedMatches(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            rs = (ResultSet) cstmt.getObject(1);
            while (rs.next()) {
                Match match = daoMatch.getFromRs(rs);
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
        DAO_Match daoMatch = new DAO_Match();
        try (CallableStatement cstmt = conn.prepareCall("{call GetPendingMatches(?)}")) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            rs = (ResultSet) cstmt.getObject(1);
            while (rs.next()) {
                Match match = daoMatch.getFromRs(rs);
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
            cstmt.registerOutParameter(6, java.sql.Types.NUMERIC);

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

    public List<MODEL_CLB> selectAllClubs() {
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

    public int getCLBIDFromGoal(int maCT, int maTD) throws SQLException {
        String sql = "SELECT cclb.MaCLB " +
                "FROM BANTHANG bt " +
                "JOIN TranDau td ON bt.MaTD = td.MaTD " +
                "JOIN VongDau vd ON td.MaVD = vd.MaVD " +
                "JOIN CAUTHU_CLB cclb ON bt.MaCT = cclb.MaCT AND cclb.MaMG = vd.MaMG " +
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
    //    Tournament
    public List<MODEL_MUAGIAI> selectAllTournament() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        return new DAO_MUAGIAI().selectAllDB();
    }

    public List<MODEL_MUAGIAI> getAllActiveTournaments() {
        List<MODEL_MUAGIAI> list = new ArrayList<>();
        String sql = "SELECT * FROM MUAGIAI WHERE NGAYKHAIMAC <= SYSDATE AND NGAYBEMAC >= SYSDATE";

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

    public void updateRanking(int MaTD) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "{call UpdateRanking(?)}";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, MaTD);
            ps.executeUpdate();
        }
    }

    //    ROUND
    public List<MODEL_VONGDAU> selectAllByTournament(int maMG) {
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

        return 1;
    }

    public boolean insertVD(MODEL_VONGDAU vd) {
        String sql = "INSERT INTO VONGDAU (MAVD, TENVD, MAMG, NGAYBD, NGAYKT) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vd.getMaVD());
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
            cstmt.registerOutParameter(4, java.sql.Types.NUMERIC);

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
            cstmt.setDate(2, new java.sql.Date(player.getNgaysinh().getTime()));
            cstmt.setString(3, player.getQuocTich());
            cstmt.setString(4, player.getAvatar());
            cstmt.setInt(5, player.getSoAo());
            cstmt.setInt(6, player.getLoaiCT());
            cstmt.setInt(7, player.getMaCLB());
            cstmt.setInt(8, player.getMaVT());

            // Register the OUT parameter for MaCT
            cstmt.registerOutParameter(9, java.sql.Types.NUMERIC);

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
            pstmt.setDate(2, new java.sql.Date(player.getNgaysinh().getTime()));
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

    //    REGISTRATION
    public boolean checkRegistration(int maCLB, int maMG) {
        String sql = "SELECT COUNT(*) FROM CAUTHU_CLB WHERE MaCLB = ? AND MaMG = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
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

    public List<MODEL_CAUTHUTHAMGIACLB> getRegistedPlayers(int maCLB, int maMG) {
        List<MODEL_CAUTHUTHAMGIACLB> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM CAUTHUTHAMGIACLB WHERE MaCLB = ? AND MaMG = ?";
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maCLB);
            pstmt.setInt(2, maMG);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MODEL_CAUTHUTHAMGIACLB cauThu = new MODEL_CAUTHUTHAMGIACLB();
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

    public boolean addRegistration(int maCLB, int maMG, List<RegistrationController.CauThuViewModel> danhSachCauThu) {
        try {
            conn.setAutoCommit(false);
            // Đăng ký CLB tham gia mùa giải
            String sqlCLB = "{call RegisterClubForSeason(?, ?)}";
            try (PreparedStatement pstmtCLB = conn.prepareStatement(sqlCLB)) {
                pstmtCLB.setInt(1, maCLB);
                pstmtCLB.setInt(2, maMG);
                pstmtCLB.executeUpdate();
            }

            // Đăng ký cầu thủ tham gia
            String sqlCauThu = "INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (?, ?, ?)";
            try (PreparedStatement pstmtCauThu = conn.prepareStatement(sqlCauThu)) {
                for (RegistrationController.CauThuViewModel cauThu : danhSachCauThu) {
                    pstmtCauThu.setInt(1, maMG);
                    pstmtCauThu.setInt(2, maCLB);
                    pstmtCauThu.setInt(3, cauThu.getMaCT());
                    pstmtCauThu.addBatch();
                }
                pstmtCauThu.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean removeRegistration(int maCLB, int maMG) {
        try {
            conn.setAutoCommit(false);

            // Xóa cầu thủ tham gia
            String sqlCauThu = "DELETE FROM CAUTHUTHAMGIACLB WHERE MaCLB = ? AND MaMG = ?";
            try (PreparedStatement pstmtCauThu = conn.prepareStatement(sqlCauThu)) {
                pstmtCauThu.setInt(1, maCLB);
                pstmtCauThu.setInt(2, maMG);
                pstmtCauThu.executeUpdate();
            }

            // Xóa CLB tham gia mùa giải
            String sqlCLB = "DELETE FROM CAUTHU_CLB WHERE MaCLB = ? AND MaMG = ?";
            try (PreparedStatement pstmtCLB = conn.prepareStatement(sqlCLB)) {
                pstmtCLB.setInt(1, maCLB);
                pstmtCLB.setInt(2, maMG);
                pstmtCLB.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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


}
