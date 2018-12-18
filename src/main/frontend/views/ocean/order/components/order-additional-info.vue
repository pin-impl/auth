<template>
    <Row v-if="order.onlineApprovalEnable" class="row-bottom payment-row">
        <Col>
        <h4 style="display: inline;margin-right: 15px">{{$t('paymentDetails')}}</h4>
        <label>{{$t('paymentType')}}:</label>
        <span v-if="order.paymentInfo">
                    {{$t('checkout.payment.card' + order.paymentInfo.cardType)}} - {{order.paymentInfo.cardMask}}
                </span>
        <span v-if="!order.paymentInfo">
                    -
                </span>
        <label>{{$t('collectedBy')}}:</label>
        <span v-show="!order.chinaToChinaOrder">{{order.supplierName}}</span>
        <span v-show="order.chinaToChinaOrder">{{$t('chinaCollector')}}</span>
        </Col>
        <Col>
        <h4 style="display: inline;margin-right: 15px">{{$t('shippingDetails')}}</h4>
        <label>{{$t('shippingOption')}}:</label>
        <span>{{$t('shippingStandard')}}</span>
        <span v-show="order.shippingAccountNumber || order.carrier">
                    <span>
                        <label>{{$t('shippingAccount')}}:</label>
                        <span>{{order.carrierName}}<span v-show="order.shippingAccountNumber">[ {{order.shippingAccountNumber}} ]</span></span>
                    </span>
                </span>
        </Col>
        <Col v-if="showInvoice">
        <h4 style="display: inline;margin-right: 15px">{{$t('additionalDetails')}}</h4>
        <label>{{$t('invoiceType')}}:</label>
        <span v-if="order.invoiceType==='ORDINARY'">{{$t('ordinaryInvoice')}}</span>
        <span v-if="order.invoiceType==='SPECIAL'">{{$t('specialInvoice')}}</span>
        </Col>
    </Row>
</template>
<script>
    import moment from 'moment'

    export default {
        components: {

        },
        name: 'order-additional-info',
        data() {
            return {
                showInvoice: false
            }
        },
        props: {
            order: {}
        },
        methods: {
            checkInvoice() {
                let vm = this;
                this.$http.get('/resources/admin/user/byEmail',  {params: {email: localStorage.userEmail}}).then(res => {
                    let currentUser = res.data;
                    this.$http.get('/resources/company/current').then(res => {
                        let currentCompany = res.data;
                        vm.showInvoice = 'CN' === currentUser.countryIsoCode2 && ('ORDER' === currentCompany.orderType || currentCompany.onlineApprovalEnable);
                    });
                });
            }
        },
        mounted () {
            this.checkInvoice();
        }
    }
</script>
