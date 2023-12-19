package juanan.WEBINF.classes.demo.app;

import java.sql.*;
import org.json.*;

import juanan.WEBINF.classes.demo.util.DBMgr;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * The Class ManagerHelper<br>
 * ManagerHelper類別（class）主要管理所有與Manager相關與資料庫之方法（method）
 * </p>
 * 
 * @author IPLab
 * @version 1.0.0
 * @since 1.0.0
 */

public class ManagerHelper {
    
    /**
     * 實例化（Instantiates）一個新的（new）ManagerHelper物件<br>
     * 採用Singleton不需要透過new
     */
    private ManagerHelper() {
        
    }
    
    /** 靜態變數，儲存ManagerHelper物件 */
    private static ManagerHelper mh;
    
    /** 儲存JDBC資料庫連線 */
    private Connection conn = null;
    
    /** 儲存JDBC預準備之SQL指令 */
    private PreparedStatement pres = null;
    
    /**
     * 靜態方法<br>
     * 實作Singleton（單例模式），僅允許建立一個ManagerHelper物件
     *
     * @return the helper 回傳ManagerHelper物件
     */
    public static ManagerHelper getHelper() {
        /** Singleton檢查是否已經有ManagerHelper物件，若無則new一個，若有則直接回傳 */
        if(mh == null) mh = new ManagerHelper();
        
        return mh;
    }
    
    /**
     * 透過管理員編號（manager_id）刪除管理員
     *
     * @param manager_id 管理員編號
     * @return the JSONObject 回傳SQL執行結果
     */
    public JSONObject deleteById(int manager_id) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            
            /** SQL指令 */
            String sql = "DELETE FROM `missa`.`manager` WHERE `manager_id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setInt(1, manager_id);
            /** 執行刪除之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }

        /** 將SQL指令與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);

        return response;
    }
    
    /**
     * 取回所有管理員資料
     *
     * @return the JSONObject 回傳SQL執行結果與自資料庫取回之所有資料
     */
    public JSONObject getAll() {
        /** 新建一個 Manager 物件之 m 變數，用於紀錄每一位查詢回之管理員資料 */
        Manager m = null;
        /** 用於儲存所有檢索回之管理員，以JSONArray方式儲存 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `missa`.`manager`";
            
            /** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
            pres = conn.prepareStatement(sql);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int manager_manager_id = rs.getInt("manager_id");
                String manager_name = rs.getString("manager_name");
                String manager_email = rs.getString("manager_email");
                String manager_password = rs.getString("manager_password");
                
                /** 將每一筆管理員資料產生一名新Manager物件 */
                m = new Manager(manager_manager_id, manager_email, manager_password, manager_name);
                /** 取出該名管理員之資料並封裝至 JSONsonArray 內 */
                jsa.put(m.getData());
            }

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        
        /** 將SQL指令、花費時間、影響行數與所有管理員資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("data", jsa);

        return response;
    }
    
    /**
     * 透過管理員編號（manager_id）取得管理員資料
     *
     * @param manager_id 管理員編號
     * @return the JSON object 回傳SQL執行結果與該管理員編號之管理員資料
     */
    public JSONObject getById(String manager_id) {
        /** 新建一個 Manager 物件之 m 變數，用於紀錄每一位查詢回之管理員資料 */
        Manager m = null;
        /** 用於儲存所有檢索回之管理員，以JSONArray方式儲存 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄SQL總行數 */
        int row = 0;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT * FROM `missa`.`manager` WHERE `manager_id` = ? LIMIT 1";
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, manager_id);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);
            
            /** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
            /** 正確來說資料庫只會有一筆該管理員編號之資料，因此其實可以不用使用 while 迴圈 */
            while(rs.next()) {
                /** 每執行一次迴圈表示有一筆資料 */
                row += 1;
                
                /** 將 ResultSet 之資料取出 */
                int manager_manager_id = rs.getInt("manager_id");
                String manager_name = rs.getString("manager_name");
                String manager_email = rs.getString("manager_email");
                String manager_password = rs.getString("manager_password");
                
                /** 將每一筆管理員資料產生一名新Manager物件 */
                m = new Manager(manager_manager_id, manager_name, manager_email, manager_password);
                /** 取出該名管理員之資料並封裝至 JSONsonArray 內 */
                jsa.put(m.getData());
            }
            
        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        
        /** 將SQL指令、花費時間、影響行數與所有管理員資料之JSONArray，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("data", jsa);

        return response;
    }
    
    /**
     * 檢查該名管理員之電子郵件信箱是否重複註冊
     *
     * @param m 一名管理員之Manager物件
     * @return boolean 若重複註冊回傳False，若該信箱不存在則回傳True
     */
    public boolean checkDuplicate(Manager m){
        /** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
        int row = -1;
        /** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
        ResultSet rs = null;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "SELECT count(*) FROM `missa`.`manager` WHERE `manager_email` = ?";
            
            /** 取得所需之參數 */
            String manager_email = m.getEmail();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, manager_email);
            /** 執行查詢之SQL指令並記錄其回傳之資料 */
            rs = pres.executeQuery();

            /** 讓指標移往最後一列，取得目前有幾行在資料庫內 */
            rs.next();
            row = rs.getInt("count(*)");
            System.out.print(row);

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(rs, pres, conn);
        }
        
        /** 
         * 判斷是否已經有一筆該電子郵件信箱之資料
         * 若無一筆則回傳False，否則回傳True 
         */
        return (row == 0) ? false : true;
    }
    
    /**
     * 建立該名管理員至資料庫
     *
     * @param m 一名管理員之Manager物件
     * @return the JSON object 回傳SQL指令執行之結果
     */
    public JSONObject create(Manager m) {
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄SQL總行數 */
        int row = 0;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "INSERT INTO `missa`.`manager`(`manager_name`, `manager_email`, `manager_password`)"
                    + " VALUES(?, ?, ?)";
            
            /** 取得所需之參數 */
            String manager_name = m.getName();
            String manager_email = m.getEmail();
            String manager_password = m.getPassword();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, manager_name);
            pres.setString(2, manager_email);
            pres.setString(3, manager_password);
            
            /** 執行新增之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();
            
            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(pres, conn);
        }


        /** 將SQL指令與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);

        return response;
    }
    
    /**
     * 更新一名管理員之管理員資料
     *
     * @param m 一名管理員之Manager物件
     * @return the JSONObject 回傳SQL指令執行結果與執行之資料
     */
    public JSONObject update(Manager m) {
        /** 紀錄回傳之資料 */
        JSONArray jsa = new JSONArray();
        /** 記錄實際執行之SQL指令 */
        String exexcute_sql = "";
        /** 紀錄SQL總行數 */
        int row = 0;
        
        try {
            /** 取得資料庫之連線 */
            conn = DBMgr.getConnection();
            /** SQL指令 */
            String sql = "Update `missa`.`manager` SET `manager_name` = ? ,`manager_password` = ? WHERE `manager_email` = ?";
            /** 取得所需之參數 */
            String manager_name = m.getName();
            String manager_email = m.getEmail();
            String manager_password = m.getPassword();
            
            /** 將參數回填至SQL指令當中 */
            pres = conn.prepareStatement(sql);
            pres.setString(1, manager_name);
            pres.setString(2, manager_password);
            pres.setString(3, manager_email);
            /** 執行更新之SQL指令並記錄影響之行數 */
            row = pres.executeUpdate();

            /** 紀錄真實執行的SQL指令，並印出 **/
            exexcute_sql = pres.toString();
            System.out.println(exexcute_sql);

        } catch (SQLException e) {
            /** 印出JDBC SQL指令錯誤 **/
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            /** 若錯誤則印出錯誤訊息 */
            e.printStackTrace();
        } finally {
            /** 關閉連線並釋放所有資料庫相關之資源 **/
            DBMgr.close(pres, conn);
        }
        
        /** 將SQL指令與影響行數，封裝成JSONObject回傳 */
        JSONObject response = new JSONObject();
        response.put("sql", exexcute_sql);
        response.put("row", row);
        response.put("data", jsa);

        return response;
    }

    /**
     * 验证管理員的登录凭据
     *
     * @param email 管理员的电子邮件
     * @param password 管理员的密码
     * @return 如果凭据有效，则返回 true，否则返回 false
     */
    public boolean validateLogin(String email, String password) {
        ResultSet rs = null;

        try {
            conn = DBMgr.getConnection();
            String sql = "SELECT count(*) FROM `missa`.`manager` WHERE `manager_email` = ? AND `manager_password` = ?";
            
            pres = conn.prepareStatement(sql);
            pres.setString(1, email);
            pres.setString(2, password);
            rs = pres.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMgr.close(rs, pres, conn);
        }

        return false;
    }
}