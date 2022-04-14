CREATE TABLE account (
  id      BIGSERIAL PRIMARY KEY
, name    VARCHAR(255)
, balance BIGINT CHECK ( balance >= 0 )
);

INSERT INTO account (name, balance) VALUES
  ('User 1', 10)
, ('User 2', 20)
, ('User 3', 30)
, ('User 4', 40)
, ('User 5', 50)
, ('User 6', 60000000);
