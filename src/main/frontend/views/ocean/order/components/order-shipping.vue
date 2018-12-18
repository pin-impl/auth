<template>
    <Poptip placement="bottom">
        <Button type="info" @click="getExpress">{{$t('logisticsTracking')}}</Button>
        <div class="api" slot="content">
            <table>
                <thead>
                    <tr>
                        {{carrierName}}: {{serialNumber}}
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="shipInfo in express">
                        <td>
                            <div>{{shipInfo.context}}</div>
                            <div>{{shipInfo.time}}</div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </Poptip>
</template>
<script>
    export default {
        name: 'order-shipping',
        data() {
            return {
                express: []
            }
        },
        props: {
            carrierName: '',
            serialNumber: '',
            code: '',
            jdeCode: ''
        },
        methods: {
            getExpress() {
                let vm = this;
                let carrier = !vm.jdeCode ? vm.code : vm.jdeCode + '-' + vm.code;
                this.$http.get('/resources/order/' + carrier +'/' + vm.serialNumber + '/status').then(res => {
                    vm.express = res.data.response;
                });
            },
        },
        mounted () {

        }
    }
</script>
