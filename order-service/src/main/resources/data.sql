insert into public.orders (total_price, order_status) VALUES
(150.25,'PENDING'),
(349.99,'CONFIRMED'),
(89.50,'SHIPPED'),
(499.00,'DELIVERED'),
(1200.75,'CANCELLED'),
(75.99,'PENDING'),
(640.40,'CONFIRMED'),
(980.10,'SHIPPED'),
(210.00,'DELIVERED'),
(59.99,'PENDING');


INSERT INTO public.order_item (order_id,product_id,quantity) VALUES
(1,1,2),
(1,2,1),
(2,3,1),
(3,1,2),
(3,5,1),
(4,2,1),
(4,7,3),
(5,10,1),
(5,4,2),
(6,8,1),
(7,3,2),
(7,6,1),
(8,9,4),
(8,12,1),
(9,15,2),
(9,11,1),
(10,13,1),
(10,14,2);