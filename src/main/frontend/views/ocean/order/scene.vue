<template>
    <div>
        <Card>
            <Table :loading="loading" stripe :columns="tableTitle" :data="orderScenes"></Table>
            <Button class="scene-add" type="info" shape="circle" @click="pushScene"><Icon type="plus-round"></Icon></Button>
            <div class="scene-save">
                <Button :disabled="invalid" type="info" @click="saveScene">Confirm</Button>
            </div>
        </Card>
        <Page class="page" :page-size="pageSize" @on-change="onPageChanged" :total="total" show-elevator></Page>
        <user-search @callback="showChanged" @return-data="returnData" :show="openUserModel" status="APPROVED" :roles="['ROLE_SUPPLIER_ADMIN']" :default-user="userData"></user-search>
    </div>
</template>
<script>
    import UserSearch from "../components/user-search.vue";

    export default {
        components: {UserSearch},
        name: 'scene',
        data() {
            let vm = this;
            return {
                properties: [],
                entities: [],
                taxRates: [],
                orderTypes: [],
                deliveryBranches: [],
                carriers: [],
                companyCodeList: [],
                orderScenes: [],
                pageSize: 10,
                total: 0,
                userData: {
                    email: '',
                    index: 0
                },
                openUserModel: false,
                invalid: true,
                newScene: {
                    id: undefined,
                    name: undefined,
                    orderType: undefined,
                    legalEntity: undefined,
                    deliveryBranch: undefined,
                    purchaser: undefined,
                    taxRate: undefined,
                    carrier: undefined,
                    shippingAccount: undefined,
                    companyCode: undefined
                },
                loading: false,
                tableTitle: [
                    {
                        title: this.$t('sceneIndex'),
                        key: 'sceneIndex',
                        width: 100,
                        render: (h, params) => {
                            return h('Input', {
                                props: {
                                    value: vm.orderScenes[params.index].sceneIndex
                                },
                                on: {
                                    'on-blur': (event) => {
                                        vm.orderScenes[params.index].sceneIndex = event.target.value;
                                    }
                                }
                            })
                        }
                    },
                    {
                        title: this.$t('sceneName') + '*',
                        key: 'name',
                        width: 150,
                        render: function (h, params) {
                            return h('Input', {
                                props: {
                                    value: vm.orderScenes[params.index].name
                                },
                                on: {
                                    'on-blur': (event) => {
                                        vm.orderScenes[params.index].name = event.target.value;
                                    }
                                }
                            })
                        }
                    },
                    {
                        title: this.$t('sceneOrderType') + '*',
                        key: 'orderType',
                        width: 150,
                        render: function (h, params) {
                            let options = _.map(vm.orderTypes, function (t) {
                                return h('Option', {
                                    props: {
                                        value: t.value
                                    }
                                }, t.value);
                            });
                            return h('Select', {
                                props: {
                                    value: vm.orderScenes[params.index].orderType
                                },
                                on: {
                                    'on-change': (val) => {
                                        vm.orderScenes[params.index].orderType = val;
                                    }
                                }
                            }, options);
                        }
                    },
                    {
                        title: this.$t('sceneLegalEntity') + '*',
                        key: 'legalEntity',
                        width: 150,
                        render: function (h, params) {
                            let options = _.map(vm.entities, function (e) {
                                return h('Option', {
                                    props: {
                                        value: e.entityId
                                    }
                                }, e.entityId);
                            });
                            return h('Select', {
                                props: {
                                    value: vm.orderScenes[params.index].legalEntity
                                },
                                on: {
                                    'on-change': (val) => {
                                        vm.orderScenes[params.index].legalEntity = val;
                                    }
                                }
                            }, options);
                        }
                    },
                    {
                        title: this.$t('sceneDeliveryBranch') + '*',
                        key: 'deliveryBranch',
                        width: 150,
                        render: function (h, params) {
                            let options = _.map(vm.deliveryBranches, function (d) {
                                return h('Option', {
                                    props: {
                                        value: d.value
                                    }
                                }, d.value);
                            });
                            return h('Select', {
                                props: {
                                    value: vm.orderScenes[params.index].deliveryBranch
                                },
                                on: {
                                    'on-change': (val) => {
                                        vm.orderScenes[params.index].deliveryBranch = val;
                                    }
                                }
                            }, options);
                        }
                    },
                    {
                        title: this.$t('scenePurchaser') + '*',
                        key: 'purchaser',
                        width: 250,
                        render: function (h, params) {
                            let search = h('Button', {
                                attrs: {
                                    slot: 'append',
                                    icon: 'ios-search'
                                },
                                on: {
                                    'click': () => {
                                        vm.userData.index = params.index;
                                        vm.openUserModel = true;
                                        vm.userData.email = params.row.purchaser;
                                    }
                                }
                            });
                            let input =  h('Input', {
                                props: {
                                    value: vm.orderScenes[params.index].purchaser
                                },
                                attrs: {
                                    readonly: true,
                                    'element-id': 'user' + params.index
                                }
                            });
                            return h('div', {
                                attrs: {
                                    style: 'display: flex;'
                                }
                            }, [input, search]);
                        }
                    },
                    {
                        title: this.$t('sceneTaxRate') + '*',
                        key: 'taxRate',
                        width: 150,
                        render: function (h, params) {
                            let options = _.map(vm.taxRates, function (tax) {
                                return h('Option', {
                                    props: {
                                        value: tax.name
                                    }
                                }, tax.name);
                            });
                            return h('Select', {
                                props: {
                                    value: vm.orderScenes[params.index].taxRate
                                },
                                on: {
                                    'on-change': (val) => {
                                        vm.orderScenes[params.index].taxRate = val;
                                    }
                                }
                            }, options);
                        }
                    },
                    {
                        title: this.$t('sceneCompanyCode'),
                        key: 'companyCode',
                        width: 150,
                        render: function (h, params) {
                            let options = _.map(vm.companyCodeList, function (c) {
                                return h('Option', {
                                    props: {
                                        value: c.value
                                    }
                                }, c.value);
                            });
                            return h('Select', {
                                props: {
                                    value: vm.orderScenes[params.index].companyCode
                                },
                                on: {
                                    'on-change': val => {
                                        vm.orderScenes[params.index].companyCode = val;
                                    }
                                }
                            }, options);
                        }
                    },
                    {
                        title: this.$t('sceneOperation'),
                        width: 100,
                        render: function (h, params) {
                            return h('Button', {
                                props: {
                                    icon: 'minus-round',
                                    shape: 'circle'

                                },
                                attrs: {
                                    style: 'cursor: pointer;color: red;'
                                },
                                on: {
                                    'click': (event) => {
                                        let index = -1;
                                        if (!params.row.id) {
                                            index = _.findIndex(vm.orderScenes, function (o) {
                                                return !o.id;
                                            });
                                        } else {
                                            vm.deleteScene(params.row.id);
                                            index = _.findIndex(vm.orderScenes, ['id', params.row.id]);
                                        }
                                        if (index > -1) {
                                            vm.orderScenes.splice(index, 1);
                                        }
                                    }
                                }
                            });
                        }
                    }
                ]
            }
        },
        methods: {
            getProperties() {
                let slf = this;
                slf.$http.get('/resources/domain/jdeProperties').then(res => {
                    slf.properties = res.data.properties;
                    slf.orderTypes = _.filter(slf.properties, {type: 'ORDER_TYPE'});
                    slf.deliveryBranches = _.filter(slf.properties, {type: 'DELIVERY_BRANCH'});
                    slf.carriers = _.filter(slf.properties, {type: 'CARRIER'});
                    slf.companyCodeList = _.filter(slf.properties, {type: 'WUXI_COMPANY_CODE'});
                });
            },
            getlegalEntities() {
                let slf = this;
                this.$http.get('/resources/domain/legalEntities').then(res => {
                    slf.entities = res.data;
                });
            },
            getTaxProperties() {
                let slf = this;
                this.$http.get('/resources/domain/taxRates').then(res => {
                    let taxs = res.data.taxRates;
                    slf.taxRates = _.chain(taxs).sortBy('name').value();
                })
            },
            loadPage(page) {
                let slf = this;
                slf.loading = true;
                slf.$http.get('/resources/order/getScenes', {params: {page: page}}).then(res => {
                    slf.orderScenes = res.data.orderSceneInfos;
                    slf.total = res.data.totalCount;
                    slf.loading = false;
                });
            },
            returnData(data) {
                let input = document.querySelector('#user' + data.index);
                input.value = data.value;
                this.orderScenes[data.index].purchaser = data.value;
            },
            showChanged(val) {
                this.openUserModel = val;
            },
            pushScene() {
                let vm = this;
                vm.orderScenes.push(_.extend({}, vm.newScene));
            },
            saveScene() {
                this.$http.post('/resources/order/saveScene', {orderSceneInfos: this.orderScenes}).then(res => {
                    this.$Message.success(this.$t('sceneSaveSuccess'));
                    this.loadPage(1);
                });
            },
            deleteScene(id) {
                this.$http.delete('/resources/order/scene?id=' + id).then(res => {
                    this.$Message.success('Delete scene success!');
                });
            },
            onPageChanged(page) {
                this.loadPage(page);
            }
        },
        watch: {
            orderScenes: {
                handler: function (val) {
                    this.invalid =  !!_.find(val, function (o) {
                        if (!o.name || !o.orderType || !o.legalEntity
                            || !o.deliveryBranch || !o.purchaser || !o.taxRate) {
                            return o;
                        }
                    });
                },
                deep: true
            }
        },
        computed: {

        },
        mounted() {
            this.getProperties();
            this.getlegalEntities();
            this.getTaxProperties();
            this.loadPage(1);
        }
    }
</script>
<style lang="less" scoped>
    .page {
        float: right;
        margin-top: 20px;
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
    .scene-add {
        margin-top: 10px;
        text-align: center;
    }
    .scene-save {
        text-align: right;
    }
</style>
