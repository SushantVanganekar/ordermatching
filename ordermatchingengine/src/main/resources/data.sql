insert into orderbook(id,ordertype,transactiontype,price,quantity,ordertimestamp,status) values
(1000L,'LIMIT', 'BUY', 100.0, 1, sysdate, 'UNMACTHED')
,(2000L,'LIMIT', 'BUY', 100.5, 10, sysdate, 'UNMACTHED')
,(3000L,'LIMIT', 'BUY', 100.3, 5, sysdate, 'UNMACTHED')
,(4000L,'MARKET', 'BUY', 9999999.9, 3, sysdate, 'UNMACTHED')
,(5000L,'LIMIT', 'BUY', 100.6, 2, sysdate, 'UNMACTHED')
,(6000L,'LIMIT', 'SELL', 100.0, 1, sysdate, 'UNMACTHED')
,(7000L,'LIMIT', 'SELL', 95.0, 12, sysdate, 'UNMACTHED')
,(8000L,'LIMIT', 'SELL', 105.0, 10, sysdate, 'UNMACTHED')
,(9000L,'MARKET', 'SELL', 0.0, 5, sysdate, 'UNMACTHED');