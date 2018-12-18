<template>
    <Card class="detail-card">
        <Row justify="center" align="middle" class="row-bottom">
            <Col span="3"><label>{{$t('orderDetail')}}:</label>{{order.jdeOrderNumber}}</Col>
            <Col span="3"><label>{{$t('orderStatus')}}:</label>{{$t(order.fulfillmentStatus)}}</Col>
            <Col span="18"><order-status-progress :current-order-status="order.fulfillmentStatus" :from-order-only="order.catalogService === 'ORDER_ONLY'"></order-status-progress></Col>
        </Row>
        <Row justify="center" align="middle" class="row-bottom">
            <Col span="3">
                <label>{{$t('requisitionTotal')}}:</label>
                <order-currency :currency="currencyCode" :amount="costs.total"></order-currency>
            </Col>
            <Col span="3">
                <label>{{$t('itemsPrice')}}:</label>
                <order-currency :currency="currencyCode" :amount="costs.totalItemCost"></order-currency>
            </Col>
            <Col span="3">
                <label>{{$t('orderShipping')}}:</label>
                <order-currency v-if="costs.shippingCost > 0 || costs.shippingCost === 0" :currency="currencyCode" :amount="costs.shippingCost"></order-currency>
                <span v-else>{{$t('pending')}}</span>
            </Col>
            <Col span="3" v-if="countryCode !== 'CN'">
                <label>{{$t('orderTaxes')}}:</label>
                <order-currency v-if="costs.tax > 0 || costs.tax === 0" :currency="currencyCode" :amount="costs.tax"></order-currency>
                <span v-else>{{$t('pending')}}</span>
            </Col>
            <Col span="3" ng-if="countryCode === 'CN'">
                <label>{{$t('orderTaxes')}}:</label>
                <span>{{$t('included')}}</span>
            </Col>
            <Col span="3">
                <label>{{$t('orderSubtotal')}}:</label>
                <order-currency :currency="currencyCode" :amount="costs.total"></order-currency>
            </Col>
        </Row>
        <Row justify="center" align="middle" class="address-row row-bottom">
            <Col span="24" style="margin-bottom: 10px">
                <label>{{$t('orderedBy')}}:</label>
                <span>{{order.companyName}}</span>
                <span>{{order.placedBy.firstName}} {{order.placedBy.lastName}}</span>
                <span>{{order.placedBy.phone}}</span>
                <span>{{order.placedBy.email}}</span>
                <span v-if="order.placedBy.bid">{{order.placedBy.bid}}<span>,</span>{{order.placedBy.room}}</span>
            </Col>
            <Col span="24" style="margin-bottom: 10px">
                <label>{{$t('shippingAddress')}}:</label>
                <span>{{order.shippingContactName}}</span>
                <span v-show="order.shipToAddress.nickname">{{order.shipToAddress.nickname}}</span>
                <span>{{order.shipToAddress.street1}}</span>
                <span v-show="order.shipToAddress.street2">{{order.shipToAddress.street2}}</span>
                <span v-show="order.shipToAddress.street3">{{order.shipToAddress.street3}}</span>
                <span v-show="order.shipToAddress.street4">{{order.shipToAddress.street4}}</span>
                <span>{{order.shipToAddress.city}}, {{order.shipToAddress.state}} {{order.shipToAddress.postalCode}}</span>
                <span>{{order.shipToAddress.country.isoCode2}}</span>
            </Col>
            <Col span="24" style="margin-bottom: 10px">
                <label>{{$t('billingAddress')}}:</label>
                <span>{{order.billingContactName}}</span>
                <span v-show="order.billToAddress.nickname">{{order.billToAddress.nickname}}</span>
                <span class="address1">{{order.billToAddress.street1}}</span>
                <span v-show="order.billToAddress.street2">{{order.billToAddress.street2}}</span>
                <span v-show="order.billToAddress.street3">{{order.billToAddress.street3}}</span>
                <span v-show="order.billToAddress.street4">{{order.billToAddress.street4}}</span>
                <span>{{order.billToAddress.city}}, {{order.billToAddress.state}} {{order.billToAddress.postalCode}}</span>
                <span>{{order.billToAddress.country.isoCode2}}</span>
            </Col>
        </Row>
        <order-product-summary :items="items"></order-product-summary>
        <order-additional-info :order="order"></order-additional-info>
        <order-track-info :jde-order-number="jdeOrderNumber" :order-type="orderType" v-if="!order.onlineApprovalEnable"></order-track-info>
        <order-history-track :order-history="orderHistory" v-if="viewAccessHistory"></order-history-track>
    </Card>
</template>
<script>
    import OrderStatusProgress from "../components/order-status-progress.vue";
    import OrderCurrency from "../components/order-currency.vue";
    import OrderTrackInfo from "./components/order-track-info.vue";
    import OrderProductSummary from "./components/order-product-summary.vue";
    import OrderHistoryTrack from "./components/order-history-track.vue";
    import orderAdditionalInfo from "./components/order-additional-info.vue";
    import { hasOneOf } from '@/libs/tools'

    export default {
        components: {
            OrderStatusProgress,
            OrderCurrency,
            OrderTrackInfo,
            OrderProductSummary,
            OrderHistoryTrack,
            orderAdditionalInfo
        },
        name: 'requisitionDetails',
        data: function () {
            return {
                order: {
                    placedBy: {},
                    shipToAddress: {
                        country: {}
                    },
                    billToAddress: {
                        country: {}
                    },
                },
                currencyCode: '',
                costs: {},
                items: [],
                jdeOrderNumber: '',
                orderType: '',
                countryCode: '',
                orderHistory: []
            }
        },
        mounted () {
            this.loadDetails();
        },
        methods: {
            loadDetails() {
                let vm = this;
                this.$http.get('/resources/order/getOrderDetails',  {params: {id: this.$route.params.id}}).then(res => {
                    vm.order = res.data.orderEntry;
                    vm.costs.totalItemCost = vm.order.totalItemCost;
                    vm.costs.shippingCost = vm.order.shippingCost;
                    vm.costs.tax = vm.order.tax;
                    if (vm.order.couponAmount) {
                        vm.costs.couponDiscount = vm.order.couponAmount * -1;
                    }
                    vm.currencyCode = vm.order.currencyCode;
                    vm.items = vm.order.orderItems;
                    vm.jdeOrderNumber = vm.order.jdeOrderNumber;
                    vm.orderType = vm.order.orderType;
                    vm.countryCode = vm.order.shipToAddress.country.isoCode2;
                    vm.orderHistory = vm.order.orderHistory;
                });

                if (vm.costs.total === undefined) {
                    Object.defineProperty(vm.costs, 'total', {
                        get: function () {
                            let totalCost = parseFloat(vm.costs.totalItemCost);
                            if(vm.costs.couponDiscount){
                                totalCost += parseFloat(vm.costs.couponDiscount);
                                if(totalCost < 0){
                                    totalCost = 0;
                                }
                            }

                            if (vm.costs.shippingCost) {
                                totalCost += parseFloat(vm.costs.shippingCost);
                            }
                            if (vm.costs.tax) {
                                totalCost += parseFloat(vm.costs.tax);
                            }
                            return totalCost;
                        }
                    });
                }

            }
        },
        watch: {
            '$route' (to, from) {
                if (to.name === 'requisitionDetails') {
                    this.loadDetails();
                }
            }
        },
        computed: {
            viewAccessHistory () {
                return hasOneOf(['ROLE_SITE_ADMIN', 'ROLE_CSR'], Base64.decode(localStorage.access))
            }
        }
    }
</script>
<style>
    .address-row > div > span {
        margin-left: 5%;
    }

    .payment-row > div > span {
        margin-left: 2%;
    }

    .row-bottom {
        padding-bottom: 15px;
        border-bottom: 2px silver solid;
        padding-top: 15px;
    }
</style>
