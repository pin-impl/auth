<template>
    <Row justify="center" align="middle" class="row-bottom">
        <header>
            <h3>{{$t('requisitionHistory')}}</h3>
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
                <tr v-for="h in orderHistory">
                    <td class="ivu-table-cell">
                        {{h.user}}
                        <span v-show="h.originatingUser">{{$t('submittedBy')}}: {{h.originatingUser}}</span>
                    </td>
                    <td class="ivu-table-cell">
                        {{h.event}}
                    </td>
                    <td class="ivu-table-cell">{{h.description}}</td>
                    <td class="ivu-table-cell">{{formatDate(h.createdAt)}}</td>
                </tr>
                </tbody>
            </table>
        </Col>
    </Row>
</template>
<script>
    import moment from 'moment'

    export default {
        components: {

        },
        name: 'order-history-track',
        data() {
            return {}
        },
        props: {
            orderHistory: Array
        },
        methods: {
            formatDate(dt) {
                return moment(dt).format('YYYY-MM-DD LT');
            }
        }
    }
</script>
