INSERT INTO yakiniku_groups (group_name)
VALUES ('Yakiniku Tokyo');

INSERT INTO shops (group_id, shop_name, address)
VALUES
(1, 'Yakiniku Tokyo 新宿本店', '東京都新宿区'),
(1, 'Yakiniku Tokyo 新宿西口店', '東京都新宿区'),
(1, 'Yakiniku Tokyo 新宿三丁目店', '東京都新宿区'),
(1, 'Yakiniku Tokyo 新宿二丁目店', '東京都新宿区'),
(1, 'Yakiniku Tokyo 歌舞伎町店', '東京都新宿区'),
(1, 'Yakiniku Tokyo 東新宿店', '東京都新宿区'),
(1, 'Yakiniku Tokyo 代々木店', '東京都渋谷区'),
(1, 'Yakiniku Tokyo 高田馬場店', '東京都新宿区'),
(1, 'Yakiniku Tokyo 渋谷店', '東京都渋谷区'),
(1, 'Yakiniku Tokyo 池袋店', '東京都豊島区');


-- 共通メニュー
INSERT INTO group_menu_items (group_id, menu_name, price)
VALUES
(1, 'カルビ', 800),
(1, 'ロース', 900),
(1, 'タン', 1000),
(1, 'ハラミ', 1100),
(1, 'レバー', 700),
(1, 'ホルモン', 750);

-- 店舗独自メニュー
INSERT INTO shop_menu_items (shop_id, menu_name, price)
VALUES
(1, '特選カルビ', 1500),
(1, '厚切りタン', 1800),
(2, '新宿限定ハラミ', 1300),
(3, '三丁目スペシャル', 1400),
(5, '歌舞伎町プレミアム盛り', 2000),
(7, '代々木ヘルシーセット', 1200),
(10, '池袋限定ミックス', 1350);


