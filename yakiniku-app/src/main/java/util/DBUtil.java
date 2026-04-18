//DBにつなぐためのクラス

package util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

// DBにつなぐための共通クラス
public final class DBUtil {

    // DB接続情報をプロパティファイルから読み込むための定数
    private static final String PROPERTIES_FILE = "db.properties";
    private static final String KEY_URL = "db.url";
    private static final String KEY_USER = "db.user";
    private static final String KEY_PASSWORD = "db.password";


    private DBUtil() {
        // インスタンス化させない
    }

    // DB接続を取得するメソッド
    public static Connection getConnection() throws Exception {
        Properties properties = loadProperties();

        String url = properties.getProperty(KEY_URL);
        String user = properties.getProperty(KEY_USER);
        String password = properties.getProperty(KEY_PASSWORD);

        validateProperties(url, user, password);

        return DriverManager.getConnection(url, user, password);
    }

    // プロパティファイルを読み込むメソッド
    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new IllegalStateException(PROPERTIES_FILE + " が見つかりません");
            }
            properties.load(input);
            return properties;
        } catch (Exception e) {
            throw new IllegalStateException("DB設定ファイルの読み込みに失敗しました", e);
        }
    }

    // プロパティの値を検証するメソッド
    private static void validateProperties(String url, String user, String password) {
        if (isBlank(url)) {
            throw new IllegalStateException(KEY_URL + " が設定されていません");
        }
        if (isBlank(user)) {
            throw new IllegalStateException(KEY_USER + " が設定されていません");
        }
        if (isBlank(password)) {
            throw new IllegalStateException(KEY_PASSWORD + " が設定されていません");
        }
    }

    // 文字列が空かどうかを判定するメソッド
    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}