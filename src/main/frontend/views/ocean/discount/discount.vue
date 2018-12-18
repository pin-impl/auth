<template>
    <div>
        <Card>
            <Tabs type="card" @on-click="tabChange">
                <TabPane name="discount" :label="$t('discountFangan')">
                    <Row class="discount-filter">
                        <Input v-model="params.description" clearable icon="search" placeholder="Discount Description" style="width: 200px" />
                        <Select v-model="params.status" clearable style="width:200px" :placeholder="$t('discountStatusTitle')">
                            <Option value="ACTIVE">{{ $t('discountStatusActive') }}</Option>
                            <Option value="SCHEDULED">{{ $t('discountStatusScheduled') }}</Option>
                            <Option value="EXPIRED">{{ $t('discountStatusExpired') }}</Option>
                        </Select>
                        <Button class="discount-add-btn" type="warning" @click="addDiscount">Create Discount</Button>
                    </Row>
                    <Row>
                        <Table :loading="loading" stripe :columns="discountTableTitles" :data="discountList" @on-row-click="showDetailDiscount"></Table>
                    </Row>
                    <Row class="discount-page">
                        <Page @on-change="onPageChanged" :page-size="pageSize" :total="discountTotal" show-elevator></Page>
                    </Row>
                </TabPane>
                <TabPane name="customer" :label="$t('discountViewByCustomer')">
                    <discount-customer v-if="showCustomer"></discount-customer>
                </TabPane>
                <TabPane name="supplier" :label="$t('discountViewBySupplier')">
                    <discount-supplier v-if="showSupplier"></discount-supplier>
                </TabPane>
            </Tabs>
        </Card>
    <discount-detail :show="showDetail" @change-show="showChanged" :can-edit="imTrue" :discount-id="discountId"></discount-detail>
    </div>
</template>
<script>
    import moment from 'moment'
    import discountDetail from './discount-detail.vue'
    import discountCustomer from './discount-customer'
    import DiscountSupplier from "./discount-supplier";
    export default {
        name: 'discount',
        data() {
            return {
                imTrue: true,
                imFalse: false,
                statusTitle: this.$t('discountStatusTitle'),
                fanganActive: false,
                showCustomer: false,
                showSupplier: false,
                params: {
                    description: '',
                    status: '',
                    sortColumn: 'createdAt',
                    sortDir: 'DESC'
                },
                loading: false,
                showDetail: false,
                discountTableTitles: [
                    {
                        key: 'id',
                        title: this.$t('discountTitleId'),
                        sortType: 'desc',
                        className: 'discount-click'
                    },
                    {
                        key: 'description',
                        title: this.$t('discountTitleDesc')
                    },
                    {
                        key: 'status',
                        title: this.$t('discountStatusTitle')
                    },
                    {
                        key: 'discountType',
                        title: this.$t('discountTitleType')
                    },
                    {
                        key: 'effectiveDate',
                        title: this.$t('discountTitleStart'),
                        render: function (h, params) {
                            return h('div',
                                moment(this.row.effectiveDate).format('YYYY-MM-DD'));
                        }
                    },
                    {
                        key: 'expirationDate',
                        title: this.$t('discountTitleEnd'),
                        render: function (h, params) {
                            return h('div',
                                moment(this.row.expirationDate).format('YYYY-MM-DD'));/*这里的this.row能够获取当前行的数据*/
                        }
                    }
                ],
                discountList: [],
                discountTotal: 0,
                pageSize: 25,
                discountId: Number
            }
        },
        methods: {
            init(page) {
                this.loading = true;
                let statusFilters = [];
                if(!this.params.status || this.params.status === '') {
                    statusFilters.push('ACTIVE');
                    statusFilters.push('SCHEDULED');
                    statusFilters.push('EXPIRED');
                } else {
                    statusFilters.push(this.params.status);
                }
                this.params.page = page;
                this.params.discountStatusFilter = statusFilters;
                this.$http.get('/resources/discount/list', {params: this.params}).then(res => {
                    this.discountList = res.data.discountSummaries;
                    this.discountTotal = res.data.total;
                    this.loading = false;
                });
            },
            onPageChanged(page) {
                this.init(page);
            },
            showDetailDiscount(row, index) {
                this.discountId = row.id;
                this.showDetail = true;
            },
            addDiscount() {
                this.showDetail = true;
                this.discountId = undefined;
            },
            tabChange(name) {
                if (name === 'customer') {
                    this.showCustomer = true;
                    this.showSupplier = false;
                } else if (name === 'supplier') {
                    this.showSupplier = true;
                    this.showCustomer = false;
                }
            },
            showChanged(val) {
                this.showDetail = val;
                if (!val) {
                    this.init(1);
                }
            }
        },
        mounted() {
            this.init(1);
        },
        watch: {
            params: {
                handler(val) {
                    this.init(1);
                },
                deep: true
            }
        },
        components: {
            DiscountSupplier,
            discountDetail, discountCustomer }
    }
</script>
<style scoped>
    .discount-click {
        color: #0C25F1;
        cursor: pointer;
    }
    .discount-filter {
        margin: 0 20px 10px 0;
    }
    .discount-page {
        float: right;
        margin-top: 20px;
    }
    .discount-add-btn {
        float: right;
        width: 120px;
        height: 30px;
    }
</style>
