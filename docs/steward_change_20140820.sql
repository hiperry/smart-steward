/*===================================*/
/* 修正TAO_BAO平台的所有订单优惠，及其订单项优惠金额*/
/*===================================*/
use steward;

alter table t_original_order add self_discount_fee bigint(20) default 0 comment '自身店铺优惠金额' after discount_fee;
alter table t_original_order_item add self_part_mjz_discount bigint(20) default 0 comment '自身店铺优惠分摊' after part_mjz_discount;

alter table t_order add self_shared_discount_fee bigint(20) default 0 comment '自身店铺优惠金额';
alter table t_order_item add self_shared_discount_fee bigint(20) default 0 comment '自身店铺优惠金额';

