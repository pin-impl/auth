<template>
    <div class="company-search" :style="{width: width, height: height}">
        <Input icon="ios-search" v-model="company" clearable></Input>
        <Card>
            <Row class="company-list" v-for="c in companies" :key="c.id"><Button type="text" @click="selected(c)">{{ c.name }}</Button></Row>
        </Card>
    </div>
</template>
<script>
    export default {
        name: 'company-list',
        data() {
            return {
                company: '',
                imFalse: false,
                imTrue: true,
                companies: [],
                loading: false
            }
        },
        props: {
            companyType: String,
            width: String,
            height: String,
            placeholder: String,
            callback: {},
        },
        methods: {
            getCompanies(query, type, init) {
                let params = {type: type, name: query};
                this.$http.get('/resources/company/listCompanies', {params: params}).then(res => {
                    this.companies = res.data.company;
                });
            },
            selected(val) {
                this.company = val.name;
                this.$emit('callback', val.id);
            },
            init() {
                this.getCompanies('', this.companyType, true);
            }
        },
        watch: {
            company(val) {
                this.getCompanies(val, this.companyType, false);
            }
        },
        mounted() {
            this.init();
        }
    }
</script>
<style scoped>
    .company-search {
        width: 100%;
        height: 350px;
        margin: 0 auto;
    }
    .company-list {
        margin-top: 10px;
        cursor: pointer;
    }
</style>
