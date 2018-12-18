<template>
    <Row justify="center" align="middle" class="row-bottom">
        <header>
            <h3>{{$t('quoteHistory')}}</h3>
        </header>
        <Col>
        <table class="table condensed">
            <thead>
            <tr>
                <th>
                    {{$t('user')}}
                </th>
                <th>
                    {{$t('event')}}
                </th>
                <th>
                    {{$t('eventDescription')}}
                </th>
                <th>
                    {{$t('dateTime')}}
                </th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="h in historyItems">
                <td class="ivu-table-cell">
                    {{h.inquirer}}
                </td>
                <td class="ivu-table-cell">
                    {{h.eventKey}}
                </td>
                <td class="ivu-table-cell">{{h.description}}</td>
                <td class="ivu-table-cell">{{formatDate(h.createAt)}}</td>
            </tr>
            </tbody>
        </table>
        </Col>
    </Row>
</template>
<script>
    import moment from 'moment'
    import DelQuoteHistoryTrack from "./del-quote-history-track.vue";

    export default {
        components: {DelQuoteHistoryTrack
        },
        name: "del-quote-history-track",
        data() {
            return {
                historyItems : [],
            }
        },
        props: {
            quoteId : String
        },
        methods: {
            formatDate(dt) {
                return moment(dt).format('YYYY-MM-DD LT');
            },
            getQuoteHistory(){
                let slf = this;
                let param = {params: {quoteId: this.quoteId , category : "DEL"}};
                slf.$http.get('/resources/quote/history/list', param).then(res => {
                    slf.historyItems = res.data;
                    if(slf.historyItems && slf.historyItems.length > 0){
                        slf.$emit('showHistory',true);
                    } else {
                        slf.$emit('showHistory',false);
                    }
                })
            }
        },
        watch: {
            quoteId: function (newVal) {
                this.getQuoteHistory();
            }
        }
    }
</script>
