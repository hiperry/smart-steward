/*
 * Created by king on 13-12-20
 */

Ext.define('Supplier.view.SFWin', {
    extend: 'Ext.window.Window',
    title: '顺丰联想单号',
    collapsible: true,
    closeAction: 'destroy',
    alias: 'widget.SFWin',
    itemId: 'SFWin',
    id: 'SFWin',
    autoShow: false,
    layout: 'fit',
    modal: true,
    width: 400,
    height: 240,
    initComponent: function () {
        this.items = Ext.create('Ext.form.Panel', {
            itemId: 'SFWinForm',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            bodyPadding: 30,
            border: 0,
            defaults: {
                xtype: 'combo',
                labelWidth: 60,
                width: 250,
                queryMode: 'local',
                triggerAction: 'all',
                forceSelection: true,
                editable: false,
                margin: '0 0 15 0'
            },
            items: [
                {
                    fieldLabel: '快递公司',
                    emptyText: '请选择',
                    name: 'shippingComp',
                    id: 'shippingComp',
                    value:'shunfeng',
                    readOnly: true,
                    allowBlank: false,
                    store: Espide.Common.expressStore()
                },
                {
                    name: 'expressType',
                    labelWidth: 60,
                    width: 250,
                    fieldLabel: '日期类型',
                    itemId: 'expressType',
                    id: 'expressType',
                    value: 'BIAO_ZHUN_KUAI_DI',
                    store: [
                        ['BIAO_ZHUN_KUAI_DI', '标准快递'],
                        ['SHUN_FENG_TE_HUI', '顺丰特惠'],
                        ['DIAN_SHANG_TE_HUI', '电商特惠'],
                        ['DIAN_SHANG_SU_PEI', '电商速配'],
                    ],
                },
//                {
//                    xtype: 'checkbox',
//                    fieldLabel: '是否允许覆盖',
//                    name: 'isCover',
//                    labelWidth: 90,
//                    width: 250
//                }

            ],
            buttons: {
                layout: {
                    pack: 'center'
                },
                items: [
                    { text: '确定', itemId: 'SFWcomfirm' },
                    { text: '取消', itemId: 'cancel' }
                ]
            }
        });
        this.listeners = {
//            show: function (){
//                var shippingComp = Espide.Common.getGridSels("OrderList", 'shippingComp')[0];
//                Ext.getCmp("autoNumberWin").down("#shippingComp").setValue(shippingComp);
//            }
        };
        this.callParent(arguments);
    }
});