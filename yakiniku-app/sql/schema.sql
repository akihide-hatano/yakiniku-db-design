CREATE TABLE yakiniku_groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL
);

CREATE TABLE shops (
    id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT,
    shop_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    FOREIGN KEY (group_id) REFERENCES yakiniku_groups(id)
);

CREATE TABLE group_menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT,
    menu_name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES yakiniku_groups(id)
);

CREATE TABLE shop_menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    shop_id INT,
    menu_name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    FOREIGN KEY (shop_id) REFERENCES shops(id)
);