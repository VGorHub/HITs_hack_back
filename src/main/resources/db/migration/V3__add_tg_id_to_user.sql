-- tg_id ------------------------------------------------------------------------------------------
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS tg_id BIGINT;

-- уникальность (индекс вместо inline-constraint даёт IF NOT EXISTS)
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_tg_id ON users(tg_id);

-- связь tg_id ↔ chat_id --------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS telegram_chats (
    tg_id   BIGINT  PRIMARY KEY,
    chat_id BIGINT  NOT NULL
);