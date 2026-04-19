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

    //店舗名検索
    public void findShopByName(String keyword){

        String sql = "SELECT id ,shop_name, address FROM shops WHERE shop_name LIKE ?";

        try (Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");

            try(ResultSet rs = stmt.executeQuery()){
                System.out.println("====== 店舗名検索 ========");

                boolean found = false;
                while (rs.next()) {
                    found =true;

                    int id = rs.getInt("id");
                    String name = rs.getString("shop_name");
                    String address = rs.getString("address");

                    System.out.println(id + " :" + name + "/" + address);
                }

                if(!found){
                    System.out.println("該当する店舗がありませんでした");
                }
            }
        } catch (Exception e) {
            System.out.println("検索失敗" + e.getMessage());
            e.printStackTrace();
        }
    }

    //店舗ID検索
    public void findShopById(int shopId){
        String sql = "SELECT id, shop_name, address FROM shops WHERE id = ?";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, shopId);

            try(ResultSet rs = stmt.executeQuery()){
                System.out.println("====== 店舗名検索 ========");

                if(rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("shop_name");
                    String address = rs.getString("address");

                    System.out.println( id + " : " + name + "/" + address);
                }else{
                    System.out.println("該当する店舗がありませんでした");
                }
            }
        } catch (Exception e) {
            System.out.println("検索失敗" + e.getMessage());
            e.printStackTrace();
        }
    }

    //店舗を追加するメソッド
    public void insertShop(int groupId,String shopName,String address){
        String sql = "INSERT INTO shops (group_id, shop_name, address) VALUES (?, ?, ?)";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, groupId);
            stmt.setString(2, shopName);
            stmt.setString(3, address);

            int count = stmt.executeUpdate();


            System.out.println("===== 店舗登録 =====");
            System.out.println(count + "件登録しました。");

        } catch (Exception e) {
            System.err.println("登録失敗" + e.getMessage());
            e.printStackTrace();
        }
    }
}