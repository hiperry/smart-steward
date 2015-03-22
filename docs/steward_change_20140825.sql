/*===================================*/
/* invoice添加三个字段*/
/*===================================*/
use steward;

alter table t_invoice add dest_code varchar(11)  comment '目的地编码';
alter table t_invoice add origin_code varchar(11)  comment '寄送地编码';

-- 因为本地修改了异常原始订单项的sku,但是抓单的时候判断sku不存在又做了插入,所以要删除
delete from t_original_order_item where id = 43724;
delete from t_original_order_item where id = 43725;