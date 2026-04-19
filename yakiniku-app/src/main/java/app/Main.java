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

        repo.insertShop(1, "Yakiniku Tokyo 上野店", "東京都台東区");
    }
}