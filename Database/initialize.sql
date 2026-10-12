/* 
This file is used SOLELY to set-up the database, the FIRST time it is run. Running it subsequently will 
DELETE all existing data in the database. Do NOT run it arbitrarily.

*/ 

/*
To-do list:
*/

DROP DATABASE IF EXISTS TBD_DB; -- Deletes everything.
CREATE DATABASE TBD_DB;

use TBD_DB;

-- Create the tables.

CREATE TABLE user( 
	/*
		Defines the user account, not to be conflated with Account (see below).
    */
	user_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	email_address VARCHAR(255) NOT NULL,
    credential VARCHAR(255) NOT NULL
);
  
CREATE TABLE account (
	/*
	A user-created container representing one real world source of money, such as a checking account, 
    cash wallet, or credit card. Account types are checking, savings, credit, and cash.
    */
    user_id INT UNSIGNED NOT NULL,
    account_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    account_name VARCHAR(255) DEFAULT 'unnamed account',
    account_type ENUM('checking', 'savings', 'credit', 'cash') NOT NULL,
    settings_json BLOB NOT NULL, -- Supports up to 65 KB; can be expanded if needed
    CONSTRAINT FK_account_user_id
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE category (
	category_label VARCHAR(255) NOT NULL,
    category_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT
);
INSERT INTO category(category_label)
VALUES ('Rent'), ('Utilities'), ('Bills'), ('Groceries'), ('Dining'), ('Entertainment'), ('Transportation'), ('Health and Wellness'), ('Shopping'), 
		('Education'), ('Income'), ('Other');

CREATE TABLE transaction(
	account_id BIGINT UNSIGNED NOT NULL,
    transaction_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    delta DECIMAL(22, 10) NOT NULL, -- This pessimistically assumes up to a trillion USD with extra precision for currency conversion.
    transaction_label VARCHAR(255) DEFAULT 'unlabeled transaction',
    transaction_description VARCHAR(4095) DEFAULT '',
    category INT UNSIGNED NOT NULL,
    transaction_date DATE NOT NULL,
    CONSTRAINT FK_transaction_account_id
    FOREIGN KEY (account_id) REFERENCES account(account_id),
    CONSTRAINT FK_transaction_category
    FOREIGN KEY (category) REFERENCES category(category_id)
);

CREATE TABLE budget(
-- Connects accounts with budget_entry
	month DATE,
    account_id BIGINT UNSIGNED NOT NULL,
    budget_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    CONSTRAINT FK_budget_account_id
    FOREIGN KEY (account_id) REFERENCES account(account_id),
    CONSTRAINT budget_unique
    UNIQUE(month, account_id)
);

CREATE TABLE budget_entry(
-- Defines the components of a budget
	budget_id BIGINT UNSIGNED NOT NULL,
    category INT UNSIGNED NOT NULL,
    alloted DECIMAL(22, 10) NOT NULL, -- This pessimistically assumes up to a trillion USD with extra precision for currency conversion.
    PRIMARY KEY(budget_id, category),
    CONSTRAINT FK_budget_entry_budget_id
    FOREIGN KEY (budget_id) REFERENCES budget(budget_id),
    CONSTRAINT FK_budget_entry_category
    FOREIGN KEY (category) REFERENCES category(category_id)
);

CREATE table FailedAttempt(
	username_entry VARCHAR(255),
	password_entry VARCHAR(255),
    logtime DATETIME,
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY
);

SHOW TABLES; -- Shows that tables exist




-- Test stuff; delete later
INSERT INTO user(email_address, credential)
VALUES ('bob@hotmail.com', 'bobizcool');

SELECT * FROM user; -- Passes

INSERT INTO account(user_id, account_name, account_type, settings_json)
VALUES (1, 'Bob\'s checking', 'checking', '{}'),
		(1, 'Bob\'s savings', 'savings', '{}');

SELECT * FROM account; -- Passes

INSERT INTO transaction(account_id, delta, category, transaction_date) 
VALUES (1, -100, 1, CURDATE());
INSERT INTO transaction(account_id, delta, category, transaction_date) 
VALUES (2, 100, 1, CURDATE() - 1);

SELECT * FROM transaction; -- Passes

INSERT INTO budget(month, account_id)
VALUES ('2026-09-01', 1), ('2026-09-01', 2);

SELECT * FROM budget; -- Passes

INSERT INTO budget_entry(budget_id, category, alloted) VALUES 
(1, 1, 50),
(1, 2, 75),
(2, 1, 0),
(2, 2, 0);

SELECT * FROM budget_entry; -- Passes