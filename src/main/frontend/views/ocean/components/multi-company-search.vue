<template>
    <div class="company-search" :style="{width: width}">
        <Input icon="ios-search" v-model="searchCompany" clearable></Input>
        <Card class="supplier-drop" v-show="companies.length > 0 && showDrop">
            <p class="supplier-drop-close" slot="extra" @click.prevent="toggleDrop">
                <Icon type="close"></Icon>
            </p>
            <div class="supplier-drop-list">
                <CheckboxGroup v-model="companyIds">
                    <Row v-for="c in companies" :key="c.id">
                        <Checkbox :label="c.id" :value="c.checked">
                            <span>{{ c.name }}</span>
                        </Checkbox>
                    </Row>
                </CheckboxGroup>
            </div>
        </Card>
    </div>
</template>
<script>
    export default {
        name: 'multi-company-search',
        data() {
            return {
                imFalse: false,
                imTrue: true,
                companies: Array,
                companyIds: [],
                selectedCompanies: Array,
                showDrop: false,
                searchCompany: ''
            }
        },
        props: {
            companyType: String,
            selected: [],
            width: Number
        },
        methods: {
            getCompanies(query, type) {
                let slf = this;
                let params = {type: type, name: query};
                this.$http.get('/resources/company/listCompanies', {params: params}).then(res => {
                    this.companies = res.data.company;
                    _.each(this.companies, function (c) {
                        c.checked = !!_.find(slf.companyIds, {"id": c.id}) || !!_.find(slf.selected, {"id": c.id});
                    });
                    this.showDrop = true;
                });
            },
            debounceGetCompanies(query, type) {
                _.debounce(this.getCompanies(query, type), 500);
            },
            toggleDrop() {
                this.showDrop = false;
            }
        },
        watch: {
            searchCompany: function (val) {
                this.debounceGetCompanies(val, this.companyType);
            },
            companyIds: function (val) {
                let slf = this;
                let selected = _.filter(slf.companies, function (s) {
                    return val.indexOf(s.id) > -1;
                });
                slf.$emit('callback', selected);
            },
            selected: function (val) {
                console.log(val)
                let slf = this;
                if (val) {
                    _.each(slf.companies, function (c) {
                        c.checked = !!_.find(val, {"id": c.id});
                    });
                }
            }
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