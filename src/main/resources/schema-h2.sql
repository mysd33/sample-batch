/* Partition Stepのローカルでの動作確認用 */
/* ユーザーマスタ */
CREATE TABLE IF NOT EXISTS m_user (
    user_id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100),
    user_name VARCHAR(100),
    birthday DATE,
    role VARCHAR(50)
);

/* ユーザーの一時テーブル */
CREATE TABLE IF NOT EXISTS m_user_temp (
    user_id VARCHAR(50) PRIMARY KEY,
    user_name VARCHAR(100),
    age INT
);