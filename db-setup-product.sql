CREATE TABLE product (
  id     BIGSERIAL PRIMARY KEY
, name   VARCHAR(255)
, amount BIGINT CHECK ( amount >= 0 )
, price  BIGINT CHECK ( price > 0 )
);

INSERT INTO product (name, amount, price) VALUES
  ('Product 1', 1, 10)
, ('Product 2', 2, 20)
, ('Product 3', 3, 30)
, ('Product 4', 4, 40)
, ('Product 5', 5, 50)
, ('Product 6', 60000000, 1);
