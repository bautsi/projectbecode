package juanan.WEBINF.classes.demo.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;

import juanan.WEBINF.classes.demo.app.WarehouseHelper;
import juanan.WEBINF.classes.tools.JsonReader;
import javax.servlet.annotation.WebServlet;

@WebServlet("/api/warehouse.do")
public class WarehouseController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private WarehouseHelper wh = WarehouseHelper.getHelper();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonReader jsr = new JsonReader(request);
        String action = jsr.getParameter("action");

        JSONObject resp = new JSONObject();
        if ("getAll".equals(action)) {
            resp = wh.getAllWarehouses();
        } else {
            // 其他 GET 请求的处理
        }

        jsr.response(resp, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonReader jsr = new JsonReader(request);
        JSONObject jso = jsr.getObject();
        String action = jso.optString("action");

        JSONObject resp = new JSONObject();
        switch (action) {
            case "add":
                String warehouseName = jso.getString("warhouse_name");
                String warehouseLocation = jso.getString("warhouse_location");
                int managerId = jso.getInt("manager_id");
                resp = wh.addWarehouse(warehouseName, warehouseLocation, managerId);
                break;
            case "delete":
                int warehouseId = jso.getInt("warhouse_id");
                resp = wh.deleteWarehouse(warehouseId);
                break;
            case "update":
                warehouseId = jso.getInt("warhouse_id");
                warehouseName = jso.getString("warhouse_name");
                warehouseLocation = jso.getString("warhouse_location");
                resp = wh.updateWarehouse(warehouseId, warehouseName, warehouseLocation);
                break;
            default:
                resp.put("status", "400");
                resp.put("message", "Invalid request operation");
                break;
        }

        jsr.response(resp, response);
    }
}
