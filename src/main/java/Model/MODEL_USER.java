/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author My PC
 */
public class MODEL_USER {
    private String userName, passWord;
    private String vaiTro;

    public MODEL_USER() {
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getVaiTroText() {
        if (vaiTro == null)
            return "Không xác định";
        switch (vaiTro) {
            case "A":
                return "Quản trị viên";
            case "B":
                return "Nhân viên";
            case "C":
                return "Quản lý";
            case "D":
                return "Khách";
            default:
                return "Không xác định";
        }
    }
}
