DROP TABLE IF EXISTS users_chats;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS chats;
DROP TRIGGER IF EXISTS chat_consistency ON messages;
DROP FUNCTION IF EXISTS check_chat_consistency();
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  login VARCHAR(30) NOT NULL UNIQUE,
  password INT NOT NULL,
  about TEXT
);

CREATE TABLE chats (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50)
);

CREATE TABLE users_chats (
  chat_id BIGINT NOT NULL REFERENCES chats(id),
  user_id BIGINT NOT NULL REFERENCES users(id),
  CONSTRAINT users_chats_unique UNIQUE (chat_id, user_id)
);

CREATE TABLE messages (
  id BIGSERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL REFERENCES chats(id),
  sender_id BIGINT NOT NULL REFERENCES users(id),
  text TEXT NOT NULL,
  time TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE OR REPLACE FUNCTION check_chat_consistency() RETURNS TRIGGER AS $chat_consistency$
DECLARE
  cou INTEGER;
  rec RECORD;
BEGIN
  cou := 0;
  FOR rec IN EXECUTE 'SELECT * FROM users_chats WHERE chat_id = ' || NEW.chat_id || ' AND user_id = ' || NEW.sender_id LOOP
    cou := cou + 1;
  END LOOP;
  IF cou = 0 THEN
    RAISE 'User % is not in chat №%', NEW.sender_id, NEW.chat_id;
  END IF;
  RETURN NEW;
END;
$chat_consistency$ LANGUAGE plpgsql;

CREATE TRIGGER chat_consistency BEFORE INSERT OR UPDATE
ON messages
FOR EACH ROW EXECUTE PROCEDURE check_chat_consistency();