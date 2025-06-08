package Service;

import Model.MODEL_BXH_BANTHANG;
import Model.MODEL_BXH_CLB;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportService {
    private final Service service= new Service();
    public void exportClubRankingsToExcel(ObservableList<MODEL_BXH_CLB> vleagueClubRankings, File file, String tournamentName, String rankingType) {
        try (FileOutputStream fileOut = new FileOutputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet sheet = workbook.createSheet("Club Rankings");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short)12);
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
            infoFont.setFontHeightInPoints((short)11);
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
                c1.setCellValue(clb.getMaMG());
                c1.setCellStyle(numberStyle);

                Cell c2 = row.createCell(col++);
                c2.setCellValue(clb.getMaCLB());
                c2.setCellStyle(numberStyle);

                String clubName = "Unknown";
                try {
                    clubName = service.getCLBByID(clb.getMaCLB()).getTenCLB();
                } catch (Exception ignored) {}
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
            headerFont.setFontHeightInPoints((short)12);
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
            infoFont.setFontHeightInPoints((short)11);
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
                c1.setCellValue(ranking.getMaMG());
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
}
