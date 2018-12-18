<template>
    <div>
        <Row class="filter-quote">
            <Col span="4">
                <Input v-model="filterQuoteNo" placeholder="Quote NO." clearable></Input>
            </Col>
            <Col span="4" offset="1">
                <Select v-model="filterStatus" placeholder="Quote Status" clearable>
                    <Option v-for="s in filters" :key="s.value" :label="s.label" :value="s.value"></Option>
                </Select>
            </Col>
            <Col span="1" offset="1">
                <Button type="primary" shape="circle" @click="searchQuote">Search</Button>
            </Col>
            <Col span="1" offset="1">
                <Button type="ghost" shape="circle" @click="resetSearch">Reset</Button>
            </Col>
        </Row>
        <Table :data="quotes" :columns="titles" @on-row-click="getHistory"></Table>
        <Row>
            <Col offset="18">
                <Page :page-size="10" :total="total" @on-change="changePage"></Page>
            </Col>
        </Row>
        <del-quote-detail :show="showDetail" :pass-quote="passQuote" @change-show="showChange"  @update-handler="updateQuote"></del-quote-detail>
        <del-quote-history-track @showHistory="showHistoryHandler" v-show="showHistoryCallBack" :quote-id="selectedRowId"></del-quote-history-track>
    </div>
</template>

<script>
    import DelQuoteDetail from "./del-quote-detail.vue";
    import DelQuoteHistoryTrack from "./components/del-quote-history-track.vue";
    import moment from 'moment'
    export default {
        components: {
            DelQuoteDetail,
            DelQuoteHistoryTrack},
        name: "del-quote-list",
        data() {
            let slf = this;
            return {
                quotes: [],
                passQuote: {},
                showDetail: false,
                total: 0,
                filterStatus: '',
                filterQuoteNo: '',
                selectedRowId : '',
                showHistoryCallBack : false,
                filters: [
                    {label: 'REQUEST_FOR_INFORMATION', value: 'REQUEST_FOR_INFORMATION'},
                    {label: 'RESPONSED', value: 'RESPONSED'},
                    {label: 'REQUEST_FOR_PROPOSAL', value: 'REQUEST_FOR_PROPOSAL'},
                    {label: 'COMPLETED', value: 'COMPLETED'}
                ],
                titles: [
                    {
                        key: 'quoteNo',
                        title: this.$t('DEL_QUOTE_ID'),
                        render: (h, params) => {
                            return h('a', {
                                on: {
                                    'click': (event) => {
                                        slf.showDetail = true;
                                        slf.passQuote = params.row;
                                    }
                                }
                            }, params.row.quoteNo);
                        }
                    },
                    {
                        key: 'createTime',
                        title: this.$t('DEL_QUOTE_DATE'),
                        render: (h, params) => {
                            return h('span', moment(params.row.createTime).format('YYYY-MM-DD LT'));
                        }
                    },
                    {
                        key: 'inquirerEmail',
                        title: this.$t('DEL_QUOTE_EMAIL')
                    },
                    {
                        key: 'inquirerCompanyName',
                        title: this.$t('DEL_QUOTE_COMPANY')
                    },
                    {
                        key: 'contactInfo',
                        title: this.$t('DEL_QUOTE_PHONE')
                    },
                    {
                        key: 'specialRequest',
                        title: this.$t('DEL_QUOTE_SPECIAL'),
                        width: 100,
                        type: 'expand',
                        render: (h, params) => {
                            return h('span', params.row.specialRequest);
                        }
                    },
                    {
                        key: 'status',
                        title: this.$t('DEL_QUOTE_STATUS')
                    }
                ]
            }
        },
        methods: {
            listDelQuote(page) {
                if (!page) {
                    page = 1;
                }
                let param = {page: page, row: 10, status: this.filterStatus, quoteNo: this.filterQuoteNo};
                this.$http.get('/resources/del/quote/list', {params: param}).then(res => {
                    this.quotes = res.data;
                    if (res.data && res.data.length > 0) {
                        this.total = res.data[0].pageCount;
                    }
                });
            },
            showChange(val) {
                this.showDetail = val;
                if (!val) {
                    this.passQuote = {};
                }
            },
            updateQuote() {
                this.listDelQuote(1);
            },
            changePage(page) {
                this.listDelQuote(page);
            },
            searchQuote() {
                this.listDelQuote(1);
            },
            resetSearch() {
                this.filterStatus = '';
                this.filterQuoteNo = '';
                this.listDelQuote(1);
            },
            getHistory(row, index){
                let vm = this;
                this.selectedRowId = row.id.toString();
            },
            showHistoryHandler(event){
                this.showHistoryCallBack = event;
            }
        },
        mounted() {
            this.listDelQuote();
        }
    }
</script>

<style scoped>
    .filter-quote {
        margin: 20px 0;
    }
</style>
