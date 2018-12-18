<template>
    <span v-if="!symbolOnly">{{symbol}}{{amount}}</span><span v-else>{{symbol}}</span>
</template>
<script>
    export default {
        name: 'order-currency',
        data() {
            return {

            }
        },
        props: {
            amount: '',
            currency: '',
            symbolOnly: ''
        },
        computed: {
            symbol() {
                return this.$t('currency' + this.currency);
            }
        }
    }
</script>
