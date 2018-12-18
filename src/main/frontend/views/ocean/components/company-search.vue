<template>
    <div class="company-search" :style="{width: width, height: height}">
        <Select icon="ios-search"
                filterable
                remote
                clearable
                :placeholder="placeholder"
                @on-change="selected"
                :remote-method="remoteSearch"
                v-model="company"
                :loading="loading">
            <Option v-for="c in companies" :value="c.id" :key="c.id">{{c.name}}</Option>
        </Select>
    </div>
</template>
<script>
    export default {
        name: 'company-search',
        data() {
            return {
                company: 0,
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
            initDrop: false
        },
        methods: {
            getCompanies(query, type, init) {
                let params = {type: type, name: query};
                this.$http.get('/resources/company/listCompanies', {params: params}).then(res => {
                    this.companies = res.data.company;
                    this.showDrop = true;
                    if (init && this.companies && this.companies.length > 0) {
                        this.company = this.companies[0].id;
                    }
                });
            },
            toggleDrop() {
                this.showDrop = false;
            },
            remoteSearch(query) {
                this.loading = true;
                this.getCompanies(query, this.companyType, false);
                this.loading = false;
            },
            selected(val) {
                this.$emit('callback', val);
            },
            init() {
                if (this.initDrop) {
                    this.getCompanies('', this.companyType, true);
                }
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
    .supplier-drop {
        z-index: 100;
        left: auto;
        text-align: left;
    }
    .supplier-drop-list {
        max-height: 320px;
        overflow-y: auto;
    }
    .supplier-drop-close {
        position: absolute;
        top: -15px;
        cursor: pointer;
    }
    .selected-suppliers-list {
        width: 350px;
        margin: 10px auto;
        height: 200px;
        overflow-y: auto;
    }
</style>
