package Service;

import Model.*;
import Util.AlertUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExportService {
    private final Service service = new Service();

    public void exportClubRankingsToExcel(ObservableList<MODEL_BXH_CLB> vleagueClubRankings, File file, String tournamentName, String rankingType) {
        try (FileOutputStream fileOut = new FileOutputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet sheet = workbook.createSheet("Club Rankings");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Info style
            CellStyle infoStyle = workbook.createCellStyle();
            Font infoFont = workbook.createFont();
            infoFont.setBold(true);
            infoFont.setFontHeightInPoints((short) 11);
            infoStyle.setFont(infoFont);

            // Cell style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Number style
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(cellStyle);
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0"));

            // Top 3 styles
            CellStyle goldStyle = workbook.createCellStyle();
            goldStyle.cloneStyleFrom(cellStyle);
            goldStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            goldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle silverStyle = workbook.createCellStyle();
            silverStyle.cloneStyleFrom(cellStyle);
            silverStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            silverStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle bronzeStyle = workbook.createCellStyle();
            bronzeStyle.cloneStyleFrom(cellStyle);
            bronzeStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            bronzeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Info rows
            Row tournamentRow = sheet.createRow(0);
            Cell tournamentCell = tournamentRow.createCell(0);
            tournamentCell.setCellValue("Mùa giải: " + tournamentName);
            tournamentCell.setCellStyle(infoStyle);

            Row rankingTypeRow = sheet.createRow(1);
            Cell rankingTypeCell = rankingTypeRow.createCell(0);
            rankingTypeCell.setCellValue("Loại BXH: " + rankingType);
            rankingTypeCell.setCellStyle(infoStyle);

            // Header
            String[] headers = {"MaMG", "MaCLB", "Tên CLB", "Hang", "SoTran", "Thang", "Hoa", "Thua", "HieuSo", "Diem"};
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            for (int i = 0; i < vleagueClubRankings.size(); i++) {
                MODEL_BXH_CLB clb = vleagueClubRankings.get(i);
                Row row = sheet.createRow(i + 4);

                CellStyle rowStyle = cellStyle;
                if (i == 0) rowStyle = goldStyle;
                else if (i == 1) rowStyle = silverStyle;
                else if (i == 2) rowStyle = bronzeStyle;

                int col = 0;
                Cell c1 = row.createCell(col++);
                c1.setCellValue(clb.getMaGD());
                c1.setCellStyle(numberStyle);

                Cell c2 = row.createCell(col++);
                c2.setCellValue(clb.getMaCLB());
                c2.setCellStyle(numberStyle);

                String clubName = "Unknown";
                try {
                    clubName = service.getCLBByID(clb.getMaCLB()).getTenCLB();
                } catch (Exception ignored) {
                }
                Cell c3 = row.createCell(col++);
                c3.setCellValue(clubName);
                c3.setCellStyle(rowStyle);

                Cell c4 = row.createCell(col++);
                c4.setCellValue(clb.getHang());
                c4.setCellStyle(numberStyle);

                Cell c5 = row.createCell(col++);
                c5.setCellValue(clb.getSoTran());
                c5.setCellStyle(numberStyle);

                Cell c6 = row.createCell(col++);
                c6.setCellValue(clb.getThang());
                c6.setCellStyle(numberStyle);

                Cell c7 = row.createCell(col++);
                c7.setCellValue(clb.getHoa());
                c7.setCellStyle(numberStyle);

                Cell c8 = row.createCell(col++);
                c8.setCellValue(clb.getThua());
                c8.setCellStyle(numberStyle);

                Cell c9 = row.createCell(col++);
                c9.setCellValue(clb.getHieuSo());
                c9.setCellStyle(numberStyle);

                Cell c10 = row.createCell(col);
                c10.setCellValue(clb.getDiem());
                c10.setCellStyle(numberStyle);

                // Apply background for top 3
                for (int j = 0; j < headers.length; j++) {
                    row.getCell(j).setCellStyle(rowStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Error");
                alert.setHeaderText("Cannot export report");
                alert.setContentText("Please check if the file is open or the path is valid.");
                alert.showAndWait();
            });
        }
    }

    public void exportScorerRankingsToExcel(ObservableList<MODEL_BXH_BANTHANG> vleagueScorerRankings, File file, String tournamentName, String rankingType) {
        try (FileOutputStream fileOut = new FileOutputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet sheet = workbook.createSheet("Scorer Rankings");

            // Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle infoStyle = workbook.createCellStyle();
            Font infoFont = workbook.createFont();
            infoFont.setBold(true);
            infoFont.setFontHeightInPoints((short) 11);
            infoStyle.setFont(infoFont);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(cellStyle);
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0"));

            CellStyle goldStyle = workbook.createCellStyle();
            goldStyle.cloneStyleFrom(cellStyle);
            goldStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            goldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle silverStyle = workbook.createCellStyle();
            silverStyle.cloneStyleFrom(cellStyle);
            silverStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            silverStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle bronzeStyle = workbook.createCellStyle();
            bronzeStyle.cloneStyleFrom(cellStyle);
            bronzeStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            bronzeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Info rows
            Row tournamentRow = sheet.createRow(0);
            Cell tournamentCell = tournamentRow.createCell(0);
            tournamentCell.setCellValue("Mùa giải: " + tournamentName);
            tournamentCell.setCellStyle(infoStyle);

            Row rankingTypeRow = sheet.createRow(1);
            Cell rankingTypeCell = rankingTypeRow.createCell(0);
            rankingTypeCell.setCellValue("Loại BXH: " + rankingType);
            rankingTypeCell.setCellStyle(infoStyle);

            // Header
            String[] headers = {"MaMG", "MaCT", "Hang", "SoBanThang", "Penalty"};
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            for (int i = 0; i < vleagueScorerRankings.size(); i++) {
                MODEL_BXH_BANTHANG ranking = vleagueScorerRankings.get(i);
                Row row = sheet.createRow(i + 4);

                CellStyle rowStyle = cellStyle;
                if (i == 0) rowStyle = goldStyle;
                else if (i == 1) rowStyle = silverStyle;
                else if (i == 2) rowStyle = bronzeStyle;

                int col = 0;
                Cell c1 = row.createCell(col++);
                c1.setCellValue(ranking.getMaGD());
                c1.setCellStyle(numberStyle);

                Cell c2 = row.createCell(col++);
                c2.setCellValue(ranking.getMaCT());
                c2.setCellStyle(numberStyle);

                Cell c3 = row.createCell(col++);
                c3.setCellValue(ranking.getHang());
                c3.setCellStyle(numberStyle);

                Cell c4 = row.createCell(col++);
                c4.setCellValue(ranking.getSoBanThang());
                c4.setCellStyle(numberStyle);

                Cell c5 = row.createCell(col);
                c5.setCellValue(ranking.getPenalty());
                c5.setCellStyle(numberStyle);

                // Apply background for top 3
                for (int j = 0; j < headers.length; j++) {
                    row.getCell(j).setCellStyle(rowStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Error");
                alert.setHeaderText("Cannot export report");
                alert.setContentText("Please check if the file is open or the path is valid.");
                alert.showAndWait();
            });
        }
    }

    public File exportTournamentInfo(int tournamentId) {
        try {
            MODEL_GIAIDAU tournament = service.getTournamentByID(tournamentId);
            if (tournament == null) {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi", "", "Không tìm thấy giải đấu với mã: " + tournamentId);
                });
                return null;
            }

            File file = new File("src/main/resources/Export/TournamentInfo_" + tournament.getMaGD() + ".xlsx");
            try (FileOutputStream fileOut = new FileOutputStream(file);
                 XSSFWorkbook workbook = new XSSFWorkbook()) {

                XSSFSheet sheet = workbook.createSheet("Tournament Info");

                // Styles
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12);
                headerStyle.setFont(headerFont);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                CellStyle infoStyle = workbook.createCellStyle();
                Font infoFont = workbook.createFont();
                infoFont.setBold(true);
                infoFont.setFontHeightInPoints((short) 11);
                infoStyle.setFont(infoFont);

                // Info rows
                Row tournamentRow = sheet.createRow(0);
                Cell tournamentCell = tournamentRow.createCell(0);
                tournamentCell.setCellValue("Mã giải đấu: " + tournament.getMaGD());
                tournamentCell.setCellStyle(infoStyle);

                Row nameRow = sheet.createRow(1);
                Cell nameCell = nameRow.createCell(0);
                nameCell.setCellValue("Tên giải đấu: " + tournament.getTenGD());
                nameCell.setCellStyle(infoStyle);

                Row startDateRow = sheet.createRow(2);
                Cell startDateCell = startDateRow.createCell(0);
                startDateCell.setCellValue("Ngày bắt đầu: " + tournament.getNgayBD());
                startDateCell.setCellStyle(infoStyle);

                Row endDateRow = sheet.createRow(3);
                Cell endDateCell = endDateRow.createCell(0);
                endDateCell.setCellValue("Ngày kết thúc: " + tournament.getNgayKT());
                endDateCell.setCellStyle(infoStyle);

                String qd = service.getTournamentInfoAndRules(tournamentId);
                Row rulesRow = sheet.createRow(4);
                Cell rulesCell = rulesRow.createCell(0);
                rulesCell.setCellValue("Quy định: " + qd);

                // **This line is required to write the workbook to the file**
                workbook.write(fileOut);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                AlertUtils.showError("Lỗi", "Không thể xuất thông tin giải đấu", e.getMessage());
            });
            return null;
        }
    }
    public static void exportTeamStatsToExcel(List<TeamStatistics> teamStats, String filePath, String season) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Thống kê đội bóng");

        // Tạo header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"STT", "Đội bóng", "Số trận", "Thắng", "Hòa", "Thua", "Bàn thắng", "Bàn thua", "Hiệu số", "Điểm"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Thêm dữ liệu
        int rowNum = 1;
        for (TeamStatistics stats : teamStats) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(stats.getTeamName());
            row.createCell(2).setCellValue(stats.getMatchesPlayed());
            row.createCell(3).setCellValue(stats.getWins());
            row.createCell(4).setCellValue(stats.getDraws());
            row.createCell(5).setCellValue(stats.getLosses());
            row.createCell(6).setCellValue(stats.getGoalsFor());
            row.createCell(7).setCellValue(stats.getGoalsAgainst());
            row.createCell(8).setCellValue(stats.getGoalDifference());
            row.createCell(9).setCellValue(stats.getPoints());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    /**
     * Xuất thống kê cầu thủ ra file Excel
     */
    public static void exportPlayerStatsToExcel(List<PlayerStatistics> playerStats, String filePath, String season) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Thống kê cầu thủ");

        // Tạo header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"STT", "Cầu thủ", "Đội bóng", "Vị trí", "Số trận", "Bàn thắng", "Kiến tạo", "Thẻ vàng", "Thẻ đỏ"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Thêm dữ liệu
        int rowNum = 1;
        for (PlayerStatistics stats : playerStats) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(stats.getPlayerName());
            row.createCell(2).setCellValue(stats.getTeamName());
            row.createCell(3).setCellValue(stats.getPosition());
            row.createCell(4).setCellValue(stats.getMatches());
            row.createCell(5).setCellValue(stats.getGoals());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    /**
     * Xuất thống kê trận đấu ra file Excel
     */
    public static void exportMatchStatsToExcel(List<MatchStatistics> matchStats, String filePath, String season) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Thống kê trận đấu");

        // Tạo header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"STT", "Ngày giờ", "Vòng đấu", "Đội nhà", "Đội khách", "Tỷ số", "Sân đấu", "Khán giả"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Thêm dữ liệu
        int rowNum = 1;
        for (MatchStatistics stats : matchStats) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(stats.getDateTime().toString());
            row.createCell(2).setCellValue("Vòng " + stats.getRound());
            row.createCell(3).setCellValue(stats.getHomeTeam());
            row.createCell(4).setCellValue(stats.getAwayTeam());
            row.createCell(5).setCellValue(stats.getHomeGoals() + " - " + stats.getAwayGoals());
            row.createCell(6).setCellValue(stats.getStadium());
            row.createCell(7).setCellValue(stats.getAttendance());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    /**
     * Xuất thống kê đội bóng ra file PDF
     */
    public static void exportTeamStatsToPDF(List<TeamStatistics> teamStats, String filePath, String season) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Thêm tiêu đề
        Font titleFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph title = new Paragraph("THỐNG KÊ ĐỘI BÓNG - MÙA GIẢI " + season, (com.itextpdf.text.Font) titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Tạo bảng
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"STT", "Đội bóng", "Số trận", "Thắng", "Hòa", "Thua", "Bàn thắng", "Bàn thua", "Hiệu số", "Điểm"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Dữ liệu
        int index = 1;
        for (TeamStatistics stats : teamStats) {
            table.addCell(String.valueOf(index++));
            table.addCell(stats.getTeamName());
            table.addCell(String.valueOf(stats.getMatchesPlayed()));
            table.addCell(String.valueOf(stats.getWins()));
            table.addCell(String.valueOf(stats.getDraws()));
            table.addCell(String.valueOf(stats.getLosses()));
            table.addCell(String.valueOf(stats.getGoalsFor()));
            table.addCell(String.valueOf(stats.getGoalsAgainst()));
            table.addCell(String.valueOf(stats.getGoalDifference()));
            table.addCell(String.valueOf(stats.getPoints()));
        }

        document.add(table);
        document.close();
    }

    /**
     * Xuất thống kê cầu thủ ra file PDF
     */
    public static void exportPlayerStatsToPDF(List<PlayerStatistics> playerStats, String filePath, String season) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Thêm tiêu đề
        Font titleFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph title = new Paragraph("THỐNG KÊ CẦU THỦ - MÙA GIẢI " + season, (com.itextpdf.text.Font) titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Tạo bảng
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"STT", "Cầu thủ", "Đội bóng", "Vị trí", "Số trận", "Bàn thắng", "Kiến tạo", "Thẻ vàng", "Thẻ đỏ"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Dữ liệu
        int index = 1;
        for (PlayerStatistics stats : playerStats) {
            table.addCell(String.valueOf(index++));
            table.addCell(stats.getPlayerName());
            table.addCell(stats.getTeamName());
            table.addCell(stats.getPosition());
            table.addCell(String.valueOf(stats.getMatches()));
            table.addCell(String.valueOf(stats.getGoals()));

        }

        document.add(table);
        document.close();
    }

    /**
     * Xuất thống kê trận đấu ra file PDF
     */
    public static void exportMatchStatsToPDF(List<MatchStatistics> matchStats, String filePath, String season) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Thêm tiêu đề
        Font titleFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph title = new Paragraph("THỐNG KÊ TRẬN ĐẤU - MÙA GIẢI " + season, (com.itextpdf.text.Font) titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Tạo bảng
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"STT", "Ngày giờ", "Vòng đấu", "Đội nhà", "Đội khách", "Tỷ số", "Sân đấu", "Khán giả"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Dữ liệu
        int index = 1;
        for (MatchStatistics stats : matchStats) {
            table.addCell(String.valueOf(index++));
            table.addCell(stats.getDateTime().toString());
            table.addCell("Vòng " + stats.getRound());
            table.addCell(stats.getHomeTeam());
            table.addCell(stats.getAwayTeam());
            table.addCell(stats.getHomeGoals() + " - " + stats.getAwayGoals());
            table.addCell(stats.getStadium());
            table.addCell(String.valueOf(stats.getAttendance()));
        }

        document.add(table);
        document.close();
    }

    /**
     * Xuất thống kê theo tuần ra file Excel
     */
    public static void exportWeeklyStatsToExcel(List<WeeklyStatistics> weeklyStats, String filePath, boolean isWeeklyMode) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(isWeeklyMode ? "Thống kê theo tuần" : "Thống kê theo vòng đấu");

        // Tạo header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"STT", "Thời gian", "Số trận", "Tổng bàn thắng", "TB bàn thắng", "Tổng khán giả", "TB khán giả", "Tổng thẻ phạt", "Thẻ vàng", "Thẻ đỏ"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Thêm dữ liệu
        int rowNum = 1;
        for (WeeklyStatistics stats : weeklyStats) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(stats.getPeriod());
            row.createCell(2).setCellValue(stats.getMatchCount());
            row.createCell(3).setCellValue(stats.getGoalCount());
            row.createCell(4).setCellValue(stats.getAvgGoals());

        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    /**
     * Xuất thống kê theo tuần ra file PDF
     */
    public static void exportWeeklyStatsToPDF(List<WeeklyStatistics> weeklyStats, String filePath,  boolean isWeeklyMode,String season) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Thêm tiêu đề
        Font titleFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        String title = isWeeklyMode ? "THỐNG KÊ THEO TUẦN - MÙA GIẢI " + season : "THỐNG KÊ THEO VÒNG ĐẤU - MÙA GIẢI " + season;
        Paragraph titleParagraph = new Paragraph(title, (com.itextpdf.text.Font) titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);
        document.add(new Paragraph(" "));

        // Tạo bảng
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"STT", "Thời gian", "Số trận", "Tổng bàn thắng", "TB bàn thắng", "Tổng khán giả", "TB khán giả", "Tổng thẻ phạt", "Thẻ vàng", "Thẻ đỏ"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Dữ liệu
        int index = 1;
        for (WeeklyStatistics stats : weeklyStats) {
            table.addCell(String.valueOf(index++));
            table.addCell(stats.getPeriod());
            table.addCell(String.valueOf(stats.getMatchCount()));
            table.addCell(String.valueOf(stats.getGoalCount()));
            table.addCell(String.format("%.2f", stats.getAvgGoals()));

        }

        document.add(table);
        document.close();
    }

    /**
     * Xuất báo cáo so sánh ra file Excel
     */
    public static void exportComparisonToExcel(List<ComparisonData> comparisonData, String filePath, String period1, String period2, String analysis) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo so sánh");

        // Tạo header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Thêm tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("SO SÁNH GIỮA " + period1 + " VÀ " + period2);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

        // Tạo header row
        Row headerRow = sheet.createRow(2);
        String[] headers = {"Chỉ số", period1, period2, "Chênh lệch", "Phần trăm"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Thêm dữ liệu
        int rowNum = 3;
        for (ComparisonData data : comparisonData) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(data.getMetric());
            row.createCell(1).setCellValue(data.getPeriod1Value());
            row.createCell(2).setCellValue(data.getPeriod2Value());
            row.createCell(3).setCellValue(data.getDifference());
            row.createCell(4).setCellValue(data.getPercentage());
        }

        // Thêm phân tích
        if (analysis != null && !analysis.trim().isEmpty()) {
            Row analysisHeaderRow = sheet.createRow(rowNum + 2);
            Cell analysisHeaderCell = analysisHeaderRow.createCell(0);
            analysisHeaderCell.setCellValue("PHÂN TÍCH:");
            analysisHeaderCell.setCellStyle(headerStyle);

            Row analysisRow = sheet.createRow(rowNum + 3);
            Cell analysisCell = analysisRow.createCell(0);
            analysisCell.setCellValue(analysis);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum + 3, rowNum + 3, 0, 4));
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    /**
     * Xuất báo cáo so sánh ra file PDF
     */
    public static void exportComparisonToPDF(List<ComparisonData> comparisonData, String filePath, String period1, String period2, String analysis) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Thêm tiêu đề
        Font titleFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph title = new Paragraph("SO SÁNH GIỮA " + period1 + " VÀ " + period2, (com.itextpdf.text.Font) titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Tạo bảng
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"Chỉ số", period1, period2, "Chênh lệch", "Phần trăm"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Dữ liệu
        for (ComparisonData data : comparisonData) {
            table.addCell(data.getMetric());
            table.addCell(data.getPeriod1Value());
            table.addCell(data.getPeriod2Value());
            table.addCell(data.getDifference());
            table.addCell(data.getPercentage());
        }

        document.add(table);

        // Thêm phân tích
        if (analysis != null && !analysis.trim().isEmpty()) {
            document.add(new Paragraph(" "));
            Font analysisHeaderFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Paragraph analysisHeader = new Paragraph("PHÂN TÍCH:", (com.itextpdf.text.Font) analysisHeaderFont);
            document.add(analysisHeader);
            document.add(new Paragraph(" "));

            Paragraph analysisParagraph = new Paragraph(analysis, FontFactory.getFont(FontFactory.HELVETICA, 12));
            document.add(analysisParagraph);
        }

        document.close();
    }

    /**
     * Xuất báo cáo tổng hợp ra file Excel
     */
    public static void exportSummaryToExcel(Map<String, Object> summaryData, String filePath, String season, boolean isWeeklyMode) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // Sheet tổng quan
        Sheet overviewSheet = workbook.createSheet("Tổng quan");
        createOverviewSheet(overviewSheet, summaryData, workbook);

        // Sheet thống kê chi tiết
        Sheet detailSheet = workbook.createSheet("Thống kê chi tiết");
        createDetailSheet(detailSheet, summaryData, workbook, isWeeklyMode);

        // Ghi file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    /**
     * Xuất báo cáo tổng hợp ra file PDF
     */
    public static void exportSummaryToPDF(Map<String, Object> summaryData, String filePath, String season, boolean isWeeklyMode) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Thêm tiêu đề
        Font titleFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
        String title = "BÁO CÁO TỔNG HỢP THỐNG KÊ " + (isWeeklyMode ? "THEO TUẦN" : "THEO VÒNG ĐẤU");
        Paragraph titleParagraph = new Paragraph(title, (com.itextpdf.text.Font) titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);
        document.add(new Paragraph(" "));

        // Thông tin giải đấu
        Font infoFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        document.add(new Paragraph("Giải đấu: " + summaryData.get("tournament"), (com.itextpdf.text.Font) infoFont));
        document.add(new Paragraph("Mùa giải: " + summaryData.get("season"), (com.itextpdf.text.Font) infoFont));
        document.add(new Paragraph("Chế độ xem: " + summaryData.get("mode"), (com.itextpdf.text.Font) infoFont));
        document.add(new Paragraph(" "));

        // Thống kê tổng quan
        Font sectionFont = (Font) FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
        document.add(new Paragraph("THỐNG KÊ TỔNG QUAN", (com.itextpdf.text.Font) sectionFont));
        document.add(new Paragraph(" "));

        PdfPTable overviewTable = new PdfPTable(2);
        overviewTable.setWidthPercentage(60);

        addOverviewRow(overviewTable, "Tổng số trận đấu:", (String) summaryData.get("totalMatches"));
        addOverviewRow(overviewTable, "Tổng số bàn thắng:", (String) summaryData.get("totalGoals"));
        addOverviewRow(overviewTable, "Trung bình bàn thắng/trận:", (String) summaryData.get("avgGoalsPerMatch"));
        addOverviewRow(overviewTable, "Tổng số thẻ phạt:", (String) summaryData.get("totalCards"));
        addOverviewRow(overviewTable, "Trung bình khán giả:", (String) summaryData.get("avgAttendance"));

        document.add(overviewTable);
        document.close();
    }

    private static void createOverviewSheet(Sheet sheet, Map<String, Object> summaryData, Workbook workbook) {
        // Tạo style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO TỔNG HỢP");
        titleCell.setCellStyle(headerStyle);

        // Thông tin cơ bản
        int rowNum = 2;
        createInfoRow(sheet, rowNum++, "Giải đấu:", (String) summaryData.get("tournament"));
        createInfoRow(sheet, rowNum++, "Mùa giải:", (String) summaryData.get("season"));
        createInfoRow(sheet, rowNum++, "Chế độ xem:", (String) summaryData.get("mode"));

        rowNum++;

        // Thống kê tổng quan
        Row overviewHeaderRow = sheet.createRow(rowNum++);
        Cell overviewHeaderCell = overviewHeaderRow.createCell(0);
        overviewHeaderCell.setCellValue("THỐNG KÊ TỔNG QUAN");
        overviewHeaderCell.setCellStyle(headerStyle);

        createInfoRow(sheet, rowNum++, "Tổng số trận đấu:", (String) summaryData.get("totalMatches"));
        createInfoRow(sheet, rowNum++, "Tổng số bàn thắng:", (String) summaryData.get("totalGoals"));
        createInfoRow(sheet, rowNum++, "Trung bình bàn thắng/trận:", (String) summaryData.get("avgGoalsPerMatch"));
        createInfoRow(sheet, rowNum++, "Tổng số thẻ phạt:", (String) summaryData.get("totalCards"));
        createInfoRow(sheet, rowNum++, "Trung bình khán giả:", (String) summaryData.get("avgAttendance"));
    }

    private static void createDetailSheet(Sheet sheet, Map<String, Object> summaryData, Workbook workbook, boolean isWeeklyMode) {
        @SuppressWarnings("unchecked")
        List<WeeklyStatistics> weeklyStats = (List<WeeklyStatistics>) summaryData.get("weeklyStatistics");

        if (weeklyStats == null || weeklyStats.isEmpty()) {
            return;
        }

        // Tạo header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"STT", "Thời gian", "Số trận", "Tổng bàn thắng", "TB bàn thắng", "Tổng khán giả", "TB khán giả", "Tổng thẻ phạt"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Dữ liệu
        int rowNum = 1;
        for (WeeklyStatistics stats : weeklyStats) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(stats.getPeriod());
            row.createCell(2).setCellValue(stats.getMatchCount());
            row.createCell(3).setCellValue(stats.getGoalCount());
            row.createCell(4).setCellValue(stats.getAvgGoals());

        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createInfoRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private static void addOverviewRow(PdfPTable table, String label, String value) {
        table.addCell(new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
        table.addCell(new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA))));
    }
}
