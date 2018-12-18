<template>
    <Row v-if="trackInfos.length > 0" class="row-bottom">
        <div>
            <header>
                <h3>{{$t('trackingInformation')}}</h3>
            </header>
            <div>
                <table>
                    <thead>
                    <tr>
                        <th style="text-align: left">{{$t('productInformation')}}</th>
                        <th style="text-align: left">{{$t('supplier')}}</th>
                        <th style="width:10%;text-align: left">{{$t('supplierNotes')}}</th>
                        <th style="text-align: left">{{$t('shippingDetails')}}</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="info in trackInfos">
                        <td style="vertical-align: top;">
                            <span>{{info.productName}}</span>
                            <span v-for="prodDesc in info.productInfo">{{prodDesc}}<br/></span>
                        </td>
                        <td style="vertical-align: top;">{{info.supplierName}}</td>
                        <td style="vertical-align: top;">
                            <div v-for="trackTime in info.orderComments">
                                {{formatDate(trackTime.createTime)}}<br v-show="trackTime.createTime"/>
                                {{trackTime.comment}}
                            </div>
                            <div v-show="info.remainingTrackInfos && info.remainingTrackInfos.length > 0" style="float: right;">
                                <a v-show="!info.showMoreTackInfo" @click="showExtraTrackInfo(info)" href="javascript:void(0)" ><span><Icon type="chevron-down"></Icon>{{$t('more')}}</span></a>
                                <a v-show="info.showMoreTackInfo" @click="hideExtraTrackInfo(info)" href="javascript:void(0)" ><span><Icon type="chevron-up"></Icon>{{$t('less')}}</span></a>
                            </div>
                        </td>
                        <td style="vertical-align: top;">
                            <div v-for="shipInfo in info.expressInfos">
                                <div><div></div>{{formatDate(shipInfo.receiveDate)}}<order-shipping :carrier-name="getCarrierName(shipInfo)" :serial-number="shipInfo.serialNumber" :jde-code="shipInfo.jdeSerialNumber" :code="shipInfo.englishName"></order-shipping></div>
                                <div>{{$t('carrier')}}: {{getCarrierName(shipInfo)}}</div>
                                <div>{{$t('trackingNumber')}}: {{shipInfo.serialNumber}}</div>
                                <div v-show="shipInfo.note">{{$t('remark')}}: {{shipInfo.note}}</div>
                            </div>
                            <div v-show="info.remainingExpressInfos && info.remainingExpressInfos.length > 0" style="text-align: center;">
                                <a v-show="!info.showMoreExpressInfo" @click="showExtraExpressInfo(info)" href="javascript:void(0)" ><span><Icon type="chevron-down"></Icon>{{$t('more')}}</span></a>
                                <a v-show="info.showMoreExpressInfo" @click="hideExtraExpressInfo(info)" href="javascript:void(0)" ><Icon type="chevron-up"></Icon><span>{{$t('less')}}</span></a>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </Row>
</template>
<script>

    import OrderShipping from "./order-shipping.vue";
    import moment from 'moment'

    export default {
        components: {
            OrderShipping
        },
        name: 'order-track-info',
        data() {
            return {
                trackInfos: []
            }
        },
        props: {
            jdeOrderNumber: '',
            orderType: ''
        },
        methods: {
            getCarrierName(trackItem) {
                if (!trackItem) {
                    return '';
                }
                return navigator.language === 'zh-CN' ? trackItem.chineseName : trackItem.englishName;
            },
            getProductInfo(trackInfos) {
                let vm = this;
                if(!trackInfos){
                    return;
                }
                _.each(trackInfos, function(trackInfo){
                    trackInfo.productInfo = [];
                    if(trackInfo.cas){
                        trackInfo.productInfo.push('CAS:' + trackInfo.cas);
                    }
                    if(trackInfo.mdl){
                        trackInfo.productInfo.push('MDL:' + trackInfo.mdl);
                    }
                    if(trackInfo.specification){
                        trackInfo.productInfo.push(trackInfo.specification);
                    }
                    if(trackInfo.supplierProductId){
                        trackInfo.productInfo.push(trackInfo.supplierProductId);
                    }
                    if(trackInfo.brand){
                        trackInfo.productInfo.push(trackInfo.brand);
                    }
                    vm.hideExtraTrackInfo(trackInfo);
                    vm.hideExtraExpressInfo(trackInfo);
                });
            },
            getTrackInfo() {
                let vm = this;
                this.$http.get('/resources/order/track-info/' + vm.jdeOrderNumber +'/' + vm.orderType).then(res => {
                    vm.trackInfos = res.data;
                    vm.getProductInfo(res.data);
                });
            },
            showExtraTrackInfo(trackInfo) {
                _.each(trackInfo.remainingTrackInfos, function (trackTime) {
                    trackInfo.orderComments.push(trackTime);
                });
                trackInfo.showMoreTackInfo = true;
            },

            hideExtraTrackInfo(trackInfo) {
                const showTrackLimit = 3;

                if(!trackInfo.orderComments || !trackInfo.orderComments.length){
                    return;
                }
                trackInfo.remainingTrackInfos = trackInfo.orderComments.splice(showTrackLimit, trackInfo.orderComments.length - showTrackLimit);
                trackInfo.showMoreTackInfo = false;
            },

            showExtraExpressInfo(trackInfo) {
                _.each(trackInfo.remainingExpressInfos, function (info) {
                    trackInfo.expressInfos.push(info);
                });
                trackInfo.showMoreExpressInfo = true;
            },

            hideExtraExpressInfo(trackInfo) {
                const showExpressLimit = 2;

                if(!trackInfo.expressInfos || !trackInfo.expressInfos.length){
                    return;
                }
                trackInfo.remainingExpressInfos = trackInfo.expressInfos.splice(showExpressLimit, trackInfo.expressInfos.length - showExpressLimit);
                trackInfo.showMoreExpressInfo = false;
            },
            formatDate(dt) {
                return moment(dt).format('YYYY-MM-DD LT');
            }
        },
        watch: {
            jdeOrderNumber: function (newVal) {
                this.getTrackInfo();
            }
        }
    }
</script>
