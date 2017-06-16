DROP TABLE IF EXISTS users_chats;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS chats;
DROP TRIGGER IF EXISTS chat_consistency
ON messages;
DROP FUNCTION IF EXISTS check_chat_consistency();
CREATE TABLE users (
  id       BIGSERIAL PRIMARY KEY,
  login    VARCHAR(30) NOT NULL UNIQUE,
  password INT         NOT NULL,
  about    TEXT
);

CREATE TABLE chats (
  id   BIGSERIAL PRIMARY KEY,
  name VARCHAR(50)
);

CREATE TABLE users_chats (
  chat_id BIGINT NOT NULL REFERENCES chats (id),
  user_id BIGINT NOT NULL REFERENCES users (id),
  CONSTRAINT users_chats_unique UNIQUE (chat_id, user_id)
);

CREATE TABLE messages (
  id        BIGSERIAL PRIMARY KEY,
  chat_id   BIGINT                   NOT NULL REFERENCES chats (id),
  sender_id BIGINT                   NOT NULL REFERENCES users (id),
  text      TEXT                     NOT NULL,
  time      TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE OR REPLACE FUNCTION check_chat_consistency()
  RETURNS TRIGGER AS $chat_consistency$
DECLARE
  cou INTEGER;
  rec RECORD;
BEGIN
  cou := 0;
  FOR rec IN EXECUTE 'SELECT * FROM users_chats WHERE chat_id = ' || NEW.chat_id || ' AND user_id = ' ||
                     NEW.sender_id LOOP
    cou := cou + 1;
  END LOOP;
  IF cou = 0
  THEN
    RAISE 'User % is not in chat №%', NEW.sender_id, NEW.chat_id;
  END IF;
  RETURN NEW;
END;
$chat_consistency$ LANGUAGE plpgsql STABLE;

CREATE TRIGGER chat_consistency
BEFORE INSERT OR UPDATE
  ON messages
FOR EACH ROW EXECUTE PROCEDURE check_chat_consistency();

CREATE OR REPLACE FUNCTION create_chat(chat_name VARCHAR(50), ids BIGINT [])
  RETURNS BIGINT AS $$
DECLARE
  new_chat_id BIGINT DEFAULT 0;
  i           INTEGER;
BEGIN
  IF array_length(ids, 1) = 2
  THEN
    SELECT chat_id
    INTO new_chat_id
    FROM users_chats
    GROUP BY chat_id
    HAVING SUM(CASE WHEN user_id IN (ids [1], ids [2])
      THEN 1
               ELSE -1 END) = 2
    LIMIT 1;
    IF new_chat_id != 0
    THEN
      RETURN new_chat_id;
    END IF;
  END IF;
  INSERT INTO chats (name) VALUES (chat_name)
  RETURNING id
    INTO new_chat_id;
  FOR i IN 1..array_length(ids, 1) LOOP
    INSERT INTO users_chats (chat_id, user_id) VALUES (new_chat_id, ids [i]);
  END LOOP;
  RETURN new_chat_id;
END;
$$ LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION get_chat_history(sender BIGINT, chat BIGINT)
  RETURNS SETOF MESSAGES AS $$
BEGIN
  IF EXISTS(SELECT *
            FROM users_chats
            WHERE chat_id = chat AND user_id = sender)
  THEN
    RETURN QUERY
    SELECT *
    FROM messages
    WHERE chat_id = chat
    ORDER BY time DESC
    LIMIT 10;
  ELSE
    RAISE 'User % is not in chat №%', sender, chat;
  END IF;
END;
$$ LANGUAGE plpgsql STABLE;