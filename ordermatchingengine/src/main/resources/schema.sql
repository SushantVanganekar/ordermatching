create table orderbook (
id integer identity primary key,
ordertype varchar(255),
transactiontype varchar(255),
price decimal,
quantity integer,
ordertimestamp date,
status varchar(255));