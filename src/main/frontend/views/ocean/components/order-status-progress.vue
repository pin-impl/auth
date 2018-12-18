<template>
    <div class="panel component-status" v-show="allOrderStatus.indexOf(currentOrderStatus) > -1">
        <Steps :current="current" size="small" v-if="!fromOrderOnly">
            <Step :title="$t('PENDING_APPROVAL')"></Step>
            <Step :title="$t('PROCESSING')"></Step>
            <Step :title="$t('WAREHOUSING')"></Step>
            <Step :title="$t('PACKING')"></Step>
            <Step :title="$t('SHIPPED')"></Step>
            <Step :title="currentOrderStatus === 'CANCELLED' ? $t('CANCELLED') : $t('DELIVERED')"></Step>
            <Step :title="$t('INVOICED')"></Step>
        </Steps>

        <Steps :current="current" size="small" v-if="fromOrderOnly">
            <Step :title="$t('PENDING_APPROVAL')"></Step>
            <Step :title="$t('FORWARDED')"></Step>
        </Steps>
    </div>
</template>
<script>
    export default {
        name: 'order-status-progress',
        data() {
            return {

            }
        },
        props: {
            currentOrderStatus: '',
            fromOrderOnly: ''
        },
        methods: {

        },
        mounted() {

        },
        computed: {
            current() {
                let vm = this;
                if (vm.currentOrderStatus === 'PENDING_APPROVAL') {
                    return 0;
                } else if (vm.currentOrderStatus === 'PROCESSING') {
                    return 1;
                } else if (vm.currentOrderStatus === 'WAREHOUSING') {
                    return 2;
                } else if (vm.currentOrderStatus === 'PACKING') {
                    return 3;
                } else if (vm.currentOrderStatus === 'SHIPPED' || vm.currentOrderStatus === 'PARTIALLY_SHIPPED') {
                    return 4;
                } else if (vm.currentOrderStatus === 'DELIVERED' || vm.currentOrderStatus === 'CANCELLED') {
                    return 5;
                } else if (vm.currentOrderStatus === 'invoiced') {
                    return 6;
                } else if (vm.currentOrderStatus === 'PENDING_APPROVAL') {
                    return 0;
                } else if (vm.currentOrderStatus === 'FORWARDED') {
                    return 1;
                }
            },
            allOrderStatus() {
                let vm = this;
                const LN_ORDER_ORDER_STATUS = [
                    'PENDING_APPROVAL',
                    'PROCESSING',
                    'WAREHOUSING',
                    'PACKING',
                    'PARTIALLY_SHIPPED',
                    'SHIPPED',
                    'DELIVERED',
                    'CANCELLED',
                    'INVOICED'
                ];
                const ORDER_ONLY_STATUS = [
                    'PENDING_APPROVAL',
                    'FORWARDED'
                ];
                if(vm.fromOrderOnly === true){
                    return ORDER_ONLY_STATUS;
                }else{
                    return LN_ORDER_ORDER_STATUS;
                }
            }
        }
    }
</script>
<style>

</style>
