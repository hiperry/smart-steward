/**
 * Created by king on 13-12-23
 */

Ext.define('Supplier.view.AddOrderChangeWin.AddOrderChangeList', {
    extend: 'Ext.grid.Panel',
    region: 'center',
    alias: 'widget.AddOrderChangeList',
    id: 'AddOrderChangeList',
    height: 150,
    forceFit: true,
    split:true,
    store: 'GoodList',
    //selType: 'checkboxmodel',
    viewConfig: Espide.Common.getEmptyText(),
    initComponent: function () {
        this.tbar = [
            Ext.create('Ext.form.Panel', {
                layout: 'hbox',
                border: false,
                itemId: 'goodSearch',
                items: [
                    {
                        xtype:'textfield',
                        hidden: true,
                        id: 'hideOrderId'
                    },
                    {
                        xtype: 'combo',
                        hideLabel: true,
                        name: 'searchType',
                        width: 90,
                        queryMode: 'local',
                        triggerAction: 'all',
                        forceSelection: true,
                        editable: false,
                        value: 'productNo',
                        store: [
                            ['name', '商品名称'],
                            ['productNo', '商品编号']
                        ],
                        margin: '0 5 0 0'
                    },
                    {
                        xtype: 'textfield',
                        emptyText: '请输入关键字',
                        itemId: 'keyword',
                        allowBlank: false,
                        name: 'searchValue',
                        width: 120,
                        margin: '0 5 0 0'
                    },
                    {
                        xtype: 'button',
                        text: '搜索',
                        margin: '0 5 0 0',
                        itemId: 'searchBtn'
                    }
                ]
            }),
            '->',
            {xtype: 'numberfield', value: 1,hidden:true, minValue: 1, width: 110, name: 'addGiftNum', itemId: 'addNum', fieldLabel: '增加数量', labelWidth: 60},
            {xtype: 'button', text: '增加',hidden:true, iconCls: 'icon-add', disabled: false, itemId: 'addBtn'}
        ];
        this.columns = [
            {text: '商品编号', dataIndex: 'productNo'},
            {text: '商品名称', dataIndex: 'name'},
            {text: 'sku', dataIndex: 'sku'},
            {text: '品牌', dataIndex: 'brandName',},
            {text: '类别', dataIndex: 'prodCategoryName',},
            {
                xtype: 'actioncolumn',
                text: '操作',
                itemId: 'addRow',
                menuDisabled: true,
                width: 50,
                iconCls: 'icon-add'
            }
        ];
        this.callParent(arguments);
    }
});
