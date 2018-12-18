<template>
    <div class="discount-customer">
        <Row>
            <Col span="5" class="discount-search">
                <company-list height="400px" company-type="BUYER" placeholder="Search Buyer" @callback="checkedBuyer"></company-list>
            </Col>
            <Col span="19">
                <div class="discount-content">
                    <company-search placeholder="Search Supplier" style="flex: 1.5;" @callback="checkedSupplier" company-type="SUPPLIER"></company-search>
                    <Input class="discount-filter" v-model="filterParams.description" icon="ios-search" clearable placeholder="Discount Description"></Input>
                    <DatePicker class="discount-filter" type="date" v-model="startDate" placeholder="Start date"></DatePicker>
                    <DatePicker class="discount-filter" type="date" v-model="endDate" placeholder="End date"></DatePicker>
                    <Checkbox class="discount-filter" v-model="filterParams.onlyActive">Active Discount Only</Checkbox>
                </div>
                <div class="discount-gap-left">
                    <Table :loading="loading" stripe :columns="tableTitle" :data="discounts" @on-row-click="showDetailDiscount"></Table>
                    <Row class="discount-page">
                        <Page @on-change="onPageChanged" :page-size="25" :total="total" show-elevator></Page>
                    </Row>
                </div>
            </Col>
        </Row>
        <discount-detail :show="showDetail" @change-show="showChanged" :can-edit="imFalse" :discount-id="discountId"></discount-detail>
    </div>
</template>
<script>
    import companySearch from '../components/company-search'
    import moment from 'moment'
    import DiscountDetail from "./discount-detail";
    import CompanyList from "../components/company-list";
    export default {
        name: 'discount-customer',
        components: {
            CompanyList,
            DiscountDetail,
            companySearch },
        data() {
            return {
                imFalse: false,
                showDetail: false,
                discountId: Number,
                filterParams: {
                    onlyActive: true,
                    buyerId: undefined,
                    supplierId: undefined,
                    startDate: undefined,
                    endDate: undefined,
                    description: ''
                },
                retrievingDiscounts: false,
                discounts: [],
                total: 0,
                loading: false,
                tableTitle: [
                    {
                        key: 'companyName',
                        title: this.$t('supplierName')
                    },
                    {
                        key: 'description',
                        title: this.$t('discountTitleDesc')
                    },
                    {
                        key: 'discountRate',
                        title: this.$t('discountRate'),
                        render: function (h, params) {
                            if (params.row.discountType === 'TIER') {
                                let spans = _.map(params.row.tiers.slice().reverse(), function (val, index) {
                                    let tier = h('span', 'Tiers ' + (index + 1) + ' Value: ' + val.quantity + ', Tier ' + index + 1
                                        + ' %: ' + val.percentage);
                                    return h('span', {}, [tier, h('br')]);
                                });
                                return h('div', {}, spans);
                            } else {
                                return h('span', params.row.discount + '%');
                            }
                        }
                    },
                    {
                        key: 'effectiveDate',
                        title: this.$t('discountTitleStart'),
                        render: function (h, params) {
                            return h('div', moment(params.row.effectiveDate).format('YYYY-MM-DD LT'));
                        }
                    },
                    {
                        key: 'expirationDate',
                        title: this.$t('discountTitleEnd'),
                        render: function (h, params) {
                            return h('div', moment(params.row.expirationDate).format('YYYY-MM-DD LT'));
                        }
                    }
                ]
            }
        },
        methods: {
            checkedBuyer(val) {
                this.filterParams.buyerId = val;
            },
            checkedSupplier(val) {
                this.filterParams.supplierId = val;
            },
            loadPage(page) {
                let slf = this;
                slf.loading = true;
                slf.retrievingDiscounts = true;
                slf.filterParams.discountStatusFilter = [];
                let params = _.extend({}, slf.filterParams);
                if (!page) {
                    params.page = 1;
                } else {
                    params.page = page;
                }
                if (slf.filterParams.onlyActive) {
                    params.discountStatusFilter.push('ACTIVE');
                } else {
                    params.discountStatusFilter.push('ACTIVE');
                    params.discountStatusFilter.push('SCHEDULED');
                    params.discountStatusFilter.push('EXPIRED');
                }
                if (!params.startDate) {
                    delete params.startDate;
                }
                if (!params.endDate) {
                    delete params.endDate;
                }
                params.buyerId = !params.buyerId ? 0 : params.buyerId;
                slf.$http.get('/resources/discount/customer/list', {params: params}).then(function (res) {
                    slf.discounts = res.data.discounts;
                    slf.retrievingDiscounts = false;
                    slf.discounts.forEach(function (entry) {
                        slf.sortAndFixTiers(entry);
                    });
                    slf.loading = false;
                }).catch(function (err) {
                    slf.retrievingDiscounts = false;
                    slf.loading = false;
                });
                this.$http.get('/resources/discount/customer/list/count', {params: params}).then(function (res) {
                    slf.total = res.data;
                });
            },
            sortAndFixTiers(discount) {
                discount.tiers.sort(function (a, b) {
                    if (a.quantity > b.quantity) {
                        return 1;
                    }
                    if (a.quantity < b.quantity) {
                        return -1;
                    }
                    return 0;
                });
                discount.tiers.forEach(function (entry) {
                    entry.percentage = 100 - entry.percentage * 100;
                    entry.percentage = +entry.percentage.toFixed(1);
                });
            },
            onPageChanged(page) {
                this.loadPage(page);
            },
            showChanged(val) {
                this.showDetail = val;
            },
            showDetailDiscount(row, index) {
                this.discountId = row.id;
                this.showDetail = true;
            }
        },
        watch: {
            filterParams: {
                handler (val) {
                    if (val.buyerId) {
                        this.loadPage(1);
                    }
                },
                deep: true
            }
        },
        computed: {
            startDate: {
                get() {
                    if (!!this.filterParams.startDate) {
                        return moment(this.filterParams.startDate).toDate();
                    } else {
                        return undefined;
                    }
                },
                set(val) {
                    if (val) {
                        this.filterParams.startDate = moment(val).format('YYYY-MM-DD');
                    }
                }
            },
            endDate: {
                get() {
                    if (!!this.filterParams.endDate) {
                        return moment(this.filterParams.endDate).toDate();
                    } else {
                        return undefined;
                    }
                },
                set(val) {
                    if (val) {
                        this.filterParams.endDate = moment(val).format('YYYY-MM-DD');
                    }
                }

            }
        }
    }
</script>
<style lang="less">
    @import "./discount.less";
</style>
