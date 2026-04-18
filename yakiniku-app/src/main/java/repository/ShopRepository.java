package repository;

import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ShopRepository {

    // 接続確認用
    public void testConnection() {
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("DB接続成功！（Repository経由）");
        } catch (Exception e) {
            System.out.println("DB接続失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 店舗一覧取得（確認用）
    public void findAllShops() {
        String sql = "SELECT id, shop_name, address FROM shops";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            System.out.println("=== 店舗一覧 ===");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("shop_name");
                String address = rs.getString("address");

                System.out.println(id + " : " + name + " / " + address);
            }
        } catch (Exception e) {
            System.out.println("取得失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }
}