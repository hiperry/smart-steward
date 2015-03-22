Ext.define('Supplier.store.ItemList', {
    //不要忘了继承
    extend: 'Ext.data.Store',
    //记得设置model
    //model: 'Supplier.model.ItemList',
    fields:[
        'product',
        'productCode',
        'productName',
        'productSku',
        'id',
        'type',
        'cateName',
        'price',
        'goodsFee',
        'buyCount',
        'repoNum',
        'specInfo',
        'brandName',
        'actualFee',
        'userActualPrice',
        'sharedPostFee',
        {name:'sharedDiscountFee',type:'float'},
        {name:'selfSharedDiscountFee',type:'float'},
    ],
    proxy: {
        type: 'ajax',
        url: 'invoice/list_invoice_order_item',
        reader: {
            type: 'json',
            successProperty: 'success',
            root: 'data.list',
            messageProperty: 'message'
        },
        listeners: {
            exception: function(proxy, response, operation){
                var data = Ext.decode(response.responseText);
                Ext.MessageBox.show({
                    title: '警告',
                    msg: data.msg,
                    icon: Ext.MessageBox.ERROR,
                    buttons: Ext.Msg.OK
                });
            }
        }
    },
    autoLoad: false
});