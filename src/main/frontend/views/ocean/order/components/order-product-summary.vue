<template>
    <Row justify="center" align="middle" class="row-bottom">
        <Col>
        <table>
            <thead>
            <tr>
                <th></th>
                <th class="ivu-table-cell">{{$t('product')}}</th>
                <!--<th class="ivu-table-cell">CAS</th>-->
                <!--<th class="ivu-table-cell">MDL</th>-->
                <th class="ivu-table-cell">{{$t('productId')}}</th>
                <th class="ivu-table-cell">{{$t('supplier')}}</th>
                <th class="ivu-table-cell">{{$t('size')}}</th>
                <th class="ivu-table-cell">{{$t('quantity')}}</th>
                <th class="ivu-table-cell">{{$t('unitPrice')}}</th>
                <th class="ivu-table-cell">{{$t('totalPrice')}}</th>
                <th v-if="viewAccessCost" class="ivu-table-cell">{{$t('unitCost')}}</th>
                <th v-if="viewAccessCost" class="ivu-table-cell">{{$t('totalCost')}}</th>
                <th class="ivu-table-cell">{{$t('estimatedShipDate')}}</th>
                <th class="ivu-table-cell">{{$t('status')}}</th>
            </tr>
            </thead>
            <tbody v-for="product in items">
            <tr>
                <td></td>
                <td class="ivu-table-cell">
                    <span v-if="product.labConsumableId">
                        <a href='#'>
                            <span v-for="name in localeProductName(product.multiProductName)">{{name}}</span>
                        </a>
                    </span>
                    <span v-if="product.bioReagentId">
                        <a href='#'>
                            <span v-for="name in localeProductName(product.multiProductName)">{{name}}</span>
                        </a>
                    </span>
                    <span v-if="!product.productId && !product.lifeScienceId && product.quoteResponseId">
                        <span v-for="name in localeProductName(product.multiProductName)">{{name}}</span>
                    </span>
                    <span v-if="!product.productId && !product.lifeScienceId && !product.bioReagentId && !product.labConsumableId && product.quoteResponsePackageId">
                        <span v-for="name in localeProductName(product.multiProductName)">{{name}}</span>
                    </span>
                    <span v-if="product.productId || product.lifeScienceId">
                        <a v-if="product.supplierUrlName && !product.lifeScienceId" href='#'>
                            <span v-for="name in localeProductName(product.multiProductName)">{{name}}</span>
                        </a>
                        <a v-if="!product.supplierUrlName && !product.lifeScienceId" href='#'>
                            <span v-for="name in localeProductName(product.multiProductName)">{{name}}</span>
                        </a>
                        <a v-if="product.lifeScienceId" href='#'>
                            <span v-for="name in localeProductName(product.multiProductName)">{{name}}</span>
                        </a>
                    </span>
                    <span v-if="(product.quoteSummary && product.quoteSummary.id && product.quoteSummary.referenceNumber) || product.quoteResponsePackageId">
                        <br/>
                        询单
                        <a v-if="!product.quoteResponsePackageId">{{product.quoteSummary.referenceNumber}} </a>
                        <a v-if="product.quoteResponsePackageId">{{product.quoteNumber}} </a>
                    </span>
                    <span v-if="!product.productId && !product.labConsumableId && !product.bioReagentId && !product.quoteResponseId && !product.quoteResponsePackageId">
                        <span v-for="name in localeProductName(product.multiProductName)">{{name}}</span>
                    </span>
                </td>
                <!--<td class="ivu-table-cell">-->
                    <!--<span>{{product.cas}}<br/></span>-->
                <!--</td>-->
                <!--<td class="ivu-table-cell">-->
                    <!--<span>{{product.mdlNumber}}<br/></span>-->
                <!--</td>-->
                <td class="ivu-table-cell">
                    <span v-show="product.itemNumber">{{product.itemNumber}}</span>
                </td>
                <td class="ivu-table-cell">{{product.supplierName}}</td>
                <td class="ivu-table-cell">{{packagingInfo(product)}}</td>
                <td class="ivu-table-cell">{{product.quantity}}</td>

                <td class="ivu-table-cell">
                    <order-currency :currency="product.currencyCode" :amount="product.unitPrice"
                                    :country="product.countryIsoCode2"/>
                </td>

                <td class="ivu-table-cell">
                    <order-currency :currency="product.currencyCode" :amount="product.unitPrice * product.quantity"
                                    :country="product.countryIsoCode2"/>
                </td>

                <td v-if="viewAccessCost" class="ivu-table-cell">
                    <order-currency v-if="!product.costCurrencySymbol" :currency="product.supplierCurrency.isoCode" :amount="product.unitCost"
                                    :country="product.countryIsoCode2"/>
                    <span v-if="product.costCurrencySymbol">{{product.costCurrencySymbol}}{{product.unitCost}}</span>

                </td>

                <td v-if="viewAccessCost" class="ivu-table-cell">
                    <order-currency v-if="!product.costCurrencySymbol" :currency="product.supplierCurrency.isoCode" :amount="totalCosts(product)"
                                    :country="product.countryIsoCode2"/>
                    <span v-if="product.costCurrencySymbol">{{product.costCurrencySymbol}}{{totalCosts(product)}}</span>

                </td>

                <td class="ivu-table-cell">
                    <span>{{product.shipTime}}</span>
                </td>

                <td class="ivu-table-cell">
                    <span v-if="product.status === 'Shipped'">
									{{$t('SHIPPED')}}
							</span>
                    <span v-if="product.status === 'Processing'">
                        {{$t('PROCESSING')}}
							</span>
                    <span v-if="product.status === 'Cancelled'">
                        {{$t('CANCELLED')}}
							</span>
                    <span v-if="product.status === 'Warehousing'">
                        {{$t('WAREHOUSING')}}
							</span>
                    <span v-if="product.status === 'JDE Error'">
                        {{$t('ERROR')}}
                            </span>
                    <span v-if="product.status === 'Packing'">
                        {{$t('PACKING')}}
							</span>
                    <span v-if="product.status === 'Invoiced'">
									{{$t('INVOICED')}}
							</span>
                    <span v-if="product.status === 'Mk Error'">
                        {{$t('MK_ERROR')}}
							</span>
                    <span v-if="product.status === 'Unknown Error'">
                        {{$t('ERROR')}}
							</span>
                    <span v-if="product.status === 'Out of Stock' || product.status === 'MK_1200'">
                        {{$t('outOfStock')}}
							</span>
                    <span v-if="product.status === 'Non-existent' || product.status === 'MK_1101'">
                        {{$t('nonExistent')}}
							</span>
                    <span v-if="product.status === 'MK_1102'">
                        {{$t('pcNonExistent')}}
							</span>
                    <span v-if="product.status === 'MK_1103'">
                        {{$t('codeNotConsistent')}}
							</span>
                    <span v-if="product.status === 'MK_1104'">
                        {{$t('mustPkg')}}
							</span>
                    <span v-if="product.status === 'MK_1105'">
                        {{$t('notEmail')}}
							</span>
                    <span v-if="product.status === 'MK_1106'">
                        {{$t('notLocation')}}
							</span>

                </td>

            </tr>
            <tr v-if="product.shipments.length > 0">
                <td></td>
                <td></td>
                <td colspan="13">
                    <table style="margin-top: 10px;">
                        <thead>
                        <tr>
                            <th class="ivu-table-cell">{{$t('status')}}</th>
                            <th class="ivu-table-cell">{{$t('shippedBy')}}</th>
                            <th class="ivu-table-cell">{{$t('carrier')}}</th>
                            <th class="ivu-table-cell">{{$t('estimatedDelivery')}}</th>
                            <th class="ivu-table-cell">{{$t('actualDelivery')}}</th>
                            <th class="ivu-table-cell">{{$t('trackingNumber')}}</th>
                        </tr>
                        </thead>

                        <tbody v-for="shipment in product.shipments">
                        <tr>
                            <td class="ivu-table-cell">{{shipment.status}}</td>
                            <td class="ivu-table-cell">{{shipment.shippedByCompany ? shipment.shippedByCompany.name : 'N/A'}}</td>
                            <td class="ivu-table-cell">{{shipment.carrier ? shipment.carrier : 'N/A'}}</td>
                            <td class="ivu-table-cell">{{shipment.estimatedDelivery ? formatDate(shipment.estimatedDelivery) : 'N/A'}}</td>
                            <td class="ivu-table-cell">{{shipment.actualDelivery ? formatDate(shipment.actualDelivery) : 'N/A'}}</td>
                            <td class="ivu-table-cell">{{shipment.trackingNumber ? shipment.trackingNumber : 'N/A'}}</td>
                        </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            </tbody>
        </table>
        </Col>
    </Row>
</template>
<script>
    import OrderCurrency from "../../components/order-currency.vue";
    import { hasOneOf } from '@/libs/tools';
    import moment from 'moment';

    export default {
        components: {
            OrderCurrency
        },
        name: 'order-product-summary',
        data() {
            return {}
        },
        props: {
            items: Array
        },
        methods: {
            packagingInfo(pd) {
                let size = pd.packagingSize;
                let unit = pd.packagingUnit;

                if (unit === 'µm' || unit === 'mkmol') {
                    unit = 'µmol';
                }

                if (!size) {
                    size = pd.unitSize;
                    unit = pd.unit;
                }
                unit = this.litersCase(unit);
                if (unit.toLocaleLowerCase() === 'ea') {
                    if (pd.unitDescription) {
                        return pd.unitDescription;
                    } else {
                        unit = ' ' + unit;
                    }
                }

                if (size && unit) {
                    return (size + unit);
                } else {
                    return false;
                }
            },
            litersCase(unit) {
                if (unit) {
                    if (unit.match(/(( |\d|m)+l$)|(^m*l)$/)) {
                        return unit.substr(0, unit.length - 1) + 'L';
                    }
                }
                return unit;
            },
            localeProductName(productNameList) {
                let language = navigator.language === 'zh-CN' ? 'zh_CN' : 'en_US';

                let results = [];
                if (!productNameList) {
                    return;
                }

                let mapNameList = {entry: []};
                _.each(productNameList, function (country, name) {
                    mapNameList.entry.push({key: name, value: country});
                });
                let entrys = _.filter(mapNameList.entry, function(name){
                    return name.key !== undefined;
                });
                let itemGroups = _.groupBy(entrys, function (item) {
                    return item.value.substr(0, item.value.length - 5);
                });
                for (let key in itemGroups) {
                    let itemGroup = itemGroups[key];
                    if (itemGroup.length === 1) {
                        results.push(itemGroup[0].key);
                    } else {
                        let temp = _.find(itemGroup, function (item) {
                            return item.value.slice(-5) === language;
                        });
                        if (temp !== undefined) {
                            results.push(temp.key);
                        } else {
                            let temp1 = itemGroup[0];
                            results.push(temp1.key);
                        }
                    }
                }
                return results;
            },
            totalCosts(item) {
                return (item.unitCost * item.quantity).toFixed(2);
            },
            formatDate(dt) {
                return moment(dt).format('YYYY-MM-DD LT');
            }
        },
        computed: {
            viewAccessCost () {
                return hasOneOf(['ROLE_SITE_ADMIN', 'ROLE_CSR'], Base64.decode(localStorage.access))
            }
        }
    }
</script>
<style>

</style>
