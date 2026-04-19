package app;

import repository.ShopRepository;

public class Main {
    public static void main(String[] args) {

        ShopRepository repo = new ShopRepository();

        // ① 接続確認
        repo.testConnection();

        // ② データ取得確認
        repo.findAllShops();

        repo.findShopByName("新宿");

        repo.findShopById(4);

        //店舗名更新
        repo.updateShopName(4, "新宿焼肉の名店");

        //店舗住所更新
        repo.updateShopAddress(4, "東京都新宿区新宿1-1-1");

    }
}