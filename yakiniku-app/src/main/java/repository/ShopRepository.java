package repository;

import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ShopRepository {

    // --------------------------------------------------
    // 1. 接続確認用メソッド
    // --------------------------------------------------
    // Repository から DB に接続できるかを確認するためのメソッド。
    // 実務では毎回使うものではないが、学習中は「本当にDBへつながるのか」を
    // 確認するのに役立つ。
    public void testConnection() {
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("DB接続成功！（Repository経由）");
        } catch (Exception e) {
            System.out.println("DB接続失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------
    // 2. 店舗一覧取得
    // --------------------------------------------------
    // shops テーブルに入っている店舗を全件表示する。
    // まずは「DBからデータを読む」基本形を理解するためのメソッド。
    public void findAllShops() {
        // 一覧取得なので、id / 店舗名 / 住所をそのまま全部読む。
        String sql = "SELECT id, shop_name, address FROM shops";

        try (
                // DB接続を作る
                Connection conn = DBUtil.getConnection();
                // SQLを実行する準備をする
                PreparedStatement stmt = conn.prepareStatement(sql);
                // SELECT を実行し、結果を ResultSet で受け取る
                ResultSet rs = stmt.executeQuery()
        ) {
            System.out.println("=== 店舗一覧 ===");

            // rs.next() は「次の1行があるか？」を確認しながら進める。
            // 一覧取得なので、複数件を読むため while を使う。
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

    // --------------------------------------------------
    // 3. 店舗名で部分一致検索
    // --------------------------------------------------
    // keyword を使って shop_name を部分一致検索する。
    // 例: 「新宿」を渡すと、「新宿本店」「新宿西口店」などが引っかかる。
    public void findShopByName(String keyword) {
        // LIKE を使うことで部分一致検索を行う。
        String sql = "SELECT id, shop_name, address FROM shops WHERE shop_name LIKE ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // %keyword% の形にすると「前後に何が付いていてもOK」の検索になる。
            stmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("====== 店舗名検索 ========");

                // 1件も見つからなかった時のためにフラグを用意する。
                boolean found = false;

                while (rs.next()) {
                    found = true;

                    int id = rs.getInt("id");
                    String name = rs.getString("shop_name");
                    String address = rs.getString("address");

                    System.out.println(id + " : " + name + " / " + address);
                }

                if (!found) {
                    System.out.println("該当する店舗がありませんでした");
                }
            }
        } catch (Exception e) {
            System.out.println("検索失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------
    // 4. 店舗IDで1件検索
    // --------------------------------------------------
    // 主キー(id)で1件だけ検索する。
    // id は一意なので、複数件取得ではなく 0件 or 1件 の想定。
    public void findShopById(int shopId) {
        String sql = "SELECT id, shop_name, address FROM shops WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // id 検索なので setInt を使う。
            stmt.setInt(1, shopId);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("====== 店舗ID検索 ========");

                // id検索は1件だけ想定なので if(rs.next()) を使う。
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("shop_name");
                    String address = rs.getString("address");

                    System.out.println(id + " : " + name + " / " + address);
                } else {
                    System.out.println("該当する店舗がありませんでした");
                }
            }
        } catch (Exception e) {
            System.out.println("検索失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------
    // 5. 店舗登録
    // --------------------------------------------------
    // 新しい店舗を shops テーブルへ登録する。
    // ただし、同じ group_id の中で同じ店舗名が既にある場合は登録しない。
    public void insertShop(int groupId, String shopName, String address) {
        String sql = "INSERT INTO shops (group_id, shop_name, address) VALUES (?, ?, ?)";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // 先に重複チェックをする。
            // ここで true が返るなら「同じグループ内に同じ店舗名が既に存在する」状態。
            if (isShopNameExists(shopName, groupId)) {
                System.out.println("同じグループ内に同名の店舗が既に存在します。登録をスキップします。");
                return;
            }

            // INSERT の ? に値を順番通りセットする。
            stmt.setInt(1, groupId);
            stmt.setString(2, shopName);
            stmt.setString(3, address);

            // INSERT / UPDATE / DELETE は executeUpdate() を使う。
            // 返り値は「何件影響したか」。
            int count = stmt.executeUpdate();

            System.out.println("===== 店舗登録 =====");
            System.out.println(count + "件登録しました。");

        } catch (Exception e) {
            System.out.println("登録失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------
    // 6. 登録時の重複チェック
    // --------------------------------------------------
    // insert 用の重複チェックメソッド。
    // 「同じ group_id の中に、同じ shop_name が既に存在するか？」を確認する。
    // 1件でも存在すれば true を返す。
    public boolean isShopNameExists(String shopName, int groupId) {
        String sql = "SELECT COUNT(*) FROM shops WHERE group_id = ? AND shop_name = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, groupId);
            stmt.setString(2, shopName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // COUNT(*) の結果は1列だけ返るので getInt(1) で取る。
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("検索失敗: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // --------------------------------------------------
    // 7. 店舗名更新
    // --------------------------------------------------
    // 店舗名だけを更新するメソッド。
    // update では次の順番で確認する。
    // ① 指定した shopId が存在するか
    // ② 今の店舗名と同じではないか
    // ③ 同じグループ内で他の店舗と重複しないか
    // ④ 問題なければ UPDATE 実行
    public void updateShopName(int shopId, String newShopName) {
        String sql = "UPDATE shops SET shop_name = ? WHERE id = ?";

        // まず、この店舗がどの group_id に属しているか確認する。
        int groupId = getGroupIdByShopId(shopId);
        if (groupId == -1) {
            System.out.println("指定された店舗が存在しません。");
            return;
        }

        // 今の店舗名を取得して、同じ値への更新なら無駄なので止める。
        String currentShopName = getShopNameByShopId(shopId);
        if (currentShopName != null && currentShopName.equals(newShopName)) {
            System.out.println("現在の店舗名と同じため、更新をスキップします。");
            return;
        }

        // update用の重複チェック。
        // insert時と違い、自分自身(id = shopId)は除外する必要がある。
        if (isShopNameExistsForUpdate(newShopName, groupId, shopId)) {
            System.out.println("同じグループ内に同名の店舗が既に存在します。更新をスキップします。");
            return;
        }

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, newShopName);
            stmt.setInt(2, shopId);

            int count = stmt.executeUpdate();
            if (count > 0) {
                System.out.println(count + "件更新しました。");
            } else {
                System.out.println("該当する店舗がありませんでした。");
            }
        } catch (Exception e) {
            System.out.println("更新失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------
    // 8. 住所更新
    // --------------------------------------------------
    // 住所だけを更新するメソッド。
    // 住所は店舗名ほど重複ルールが厳しくないので、
    // 「存在確認」と「同じ住所なら更新しない」だけを入れている。
    public void updateShopAddress(int shopId, String newAddress) {
        String sql = "UPDATE shops SET address = ? WHERE id = ?";

        // 現在の住所を取得して存在確認を行う。
        String currentAddress = getShopAddressByShopId(shopId);
        if (currentAddress == null) {
            System.out.println("指定された店舗が存在しません。");
            return;
        }

        // 今と同じ住所なら更新しない。
        if (currentAddress.equals(newAddress)) {
            System.out.println("現在の住所と同じため、更新をスキップします。");
            return;
        }

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, newAddress);
            stmt.setInt(2, shopId);

            int count = stmt.executeUpdate();
            if (count > 0) {
                System.out.println(count + "件更新しました。");
            } else {
                System.out.println("該当する店舗がありませんでした。");
            }
        } catch (Exception e) {
            System.out.println("更新失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------
    // 9. shopIdからgroup_idを取得
    // --------------------------------------------------
    // update時に「この店舗はどのグループに属しているか」を調べるために使う。
    // 店舗が存在しない場合は -1 を返す。
    public int getGroupIdByShopId(int shopId) {
        String sql = "SELECT group_id FROM shops WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, shopId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("group_id");
                }
            }
        } catch (Exception e) {
            System.out.println("group_id取得失敗: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // --------------------------------------------------
    // 10. shopIdから現在の店舗名を取得
    // --------------------------------------------------
    // update前に「今の店舗名と同じかどうか」を確認するために使う。
    public String getShopNameByShopId(int shopId) {
        String sql = "SELECT shop_name FROM shops WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, shopId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("shop_name");
                }
            }
        } catch (Exception e) {
            System.out.println("店舗名取得失敗: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // --------------------------------------------------
    // 11. shopIdから現在の住所を取得
    // --------------------------------------------------
    // update前に「今の住所と同じかどうか」を確認するために使う。
    public String getShopAddressByShopId(int shopId) {
        String sql = "SELECT address FROM shops WHERE id = ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, shopId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("address");
                }
            }
        } catch (Exception e) {
            System.out.println("住所取得失敗: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // --------------------------------------------------
    // 12. update用の重複チェック
    // --------------------------------------------------
    // update時は「自分自身」は重複扱いしたらダメなので、
    // id <> ? を使って自分を除外している。
    // つまり「同じグループ内で、同じ店舗名を持つ“他の店舗”がいるか？」を見る。
    public boolean isShopNameExistsForUpdate(String shopName, int groupId, int shopId) {
        String sql = "SELECT COUNT(*) FROM shops WHERE group_id = ? AND shop_name = ? AND id != ?";

        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, groupId);
            stmt.setString(2, shopName);
            stmt.setInt(3, shopId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("重複チェック失敗: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}