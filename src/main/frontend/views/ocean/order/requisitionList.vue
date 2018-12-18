<template>
    <Row>
        <Row class="search-filter">
            <label>{{$t('orderNo')}}:</label><Input v-model="params.filters.jdeOrderNumber" clearable icon="search" placeholder="e.g 123456789" style="width: 200px" />
            <label>CAS:</label><Input v-model="params.filters.cas" clearable icon="search" placeholder="CAS#" style="width: 200px" />
            <label>MDL:</label><Input v-model="params.filters.mdl" clearable icon="search" placeholder="MDL#" style="width: 200px" />
            <label>{{$t('orderDate')}}:</label>
            <DatePicker class="discount-filter" type="date" v-model="params.filters.createdDateFrom" placeholder="Start date"></DatePicker>
            <DatePicker class="discount-filter" type="date" v-model="params.filters.createdDateTo" placeholder="End date"></DatePicker>
        </Row>
        <Row class="search-filter">
            <div class="search-row">
                <div class="search-field"><label class="search-user-width">{{$t('purchasedBy')}}:</label><div class="search-width"><user-search-input :status="'APPROVED'" :default-user="userData" @return-data="returnData"></user-search-input></div></div>
                <div class="search-field"><label class="search-label-width">{{$t('buyerCompany')}}:</label><div class="search-width"><order-company-search placeholder="Buyer company" :default-company="buyerCompany" class="discount-filter" @callback="checkedBuyer" order-type="REQUISITION"></order-company-search></div></div>
                <div class="search-field"><label class="search-label-projectno">{{$t('projectCode')}}:</label><Input v-model="params.filters.projectCode" clearable icon="search" style="width: 200px" /></div>
                <div class="search-field"><label class="search-label-jde">JDE:</label><Input v-model="params.filters.jdeCode" clearable icon="search" style="width: 200px" /></div>
                <div class="search-button"><Button class="requisition-search" type="info" shape="circle" @click="search">{{$t('searchButton')}}</Button></div>
                <div class="search-button"><Button class="filter-clear" type="info" shape="circle" @click="cleanFilter">{{$t('reset')}}</Button></div>
                <!--<div class="export-button"><Button class="export-requisition" type="warning" shape="circle" @click="exportRequisitions">{{$t('exportOrder')}}</Button></div>-->
            </div>

        </Row>
        <Card>
            <Tabs type="card" @on-click="tabChange">
                <TabPane name="ALL" :label="$t('allOrders')">
                    <Table v-if="params.status === 'ALL'" stripe :columns="tableTitle" :data="orders" @on-row-click="showOrderDetail" @on-sort-change='sortColumn'></Table>
                </TabPane>
                <TabPane name="PROCESSING" :label="$t('processingOrders')">
                    <Table v-if="params.status === 'PROCESSING'" stripe :columns="tableTitle" :data="orders" @on-row-click="showOrderDetail" @on-sort-change='sortColumn'></Table>
                </TabPane>
                <TabPane name="OTHERS" :label="$t('othersOrders')">
                    <Table v-if="params.status === 'OTHERS'" stripe :columns="tableTitle" :data="orders" @on-row-click="showOrderDetail" @on-sort-change='sortColumn'></Table>
                </TabPane>
            </Tabs>

        </Card>
        <Page class="page" :page-size="pageSize" :current.sync="currentPage" @on-change="onPageChanged" :total="total" show-elevator show-sizer show-total></Page>
    </Row>
</template>
<script>
    import OrderCompanySearch from '../components/order-company-search.vue';
    import moment from "moment";
    import UserSearchInput from "../components/user-search-input.vue";
    import {mapState} from 'vuex'

    export default {
        components: {
            UserSearchInput,
            OrderCompanySearch},
        name: 'requisitionList',
        data: function () {
            let vm = this;
            return {
                isReRender: true,
                pageSize: 10,
                total: 0,
                currentPage: 1,
                userData: {
                    email: '',
                    index: 0
                },
                buyerCompany: {
                    name: '',
                    index: 0,
                    companyId: 0
                },
                orders: [],
                params: {
                    status: "ALL",
                    sortColumn: 'createdAt',
                    sortOrder: 'DESC',
                    offset: 0,
                    limit: 10,
                    filters: {
                        jdeOrderNumber: undefined,
                        cas: undefined,
                        mdl: undefined,
                        createdDateFrom: undefined,
                        createdDateTo: undefined,
                        placedBy: undefined,
                        buyerCompanyIds: [],
                        projectCode: undefined,
                        jdeCode: undefined
                    },
                    orderType: "REQUISITION",
                    companyId: undefined,
                    excludeFilterInfo: false
                },
                openUserModel: false,
                invalid: true,

                tableTitle: [
                    {
                        title: this.$t('orderNo'),
                        key: 'jdeOrderNumber',
                        render: function (h, params) {
                            return h('a', {
                                attrs: {
                                    href: '#/requisitionDetails/' + this.row.orderId,
                                }
                            }, this.row.jdeOrderNumber)
                        }
                    },
                    {
                        title: this.$t('orderDate'),
                        key: 'createdAt',
                        sortable: 'custom',
                        render: function (h, params) {
                            return h('div',
                                moment(this.row.orderDate).format('YYYY-MM-DD'));
                        }
                    },
                    {
                        title: this.$t('buyerCompany'),
                        key: 'buyerCompany',
                        sortable: 'custom',
                        render: function (h, params) {
                            return h('span', this.row.placedBy.company);
                        }
                    },
                    {
                        title: this.$t('productName'),
                        key: 'productName',
                        render: function (createElement) {
                            if (this.row.productName) {
                                return createElement('div', vm.productNames(this.row.productName).map(function (name) {
                                    return createElement('div', name)
                                }))
                            }
                        }
                    },
                    {
                        title: 'CAS',
                        key: 'cas',
                        render: function (createElement) {
                            if (this.row.orderItems.length) {
                                let cases = _.uniq(_.map(this.row.orderItems, 'cas'));
                                return createElement('div', cases.map(function (cas) {
                                    return createElement('div', cas)
                                }))
                            }
                        }
                    },
                    {
                        title: 'MDL',
                        key: 'mdl',
                        render: function (createElement) {
                            if (this.row.orderItems.length) {
                                let mdls = _.uniq(_.map(this.row.orderItems, 'mdlNumber'));
                                return createElement('div', mdls.map(function (mdl) {
                                    return createElement('div', mdl)
                                }))
                            }
                        }
                    },
                    {
                        title: this.$t('packageQuantity'),
                        key: 'packageQuantity',
                        render: function (createElement) {
                            if (this.row.orderItems.length) {
                                return createElement('div', this.row.orderItems.map(function (orderItem) {
                                    return createElement('div', orderItem.unitSize + orderItem.unit + "/" + orderItem.quantity)
                                }))
                            }
                        }
                    },
                    {
                        title: this.$t('orderStatus'),
                        key: 'fulfillmentStatus',
                        sortable: 'custom',
                        render: function (h, params) {
                            return h('div', vm.translateOrderStatus(this.row.fulfillmentStatus))
                        }
                    }
                ]
            }
        },
        mounted () {
            this.init()
        },
        methods: {
            init(page) {
                this.loadPage(1);
            },
            onPageChanged(page) {
                //this.currentPage = page;
                this.init(page);
            },
            showOrderDetail(row, index) {
                this.orderId = row.id;
                this.showDetail = true;
            },
            tabChange(name) {
                //this.clear();
                this.params.status = name;
                this.loadPage(1);
            },
            changeLocale (locale) {
                this.$store.commit('setLocale', locale)
            },
            showChanged(val) {
                this.showDetail = val;
                if (!val) {
                    this.init(1);
                }
            },
            checkedBuyer(val) {
                let companyId = [];
                companyId.push(val);
                this.params.filters.buyerCompanyIds = companyId;
            },
            returnData(data) {
                console.log(data);
                this.params.filters.placedBy = data;
            },
            sortColumn(column) {
                console.log("key = ", column.key)
                console.log("order = ", column.order)
                this.params.sortColumn = column.key;
                this.params.sortOrder = column.order;
                this.loadPage(1);
            },
            cleanFilter() {
                this.userData = {
                    email: '',
                    index: 0,
                    id: 0
                }
                this.buyerCompany = {
                    name: '',
                    index: 0,
                    companyId: 0
                }
                this.params = {
                    status: this.params.status,
                    sortColumn: 'createdAt',
                    sortOrder: 'DESC',
                    offset: 0,
                    limit: 10,
                    filters: {
                        jdeOrderNumber: undefined,
                        cas: undefined,
                        mdl: undefined,
                        createdDateFrom: undefined,
                        createdDateTo: undefined,
                        placedBy: undefined,
                        buyerCompanyIds: [],
                        projectCode: undefined,
                        jdeCode: undefined
                    },
                    orderType: "REQUISITION",
                    companyId: undefined,
                    excludeFilterInfo: false
                };
            },
            showChanged(val) {
                this.openUserModel = val;
            },
            search() {
                this.loadPage(1);
            },
            onPageChanged(page) {
                this.loadPage(page);
            },
            loadPage(page) {
                let slf = this;
                this.currentPage = page;
                this.params.offset = (page - 1) * this.pageSize;
                this.params.limit = this.pageSize;
                this.$http.post('/resources/order/get-list',  this.params).then(res => {
                    slf.orders = res.data.orderEntryInfo;
                    slf.total = res.data.total;
                    //slf.currentPage = 1;
                    if (slf.orders && slf.orders.length) {
                        let companyIds = _.uniq(_.map(slf.orders, 'placedBy.companyId'));
                        this.getCompanyLocalNames(companyIds).then(localNames => {
                            _.each(slf.orders, function (order) {
                                let localName = _.find(localNames, function (item) {
                                    return order.placedBy.companyId === item.id
                                })
                                if (localName) order.placedBy.company = localName.localName;
                            })
                        })
                    }
                });
            },
            exportRequisitions() {
                let slf = this;
                this.params.offset = (page - 1) * this.pageSize;
                this.params.limit = this.pageSize;
                this.$http.post('/resources/order/export/list',  this.params).then(res => {
                    console.log("Export csv success.");
                });
            },
            productNames(productNameList) {
                let localeForEach;

                let localeMatch = function (item) {
                    //console.log("item.value.slice(-5): ", item.value.slice(-5));

                    let value = item.value.slice(-5).replace("_", "");
                    let locale = localeForEach.replace("-", "");
                    //console.log(value , locale);
                    return value === locale;
                };
                //console.log("this.$store.state.app.lang :" + this.$store.state.app.lang)
                localeForEach = this.$store.state.app.lang;
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
                        let temp = _.find(itemGroup, localeMatch);
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
            changeUserSearch(val) {
                this.openUserModel = val;
            },
            getCompanyLocalNames(companyIds) {
                let request = {
                    locale: this.$store.state.app.lang.replace("-", "_"),
                    companyIds: companyIds
                }
                let localNames;
                return new Promise((resolve, reject) => {
                    this.$http.post('/resources/companies/local-names', request).then(res => {
                        resolve(res.data);
                    }, error => {
                        reject(error);
                    })

                })
            },
            translateOrderStatus(status) {
                return this.$t(status)
            }
        },
        computed: {
            ...mapState({
                lang: state => state.app.lang
            })
        }
    }
</script>
<style lang="less" scoped>
    .page {
        float: right;
        margin-top: 20px;
    }
    .search-filter {
        margin: 0 20px 10px 0;
    }
    .order-type {
        width: 200px;
    }
    .scene-col {
        padding: 0;
    }
    .scene-table{
        padding-left: 0 !important;
    }
    .requisition-search .filter-clear {
        margin-top: 10px;
        text-align: center;
    }
    .export-requisition {
        float: right;
        width: 120px;
        height: 30px;
    }
    .search-label-width {
        width: 80px;
        margin-top: 7px;
        padding-left: 2px;
    }
    .search-user-width {
        width: 75px;
        margin-top: 7px;
        padding-left: 2px;
    }
    .search-label-projectno {
        width: 50px;
        margin-top: 7px;
        padding-left: 2px;
    }
    .search-label-jde {
        width: 30px;
        margin-top: 7px;
        padding-left: 2px;
    }
    .search-width {
        width: 200px;
    }
    .search-row {
        display: inline-flex;
    }
    .search-field {
        display: inherit;
        flex: 3;
        height: 60px;
    }
    .search-button {
        padding-left: 4px;
        flex: 1;
        height: 60px;
    }
    .export-button {
        padding-left: 2px;
        flex: 4;
        height: 60px;
    }
</style>
