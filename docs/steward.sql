/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-6-26 19:02:38                           */
/*==============================================================*/


drop table if exists t_brand;

drop table if exists t_brand_platform;

drop table if exists t_business_log;

drop table if exists t_conf;

drop table if exists t_contract;

drop table if exists t_gift_brand;

drop table if exists t_gift_brand_item;

drop table if exists t_gift_prod;

drop table if exists t_invoice;

drop table if exists t_invoice_printinfo;

drop table if exists t_logistics_info;

drop table if exists t_logistics_printinfo;

drop table if exists t_mealset;

drop table if exists t_mealset_item;

drop table if exists t_order;

drop table if exists t_order_approve;

drop table if exists t_order_dispose;

drop table if exists t_order_fetch;

drop table if exists t_order_handle_log;

drop table if exists t_order_item;

drop table if exists t_original_order;

drop table if exists t_original_order_item;

drop table if exists t_original_refund;

drop table if exists t_original_refund_fetch;

drop table if exists t_original_refund_item;

drop table if exists t_original_refund_tag;

drop table if exists t_payment;

drop table if exists t_payment_allocation;

drop table if exists t_platform;

drop table if exists t_prod_platform;

drop table if exists t_product;

drop table if exists t_product_category;

drop table if exists t_promotion_info;

drop table if exists t_refund;

drop table if exists t_repository;

drop table if exists t_shop;

drop table if exists t_shop_auth;

drop table if exists t_sql_log;

drop table if exists t_storage;

drop table if exists t_storage_flow;

drop table if exists t_supplier;

/*==============================================================*/
/* Table: t_brand                                               */
/*==============================================================*/
create table t_brand
(
   id                   int(11) not null auto_increment,
   name                 varchar(50) not null comment '品牌名',
   code                 varchar(255) comment '品牌代码',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11),
   supplier_id          int comment '所属供应商',
   payment_type         varchar(30) comment '结算方式',
   payment_rule         varchar(100) comment '结算规则，时间要求等',
   deleted              tinyint default 0,
   primary key (id)
);

/*==============================================================*/
/* Table: t_brand_platform                                      */
/*==============================================================*/
create table t_brand_platform
(
   id                   int not null auto_increment,
   brand_id             int comment '品牌ID',
   platform_id          int comment '平台ID',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   operator_id          int comment '操作人',
   primary key (id)
);

alter table t_brand_platform comment '品牌允许上样的平台';

/*==============================================================*/
/* Table: t_business_log                                        */
/*==============================================================*/
create table t_business_log
(
   id                   int(11) not null auto_increment,
   operator_name        varchar(64) default null comment '操作用户名称',
   operation_name       varchar(32) default null comment '操作名称',
   params               text comment '请求传入的参数',
   request_url          varchar(512) default null comment '请求的url',
   create_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11) default null comment '操作用户ID',
   primary key (id)
);

alter table t_business_log comment '操作日志';

/*==============================================================*/
/* Table: t_conf                                                */
/*==============================================================*/
create table t_conf
(
   id                   int(11) not null auto_increment,
   name                 varchar(64) not null comment 'key',
   value                varchar(128) not null comment 'value',
   description          varchar(512) default null comment '说明',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11),
   primary key (id),
   unique key config_key (name)
);

/*==============================================================*/
/* Table: t_contract                                            */
/*==============================================================*/
create table t_contract
(
   id                   int not null auto_increment,
   code                 varchar(20) comment '合同编号，系统生成',
   deposit              bigint comment '保证金',
   service_fee          bigint comment '服务费',
   invoice_ejs_title    varchar(30) comment '给客户开发票时，易居尚平台的抬头',
   invoice_other_title  varchar(30) comment '给客户开发票时，非易居尚平台的抬头',
   invoice_to_ejs       varchar(30) comment '第三方平台销售是否补开发票给易居尚',
   other_rule           text comment '其它条款',
   remark               text comment '补充协议',
   overdue_fine         varchar(50) comment '滞纳金情况 ',
   begin_time           datetime default '0000-00-00 00:00:00' comment '合同开始时间',
   end_time             datetime default '0000-00-00 00:00:00' comment '合同结束时间',
   real_end_time        datetime default '0000-00-00 00:00:00' comment '实际中止时间',
   end_reason           varchar(200) comment '终止原因',
   create_time          datetime default '0000-00-00 00:00:00' comment '合同录入时间',
   supplier_id          int,
   ejs_comp_name        varchar(50) comment '签合同的时候，我方公司名',
   operator_id          int(11),
   update_time          datetime,
   payment_type         varchar(30) comment '结算方式',
   payment_rule         varchar(100) comment '结算规则，时间要求等',
   shot_fee_type        varchar(50) comment '拍摄费用情况',
   shipping_fee_type    varchar(50) comment '物流费用情况',
   box_fee_type         varchar(50) comment '物流包装费用',
   third_platform_fee_type varchar(50) comment '第三方平台费用情况',
   to_ejs_fee_type      varchar(50) comment '到易居尚仓库的费用情况',
   commission           varchar(50) comment '物流包装费用',
   primary key (id)
);

alter table t_contract comment '供应商合同信息';

/*==============================================================*/
/* Table: t_gift_brand                                          */
/*==============================================================*/
create table t_gift_brand
(
   id                   int(11) not null auto_increment,
   brand_id             int(11) default null comment '品牌id',
   price_begin          bigint(20) default null comment '起始价格',
   price_end            bigint(20) default null comment '结束价格',
   in_use               tinyint(1) default 1 comment '是否启用(0是表示未启用, 1表示已启用)默认是已启用1',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11),
   primary key (id)
);

/*==============================================================*/
/* Table: t_gift_brand_item                                     */
/*==============================================================*/
create table t_gift_brand_item
(
   id                   int(11) not null auto_increment,
   gift_brand_id        int(11) not null comment '优惠活动-品牌id',
   gift_prod_id         int(11) not null comment '赠送商品id',
   gift_prod_count      int(11) comment '赠送数量',
   primary key (id)
);

/*==============================================================*/
/* Table: t_gift_prod                                           */
/*==============================================================*/
create table t_gift_prod
(
   id                   int(11) not null auto_increment,
   sell_prod_id         int(11) default null comment '购买的商品id',
   gift_prod_id         int(11) not null comment '赠送的商品id',
   gift_prod_count      int(11) default null comment '赠送数量',
   in_use               tinyint(1) default 1 comment '是否启用(0是表示未启用, 1表示已启用)默认是已启用1',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11),
   primary key (id)
);

/*==============================================================*/
/* Table: t_invoice                                             */
/*==============================================================*/
create table t_invoice
(
   id                   int(11) not null auto_increment,
   receiver_name        varchar(128) comment '收货人姓名',
   receiver_phone       varchar(128) default null comment '收货人手机号(与收货人电话在业务上必须存在一个)',
   receiver_mobile      varchar(128) default null comment '收货人电话(与收货人手机号在业务上必须存在一个)',
   receiver_zip         varchar(128) default null comment '收货人邮编',
   receiver_state       varchar(64) comment '收货人省份',
   receiver_city        varchar(64) comment '收货人城市',
   receiver_district    varchar(64) default null comment '收货人地区',
   receiver_address     varchar(256) comment '不包含省市区的详细地址',
   shipping_no          varchar(128) default null comment '物流编号',
   shipping_comp        varchar(64) default null comment '物流公司',
   primary key (id)
);

/*==============================================================*/
/* Table: t_invoice_printinfo                                   */
/*==============================================================*/
create table t_invoice_printinfo
(
   id                   int(10) unsigned not null auto_increment,
   print_html           text comment '发票信息打印的lodop代码',
   logistics_picture_path varchar(500) default null comment '发票图片路径',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   primary key (id)
);

/*==============================================================*/
/* Table: t_logistics_info                                      */
/*==============================================================*/
create table t_logistics_info
(
   id                   int(11) not null auto_increment,
   order_no             varchar(64) not null comment '订单号(一个订单在业务上只允许有一个快递单号)',
   express_no           varchar(64) not null comment '物流单号(每一笔物流单号的数据都会向 kuaidi100 发送请求. 请求一次就需要付费! 所以, 请务必保证只在线上可以请求及只发送一次)',
   express_company      varchar(255) not null comment '物流公司名(shunfeng, yunda, ems, tiantian等)',
   send_to              varchar(32) default null comment '收货地址(到市一级即可. 如广东省深圳市, 北京市)',
   express_info         text comment '物流信息(由快递 100 提供)',
   express_status       tinyint(1) default 0 comment '物流状态(1表示配送完成, 0表示未完成).',
   first_time           datetime default '0000-00-00 00:00:00' comment '第一条物流状态时间',
   latest_time          datetime default '0000-00-00 00:00:00' comment '物流状态最新的时间',
   was_request          tinyint(1) default 0 comment '是否已请求第三方物流(1已请求, 0未请求), 默认是 0',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   primary key (id),
   unique key express_no (express_no)
);

/*==============================================================*/
/* Table: t_logistics_printinfo                                 */
/*==============================================================*/
create table t_logistics_printinfo
(
   id                   int(10) unsigned not null auto_increment,
   name                 varchar(150) not null comment '物流名称',
   law                  int(11) not null comment '物流单号递增规律',
   print_html           text comment '物流信息打印的lodop代码',
   logistics_picture_path varchar(500) default null comment '物流图片路径',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   repository_id        int(11) comment '所属仓库',
   page_height          int(11) comment '打印页面高度',
   page_width           int(11) comment '打印页面宽度',
   primary key (id)
);

/*==============================================================*/
/* Table: t_mealset                                             */
/*==============================================================*/
create table t_mealset
(
   id                   int(11) not null auto_increment,
   name                 varchar(20) not null comment '套餐名',
   sku                  varchar(20) not null comment '套餐条形码',
   sell_description     varchar(512) default null comment '卖点描述',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   deleted              tinyint(1) default 0,
   operator_id          int(11),
   primary key (id)
);

/*==============================================================*/
/* Table: t_mealset_item                                        */
/*==============================================================*/
create table t_mealset_item
(
   id                   int(11) not null auto_increment,
   mealset_id           int(20) not null comment '套餐id',
   product_id           int(11) not null comment '商品id',
   price                bigint(20) not null comment '套餐价',
   amount               int(20) not null comment '套餐中的数量',
   primary key (id)
);

/*==============================================================*/
/* Table: t_order                                               */
/*==============================================================*/
create table t_order
(
   id                   int(11) not null auto_increment,
   order_no             varchar(20) not null comment '订单编号',
   type                 varchar(15) not null comment '订单类型',
   status               varchar(20) not null comment '订单状态',
   return_status        varchar(20) not null comment '退货状态',
   generate_type        varchar(30) comment '生成类型',
   refunding            tinyint(1) comment '是否正在申请退款',
   shared_discount_fee  bigint(20) comment '系统优惠金额',
   shared_post_fee      bigint(20) comment '邮费',
   actual_fee           bigint(20) comment '实付金额',
   goods_fee            bigint(20) comment '货款',
   buyer_id             varchar(128) comment '买家Id(淘宝号)',
   buyer_alipay_no      varchar(128) comment '买家支付宝账号（淘宝）',
   buyer_message        text comment '买家留言',
   remark               text comment '客服备注',
   repo_id              int(11) comment '库房id',
   buy_time             datetime default '0000-00-00 00:00:00' comment '下单时间',
   pay_time             datetime default '0000-00-00 00:00:00' comment '支付时间',
   shop_id              int(11) comment '店铺id',
   need_receipt         tinyint(1) default null comment '是否需要发票',
   receipt_title        varchar(100) default null comment '发票抬头',
   receipt_content      varchar(512) default null comment '发票内容',
   platform_type        varchar(10) not null comment '外部平台类型(天猫还是京东)',
   platform_order_no    varchar(100) comment '外部订单号',
   original_order_id    int(11) default null comment '原始订单ID',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   update_time          datetime default '0000-00-00 00:00:00' comment '更新时间',
   operator_id          int(11),
   invoice_id           int(11) comment '发货单信息ID',
   valid                tinyint(1) comment '是否有效',
   primary key (id)
);

/*==============================================================*/
/* Table: t_order_approve                                       */
/*==============================================================*/
create table t_order_approve
(
   id                   int(11) not null auto_increment,
   order_status         varchar(20) not null comment '订单状态',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   update_time          datetime default '0000-00-00 00:00:00' comment '更新时间',
   operator_id          int(11),
   order_id             int(11) not null,
   primary key (id)
);

/*==============================================================*/
/* Table: t_order_dispose                                       */
/*==============================================================*/
create table t_order_dispose
(
   id                   int(11) not null auto_increment,
   source_ids           varchar(127) not null comment '原始订单ID,多个用逗号分割',
   target_ids           varchar(127) not null comment '新生成的订单ID,多个用逗号分割',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   operator_id          int(11),
   type                 varchar(20) not null comment '拆分或合并',
   manual               tinyint(1),
   primary key (id)
);

/*==============================================================*/
/* Table: t_order_fetch                                         */
/*==============================================================*/
create table t_order_fetch
(
   id                   int(11) not null auto_increment,
   fetch_time           datetime not null comment '抓取时间',
   platform_type        varchar(20) not null comment '抓取平台',
   shop_id              bigint(11) not null comment '店铺id',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   fetch_data_type      varchar(30) comment '抓取数据类型：订单\退款单\退货单 等',
   primary key (id)
);

/*==============================================================*/
/* Table: t_order_handle_log                                    */
/*==============================================================*/
create table t_order_handle_log
(
   id                   int(11) not null auto_increment,
   from_status          varchar(20) not null comment '原始状态',
   to_status            varchar(20) not null comment '目标状态',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   operator_id          int(11),
   order_id             int(11) not null,
   primary key (id)
);

/*==============================================================*/
/* Table: t_order_item                                          */
/*==============================================================*/
create table t_order_item
(
   id                   int(11) not null auto_increment,
   platform_sub_order_no varchar(32) comment '外部平台子订单编号',
   original_order_item_id int(11) comment '原始订单项编号',
   platform_type        varchar(20) not null comment '外部平台类型(天猫还是京东)',
   platform_order_no    varchar(100) comment '外部订单号',
   product_id           int(11) not null comment '商品id',
   product_code         varchar(32) comment '商品编码',
   product_sku          varchar(32) not null comment '商品条形码',
   outer_sku            varchar(50) comment '外部平台商品条形码（京东）',
   product_name         varchar(32) comment '商品名称',
   price_description    varchar(256) default null comment '订单价格描述',
   order_id             int(11) not null comment '订单ID',
   source_item_id       int(11) comment '如果类型是套餐,则为套餐项ID',
   status               varchar(20) comment '订单项状态',
   type                 varchar(20) not null comment '订单项类型(商品, 套餐, 赠品...)',
   return_status        varchar(20) comment '线上退货状态',
   offline_return_status varchar(20) comment '线下退货状态',
   exchanged            tinyint(1) comment '是否被售前换货',
   refunding            tinyint(1) comment '是否正在申请退款',
   price                bigint(20) comment '商品单价',
   discount_price       bigint(20) comment '促销价',
   buy_count            int(11) comment '购买数量',
   discount_fee         bigint(20) comment '子订单级订单优惠金额',
   shared_discount_fee  bigint(20) comment '分摊优惠金额',
   shared_post_fee      bigint(20) comment '分摊邮费',
   actual_fee           bigint(20) comment '实付总金额',
   goods_fee            bigint(20) comment '货款',
   refund_fee           bigint(20) comment '线上账面退款金额',
   actual_refund_fee    bigint(20) comment '线上实际退款金额',
   offline_refund_fee   bigint(20) comment '线下退款金额',
   service_cover_fee    bigint(20) comment '服务补差金额',
   service_cover_refund_fee bigint(20) comment '服务补差退款金额',
   post_cover_fee       bigint(20) comment '邮费补差金额',
   post_cover_refund_fee bigint(20) comment '邮费补差退款金额',
   return_post_fee      bigint(20) comment '线上退货邮费',
   offline_return_post_fee bigint(20) comment '线下退货邮费',
   exchange_post_fee    bigint(20) comment '换货邮费',
   return_post_payer    varchar(20) comment '线上退货邮费承担方',
   offline_return_post_payer varchar(20) comment '线下退货邮费承担方',
   exchange_post_payer  varchar(20) comment '换货邮费承担方',
   exchange_source_id   bigint(20) comment '被换货的OrderItemId',
   valid                tinyint(1) comment '是否有效',
   primary key (id)
);

/*==============================================================*/
/* Table: t_original_order                                      */
/*==============================================================*/
create table t_original_order
(
   id                   int(11) not null auto_increment,
   status               varchar(50) not null comment '订单状态',
   total_fee            bigint(20) comment '订单商品总金额（SUM(一口价*数量)）',
   actual_fee           bigint(20) comment '订单实付金额（卖家应收）',
   discount_fee         bigint(20) comment '系统优惠金额（如打折，VIP，满就送等）',
   has_post_fee         tinyint(1) comment '是否包含邮费。与available_confirm_fee同时使用',
   post_fee             bigint(20) comment '邮费',
   available_confirm_fee bigint(20) comment '交易中剩余的确认收货金额（这个金额会随着子订单确认收货而不断减少，交易成功后会变为零）',
   received_payment     bigint(20) comment '卖家实际收到的支付宝打款金额（由于子订单可以部分确认收货，这个金额会随着子订单的确认收货而不断增加，交易成功后等于买家实付款减去退款金额）',
   adjust_fee           bigint(20) comment '手工调整金额（淘宝）',
   point_fee            bigint(20) comment '买家使用积分,下单时生成，且一直不变',
   real_point_fee       bigint(20) comment '买家实际使用积分（扣除部分退款使用的积分），交易完成后生成（交易成功或关闭），交易未完成时该字段值为0',
   seller_discount_fee  bigint(20) comment '京东商家优惠金额，包含整单以及订单项优惠',
   seller_fee           bigint(20) comment '订单货款金额（订单总金额-商家优惠金额）（京东）',
   balance_used         bigint(20) comment '余额支付金额（京东）',
   payable_fee          bigint(20) comment '订单应付金额（京东，减去礼品卡/余额支付/京豆优惠后，买家应付现金',
   delivery_type        varchar(10) comment '送货（日期）类型（1-只工作日送货(双休日、假日不用送);2-只双休日、假日送货(工作日不用送);3-工作日、双休日与假日均可送货;其他值-返回"任意时间"）',
   buyer_message        text comment '买家留言',
   remark               text comment '客服备注',
   buyer_id             varchar(128) comment '买家Id(淘宝号)',
   buyer_alipay_no      varchar(128) comment ' 买家支付宝账号（淘宝）',
   receiver_name        varchar(128) comment '收货人姓名',
   receiver_phone       varchar(128) default null comment '收货人手机号(与收货人电话在业务上必须存在一个)',
   receiver_mobile      varchar(128) default null comment '收货人电话(与收货人手机号在业务上必须存在一个)',
   receiver_zip         varchar(128) default null comment '收货人邮编',
   receiver_state       varchar(64) comment '收货人省份',
   receiver_city        varchar(64) comment '收货人城市',
   receiver_district    varchar(64) default null comment '收货人地区',
   receiver_address     varchar(256) comment '不包含省市区的详细地址',
   buy_time             datetime default '0000-00-00 00:00:00' comment '下单时间',
   pay_time             datetime default '0000-00-00 00:00:00' comment '支付时间',
   end_time             datetime default '0000-00-00 00:00:00' comment '结单时间',
   modified_time        datetime default '0000-00-00 00:00:00' comment '订单更新时间',
   platform_type        varchar(10) comment '外部平台类型(天猫还是京东)',
   platform_order_no    varchar(100) comment '外部订单号',
   shop_id              bigint(11) comment '店铺id',
   out_shop_id          varchar(20) comment '淘宝店铺id/京东商家id',
   need_receipt         tinyint(1) default null comment '是否需要发票',
   receipt_title        varchar(100) default null comment '发票抬头',
   receipt_content      varchar(512) default null comment '发票内容',
   create_time          datetime default '0000-00-00 00:00:00',
   processed            tinyint(1) default null comment '是否已经被订单处理程序处理过',
   primary key (id)
);

/*==============================================================*/
/* Table: t_original_order_item                                 */
/*==============================================================*/
create table t_original_order_item
(
   id                   int(11) not null auto_increment,
   sku                  varchar(20) default null comment '商品条形码',
   outer_sku            varchar(50) comment '外部平台商品条形码(京东）',
   price                bigint(20) comment '商品单价',
   buy_count            bigint(11) comment '购买数量',
   total_fee            bigint(20) comment '商品总金额',
   payable_fee          bigint(20) comment '应付总金额',
   actual_fee           bigint(20) comment '实付总金额',
   platform_sub_order_no varchar(32) comment '原始订单子编号',
   original_order_id    int(11) not null comment '原始订单ID',
   discount_fee         bigint(20) comment '子订单级订单优惠金额',
   adjust_fee           bigint(20) comment '手工调整金额',
   divide_order_fee     bigint(20) comment '分摊之后的实付金额',
   part_mjz_discount    bigint(20) comment '优惠分摊',
   primary key (id)
);

/*==============================================================*/
/* Table: t_original_refund                                     */
/*==============================================================*/
create table t_original_refund
(
   id                   int(11) not null auto_increment comment '主键',
   refund_id            varchar(30) comment '退款单编号（外部平台）',
   refund_type          varchar(20) comment 'refund:仅退款 return:退款退货',
   trade_status         varchar(50) comment '淘宝交易状态',
   refund_fee           bigint(20) comment '申请退款金额，单位：分',
   reason               text comment '申请退款原因',
   actual_refund_fee    bigint(20) comment '实际退款金额/京东退款金额',
   created              datetime comment '退款创建时间/京东申请时间',
   current_phase_timeout datetime comment '当前状态超时时间',
   alipay_no            varchar(30) comment '支付宝交易号',
   buyer_nick           varchar(50) comment '买家昵称/京东客户姓名',
   seller_nick          varchar(50) comment '卖家昵称',
   tid                  varchar(30) comment '淘宝交易号/京东订单号',
   oid                  varchar(30) comment '淘宝子订单号',
   cs_status            varchar(10) comment '淘宝小二是否介入',
   status               varchar(50) comment '退款单状态/京东审核状态',
   refund_phase         varchar(20) comment 'onsale:售中 aftersale：售后',
   modified             datetime comment '最后一次修改时间
            最后一次修改时间
            最后一次修改时间',
   bill_type            varchar(20) comment '单据类型，退款单/退货单',
   refund_version       varchar(30) comment '退款协议版本',
   operation_constraint varchar(30) comment 'cannot_refuse: 不允许操作 refund_onweb: 需要到网页版操作',
   company_name         varchar(100) comment '物流公司（淘宝退货）',
   sid                  varchar(30) comment '物流运单号（淘宝退货）',
   operation_log        text comment '退货单操作日志（淘宝退货）',
   description          text comment '退款说明（淘宝退货）',
   buyer_id             varchar(30) comment '客户账号（京东）',
   check_time           datetime comment '审核日期 （京东）',
   check_username       varchar(50) comment '审核人（京东）',
   platform_type        varchar(20) comment '平台类型',
   shop_id              int(11) comment '店铺id',
   processed            tinyint(1) default null comment '是否已经被处理过',
   primary key (id)
);

/*==============================================================*/
/* Table: t_original_refund_fetch                               */
/*==============================================================*/
create table t_original_refund_fetch
(
   id                   int(11) not null auto_increment comment '主键',
   fetch_time           datetime comment '抓取日期',
   platform_type        varchar(30) comment '抓取平台',
   shop_id              int(11) comment '店铺id',
   create_time          datetime comment '记录创建时间',
   primary key (id)
);

alter table t_original_refund_fetch comment '原始退款单抓取日志表';

/*==============================================================*/
/* Table: t_original_refund_item                                */
/*==============================================================*/
create table t_original_refund_item
(
   id                   int(11) not null auto_increment comment '主键',
   original_refund_id   int(11) comment '外键，依赖于t_original_refund的主键id',
   num_iid              varchar(20) comment '商品id',
   price                bigint(20) comment '商品价格',
   num                  bigint(20) comment '商品数量',
   outer_id             varchar(50) comment '商品外部商家编码',
   sku                  varchar(200) comment 'sku信息，如30004447689|颜色分类:军绿色;尺码:XS',
   primary key (id)
);

/*==============================================================*/
/* Table: t_original_refund_tag                                 */
/*==============================================================*/
create table t_original_refund_tag
(
   id                   int(11) not null auto_increment comment '主键',
   original_refund_id   int(11) comment '外键，依赖于t_original_refund的主键id',
   tag_key              varchar(50) comment '标签key，如service_7d',
   tag_name             varchar(50) comment '标签名，如服务-7天无理由',
   tag_type             varchar(20) comment '签标类型，如service',
   primary key (id)
);

alter table t_original_refund_tag comment '天猫退款单的标签，可以知道当前退款是什么类型，如财务-运费险、服务-七天无理由、退款-批量可退款、退款-极速退款';

/*==============================================================*/
/* Table: t_payment                                             */
/*==============================================================*/
create table t_payment
(
   id                   int(11) not null auto_increment,
   platform_sub_order_no varchar(32) comment '外部平台子订单编号',
   original_order_item_id int(11) comment '原始订单项编号',
   platform_type        varchar(20) not null comment '外部平台类型(天猫还是京东)',
   platform_order_no    varchar(100) not null comment '外部订单号',
   original_order_id    int(11) default null comment '原始订单ID',
   buy_time             datetime default '0000-00-00 00:00:00' comment '下单时间',
   pay_time             datetime default '0000-00-00 00:00:00' comment '支付时间',
   buyer_id             varchar(128) comment '买家Id(淘宝号)',
   buyer_message        text comment '买家留言',
   remark               text comment '客服备注',
   allocate_status      varchar(20) not null comment '分配状态(未分配,已分配,售前退款)',
   type                 varchar(20) not null comment '预收款类型(邮费补差,服务补差,订单邮费)',
   payment_fee          bigint(20) comment '预收金额',
   refund_fee           bigint(20) comment '预收退款金额',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   update_time          datetime default '0000-00-00 00:00:00' comment '更新时间',
   operator_id          int(11),
   shop_id              bigint(11) comment '店铺id',
   primary key (id)
);

/*==============================================================*/
/* Table: t_payment_allocation                                  */
/*==============================================================*/
create table t_payment_allocation
(
   id                   int(11) not null auto_increment,
   order_item_id        int(11) default null comment '订单项ID',
   payment_id           int(11) default null comment '预收款ID',
   payment_fee          bigint(20) default 0 comment '预收分配金额',
   refund_fee           bigint(20) default 0 comment '预收退款分配金额',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   update_time          datetime default '0000-00-00 00:00:00' comment '更新时间',
   operator_id          int(11),
   primary key (id)
);

/*==============================================================*/
/* Table: t_platform                                            */
/*==============================================================*/
create table t_platform
(
   id                   int not null auto_increment,
   name                 varchar(50) comment '平台名',
   storage_percent      int,
   prod_link_prefix     varchar(300),
   type                 varchar(30),
   primary key (id)
);

alter table t_platform comment '目前在运营平台(上样平台）';

/*==============================================================*/
/* Table: t_prod_platform                                       */
/*==============================================================*/
create table t_prod_platform
(
   id                   int not null auto_increment,
   platform_id          int,
   prod_id              int,
   price                bigint comment '一口价，掉牌价',
   discount_price       bigint comment '促销价',
   storage_percent      int comment '库存占比，如果设置将覆写掉t_platform的占比值',
   storage_num          int,
   is_putaway           tinyint,
   syn_status           tinyint comment '同步状态',
   platform_url         varchar(100)binary,
   primary key (id)
);

/*==============================================================*/
/* Table: t_product                                             */
/*==============================================================*/
create table t_product
(
   id                   int(11) not null auto_increment,
   brand_id             int(11) comment '品牌id',
   name                 varchar(128) not null comment '商品名',
   product_no           varchar(128) comment '商品编码',
   sku                  varchar(128) not null comment '商品条形码',
   category_id          int(11) comment '商品分类id',
   description          text comment '产品描述',
   pic_url              varchar(512) comment '图片地址',
   market_price         bigint(20) comment '市场价',
   minimum_price        bigint(20) comment '最低价',
   color                varchar(32) comment '颜色',
   weight               varchar(32) comment '重量',
   box_size             varchar(32) comment '包装尺寸',
   speci                text comment '规格',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   deleted              tinyint(1) default 0,
   operator_id          int(11),
   orgin                varchar(32) comment '产地',
   style                varchar(16) comment '产品类型爆款等',
   location             varchar(16) comment '库位赠品正常等',
   outer_product_no     varchar(32) comment '外部平台产品编码（天猫同步信息用）',
   primary key (id)
);

/*==============================================================*/
/* Table: t_product_category                                    */
/*==============================================================*/
create table t_product_category
(
   id                   int(11) not null auto_increment,
   name                 varchar(50) not null comment '分类名',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11),
   parent_category_id   int(11) comment '父分类',
   primary key (id)
);

/*==============================================================*/
/* Table: t_promotion_info                                      */
/*==============================================================*/
create table t_promotion_info
(
   id                   int(11) not null auto_increment comment '主键，自增',
   original_order_id    int(11) not null comment '原始订单主键',
   promotion_name       varchar(100) comment '淘宝优惠信息名称',
   discount_fee         bigint(20) comment '淘宝/京东优惠金额',
   gift_item_name       varchar(100) comment '淘宝满就送商品时的商品名称',
   gift_item_id         varchar(20) comment '淘宝赠品的宝贝id',
   gift_item_num        int(10) comment '淘宝赠品的数量',
   promotion_desc       text comment '淘宝优惠活动的描述',
   promotion_id         varchar(100) comment '淘宝优惠id，结构为：营销工具id-优惠活动id_优惠详情id',
   coupon_type          varchar(50) comment '京东优惠类型',
   sku_id               varchar(32) comment '京东sku编号，整单优惠活动时为null',
   out_platform_type    varchar(20) comment '来自哪个第三方平台',
   primary key (id)
);

alter table t_promotion_info comment '订单优惠详情表';

/*==============================================================*/
/* Table: t_refund                                              */
/*==============================================================*/
create table t_refund
(
   id                   int(11) not null auto_increment,
   platform_refund_no   varchar(128) comment '退款单号',
   platform_type        varchar(20) comment '平台类型',
   original_refund_id   bigint(20) comment '原始退款ID',
   status               varchar(20) comment '退款状态(正在申请,成功,失败)',
   phase                varchar(20) comment '售前还是售后退款',
   type                 varchar(20) comment '退款类型(订单退款,预收款退款)',
   order_item_id        int(11) comment '订单项ID',
   payment_id           int(11) comment '预收款ID',
   online               tinyint(1) comment '是否是线上退款',
   refund_fee           bigint(20) comment '账面退款金额',
   actual_refund_fee    bigint(20) comment '实际退款金额',
   refund_time          datetime default '0000-00-00 00:00:00' comment '退款时间',
   buyer_id             varchar(128) comment '买家Id',
   buyer_name           varchar(128) comment '买家昵称',
   reason               text comment '退款原因',
   remark               text comment '备注',
   description          text comment '退款说明',
   also_return          tinyint(1) comment '是否同时退货',
   post_fee             bigint(20) comment '运费',
   post_payer           varchar(20) comment '运费承担方',
   create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
   update_time          datetime default '0000-00-00 00:00:00' comment '更新时间',
   operator_id          int(11),
   shop_id              bigint(11) not null comment '店铺id',
   revisit_time         datetime default '0000-00-00 00:00:00' comment '回访时间',
   shipping_no          varchar(128) default null comment '物流编号',
   shipping_comp        varchar(64) default null comment '物流公司',
   buyer_alipay_no      varchar(128) comment '买家支付宝账号（淘宝）',
   primary key (id)
);

/*==============================================================*/
/* Table: t_repository                                          */
/*==============================================================*/
create table t_repository
(
   id                   int(11) not null auto_increment,
   name                 varchar(50) not null comment '仓库名',
   code                 varchar(16) comment '仓库编码',
   address              varchar(200) comment '仓库地址',
   charge_person_id     int(11) default null comment '责任人id',
   shipping_comp        varchar(20) default null comment '物流公司',
   charge_mobile        varchar(20) default null comment '责任人手机号',
   charge_phone         varchar(20) default null comment '负责人电话',
   create_time          datetime default '0000-00-00 00:00:00',
   update_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11),
   province_id          varchar(10) not null,
   city_id              varchar(10),
   area_id              varchar(10),
   column_15            char(10),
   primary key (id)
);

/*==============================================================*/
/* Table: t_shop                                                */
/*==============================================================*/
create table t_shop
(
   id                   int(11) not null auto_increment comment '主键',
   shop_auth_id         int(11) comment 't_shop_auth外键',
   out_shop_id          varchar(20) comment '外部平台店铺id',
   cat_id               varchar(20) default null comment '店铺所属类目id',
   uid                  varchar(50) comment '卖家id',
   nick                 varchar(100) not null comment '卖家昵称',
   title                varchar(100) default null comment '店铺标题',
   description          text comment '店铺描述',
   bulletin             text comment '店铺公告',
   pic_path             varchar(200) default null comment '店标地址',
   item_score           varchar(10) default null comment '商品描述评分',
   service_score        varchar(10) default null comment '服务态度评分',
   delivery_score       varchar(10) default null comment '发货速度评分',
   de_express           varchar(50) default null comment '默认快递',
   enable_msg           tinyint(1) default null comment '是否启用发货短信，0：禁用；1：启用',
   msg_temp             text comment '发货短信模板',
   msg_sign             varchar(200) default null comment '短信签名',
   platform_type        varchar(10) default null comment '店铺来自哪个平台（如天猫，京东）',
   shop_type            varchar(20) comment '账号是店铺还是供应商',
   create_time          datetime default null comment '开店时间',
   update_time          datetime default null comment '最后修改时间',
   operator_id          int(11),
   is_delete            tinyint default 0 comment '是否删除（0：未删除，1：删除）',
   primary key (id)
);

/*==============================================================*/
/* Table: t_shop_auth                                           */
/*==============================================================*/
create table t_shop_auth
(
   id                   int(11) not null auto_increment comment '表主键',
   session_key          varchar(100) not null comment '店铺对应的session key(即Access Token)',
   token_type           varchar(20) default null comment 'Access token的类型目前只支持bearer',
   expires_in           varchar(50) default null comment 'Access token过期时间',
   refresh_token        varchar(100) not null comment 'Refresh token',
   re_expires_in        varchar(50) default null comment 'Refresh token过期时间',
   r1_expires_in        varchar(50) default null comment 'r1级别API或字段的访问过期时间',
   r2_expires_in        varchar(50) default null comment 'r2级别API或字段的访问过期时间',
   w1_expires_in        varchar(50) default null comment 'w1级别API或字段的访问过期时间',
   w2_expires_in        varchar(50) default null comment 'w2级别API或字段的访问过期时间',
   user_nick            varchar(100) default null comment '外部平台账号昵称',
   user_id              varchar(100) not null comment '外部平台帐号对应id',
   platform_type        varchar(10) default null comment '当前授权用户来自哪个平台（如天猫，京东）',
   create_time          datetime default null comment 'Session key第一次授权时间',
   update_time          datetime default null comment 'Session key最后修改时间',
   is_delete            tinyint default 0 comment '删除标识（0：未删除，1：删除）',
   primary key (id)
);

/*==============================================================*/
/* Table: t_sql_log                                             */
/*==============================================================*/
create table t_sql_log
(
   id                   int(11) not null auto_increment,
   business_log_id      int(11) default null comment '业务日志ID',
   content              text,
   operation_type       varchar(20) default null comment '操作类型',
   execution_time       int(11) default null comment '请求耗时',
   primary key (id)
);

/*==============================================================*/
/* Table: t_storage                                             */
/*==============================================================*/
create table t_storage
(
   id                   int(11) not null auto_increment,
   product_id           int(11) not null comment '商品id',
   repository_id        int(11) not null comment '仓库id',
   amount               int(11) not null comment '实际库存',
   primary key (id)
);

/*==============================================================*/
/* Table: t_storage_flow                                        */
/*==============================================================*/
create table t_storage_flow
(
   id                   int(11) not null auto_increment,
   storage_id           int(11),
   type                 varchar(20) comment '出库/入库',
   amount               int(11),
   create_time          datetime default '0000-00-00 00:00:00',
   operator_id          int(11),
   order_id             int(11),
   in_out_stock_type    varchar(50) comment '出入库类型(原因)',
   description          varchar(255),
   before_amount        int(11) comment '操作前数量',
   primary key (id)
);

/*==============================================================*/
/* Table: t_supplier                                            */
/*==============================================================*/
create table t_supplier
(
   id                   int not null auto_increment,
   code                 varchar(10),
   name                 varchar(100),
   deleted              tinyint default 0 comment '是否冻结',
   primary key (id)
);

alter table t_supplier comment '供应商';

