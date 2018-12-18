<template>
    <Modal
            v-model="showPage"
            :title="$t('viewDiscount')"
            width="60">
        <Row>
            <Col span="12" class-name="detail-separator">
            <Row>
                <Form>
                    <FormItem :label="$t('discountTitleDesc')">
                        <Input class="detail-input" v-model="discountDetailData.description" :readonly="disabledEdit"></Input>
                    </FormItem>
                    <Row class-name="detail-line"></Row>
                    <FormItem :label="$t('discountTitleType')">
                        <Select class="detail-input" v-model="discountDetailData.discountType" :disabled="disabledEdit">
                            <Option value="PERCENT">Percent</Option>
                            <Option value="TIER">Tier</Option>
                        </Select>
                    </FormItem>
                    <FormItem :label="$t('discount')" v-if="discountDetailData.discountType === 'PERCENT'">
                        <Input class="detail-input" v-model="discountDetailData.discount" :readonly="disabledEdit"></Input>
                    </FormItem>
                    <FormItem :label="$t('discount')" v-if="discountDetailData.discountType === 'TIER'">
                        <table style="float: right;width: 300px;">
                            <tr v-for="tier in discountDetailData.tiers" :key="tier.id">
                                <td width="20%">
                                    <label class="control-label">{{ $t('discountAmount') }}:</label>
                                </td>
                                <td width="30%">
                                    <Input :number="imTrue" :readonly="disabledEdit" v-model="tier.quantity"></Input>
                                </td>
                                <td width="10%" style="text-align: right;"><label class="control-label">%</label></td>
                                <td width="30%" style="text-align: right">
                                    <FormItem>
                                        <Input :number="imTrue" v-model="tier.percentage" :readonly="disabledEdit"></Input>
                                    </FormItem>
                                </td>
                                <td v-if="!disabledEdit">
                                    <Icon @click="popTier(tier)" style="color: red;cursor: pointer;" type="minus-circled"></Icon>
                                </td>
                            </tr>
                            <tr v-if="!disabledEdit">
                                <td width="20%">
                                    <label class="control-label">{{ $t('discountAmount') }}:</label>
                                </td>
                                <td width="30%">
                                    <Input :number="imTrue" v-model="newTier.quantity"></Input>
                                </td>
                                <td width="10%" style="text-align: right;"><label class="control-label">%</label></td>
                                <td width="30%" style="text-align: right">
                                    <Input :number="imTrue" v-model="newTier.percentage"></Input>
                                </td>
                                <td v-if="!disabledEdit">
                                    <Icon @click="pushTier(newTier)" style="color: green; cursor: pointer;" type="plus-circled"></Icon>
                                </td>
                            </tr>
                        </table>
                    </FormItem>
                    <Row class-name="detail-line"></Row>
                    <FormItem :label="$t('discountTitleStart')">
                        <DatePicker type="date" placeholder="Select date" format="yyyy-MM-dd" v-model="startDate" class="detail-input" :readonly="disabledEdit"></DatePicker>
                    </FormItem>
                    <FormItem :label="$t('discountTitleEnd')">
                        <DatePicker type="date" placeholder="Select date" format="yyyy-MM-dd" v-model="endDate" class="detail-input" :readonly="disabledEdit"></DatePicker>
                    </FormItem>
                    <Row class-name="detail-line"></Row>
                    <FormItem :label="$t('combineDiscounts')">
                        <RadioGroup @on-change="allowOverlapChange" v-model="allowOverlapRadio">
                            <Radio label="Y" :disabled="disabledEdit">
                                <span>Yes</span>
                            </Radio>
                            <Radio label="N" :disabled="disabledEdit">
                                <span>No</span>
                            </Radio>
                        </RadioGroup>
                    </FormItem>
                    <FormItem :label="$t('supplierFounded')">
                        <RadioGroup @on-change="appliedToLabNetworkCostChange" v-model="appliedToLabNetworkCostRadio">
                            <Radio label="Y" :disabled="disabledEdit">
                                <span>Yes</span>
                            </Radio>
                            <Radio label="N" :disabled="disabledEdit">
                                <span>No</span>
                            </Radio>
                        </RadioGroup>
                    </FormItem>
                    <Row class-name="detail-line"></Row>
                    <FormItem :label="$t('eligibleProductCategories')">
                        <div class="group-list" v-if="disabledEdit && !discountDetailData.appliedToAllProductGroups">
                            <span v-for="group in discountDetailData.productGroups">{{ group.shortName }}</span>
                        </div>
                        <div class="group-list" v-if="disabledEdit && discountDetailData.appliedToAllProductGroups">
                            <span v-for="group in allProductGroups">{{ group.shortName }}</span>
                        </div>
                        <CheckboxGroup v-if="!disabledEdit" v-model="groupIds" class="detail-group">
                            <Checkbox v-for="group in allProductGroups" :label="group.id" :key="group.id">
                                {{ group.shortName }}
                            </Checkbox>
                        </CheckboxGroup>
                    </FormItem>
                </Form>
            </Row>
            </Col>
            <Col span="12">
            <Row>
                <Form>
                    <FormItem :label="$t('eligibleSuppliers')">
                        <Input class="company-search-input" :disabled="disabledEdit || discountDetailData.appliedToAllSuppliers" :readonly="disabledEdit || discountDetailData.appliedToAllSuppliers" clearable icon="ios-search" v-model="searchSupplier"></Input>
                        <Card class="supplier-drop" v-show="supplierCompanies.length > 0 && showSupplierDrop">
                            <p class="supplier-drop-close" slot="extra" @click.prevent="toggleSupplierDrop">
                                <Icon type="close"></Icon>
                            </p>
                            <Card class="supplier-drop-list" :bordered="imFalse" :dis-hover="imTrue">
                                <CheckboxGroup v-model="supplierIds">
                                    <Row v-for="supplier in supplierCompanies" :key="supplier.id">
                                        <Checkbox :label="supplier.id">
                                            <span>{{ supplier.name }}</span>
                                        </Checkbox>
                                    </Row>
                                </CheckboxGroup>
                            </Card>
                        </Card>
                        <Card class="selected-suppliers-list">
                            <Row>
                                <Checkbox :disabled="disabledEdit" v-model="discountDetailData.appliedToAllSuppliers" @on-change="selectAllSupplier">
                                    <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;All Companies</span>
                                </Checkbox>
                            </Row>
                            <Row v-for="s in discountDetailData.suppliers" :key="s.id">
                                <div style="float: left;cursor: pointer; color: red;" v-if="!disabledEdit" @click="cancelSupplierChecked(s)"><Icon type="minus-circled"></Icon></div>
                                <div style="float: left;cursor: pointer; color: silver;" v-if="disabledEdit"><Icon type="minus-circled"></Icon></div>
                                <div style="float: right;">{{ s.name }}</div>
                            </Row>
                        </Card>
                    </FormItem>
                    <FormItem :label="$t('eligibleBuyers')">
                        <Input class="company-search-input" :disabled="disabledEdit || discountDetailData.appliedToAllBuyers" :readonly="disabledEdit || discountDetailData.appliedToAllBuyers" clearable icon="ios-search" v-model="searchBuyer"></Input>
                        <Card class="supplier-drop" v-show="buyerCompanies.length > 0 && showBuyerDrop">
                            <p class="supplier-drop-close" slot="extra" @click.prevent="toggleBuyerDrop"><Icon type="close"></Icon></p>
                            <Card class="supplier-drop-list" :bordered="imFalse" :dis-hover="imTrue">
                                <CheckboxGroup v-model="buyerIds">
                                    <Row v-for="buyer in buyerCompanies" :key="buyer.id">
                                        <Checkbox :label="buyer.id">
                                            <span>{{ buyer.name }}</span>
                                        </Checkbox>
                                    </Row>
                                </CheckboxGroup>
                            </Card>
                        </Card>
                        <Card class="selected-suppliers-list">
                            <Row>
                                <Checkbox :disabled="disabledEdit" v-model="discountDetailData.appliedToAllBuyers" @on-change="selectAllBuyer">
                                    <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;All Companies</span>
                                </Checkbox>
                            </Row>
                            <Row v-for="s in discountDetailData.buyers" :key="s.id">
                                <div style="float: left;cursor: pointer; color: red;" v-show="!disabledEdit" @click="cancelBuyerChecked(s)"><Icon type="minus-circled"></Icon></div>
                                <div style="float: left;cursor: pointer; color: silver;" v-show="disabledEdit"><Icon type="minus-circled"></Icon></div>
                                <div style="float: right;">{{ s.name }}</div>
                            </Row>
                        </Card>
                    </FormItem>
                </Form>
            </Row>
            </Col>
        </Row>
        <div slot="footer">
            <Button type="primary" v-show="disabledEdit" :disabled="!canEdit" @click="editDetail">Edit</Button>
            <Button type="primary" @click="showPage = false">Cancel</Button>
            <Button type="primary" v-show="!disabledEdit" @click="saveEditDetail">Confirm</Button>
        </div>
    </Modal>
</template>
<script>
    import moment from 'moment'
    import companySearch from '../components/company-search'
    import canEditTable from '../../tables/editable-table'
    export default {
        name: 'discount-detail',
        components: { companySearch, canEditTable },
        data() {
            return {
                imTrue: true,
                imFalse: false,
                searchSupplier: '',
                searchBuyer: '',
                supplierCompanies: [],
                buyerCompanies: [],
                supplierIds: [],
                buyerIds: [],
                showSupplierDrop: false,
                showBuyerDrop: false,
                discountDetailData: {},
                allProductGroups: [],
                groupIds: [],
                disabledEdit: true,
                newTier: {},
                showPage: this.show
            }
        },
        props: {
            discountId: 0,
            show: false,
            canEdit: true,
            changeShow: {}
        },
        methods: {
            getCompanies(query, type) {
                let slf = this;
                let params = {type: type, name: query};
                this.$http.get('/resources/company/listCompanies', {params: params}).then(res => {
                    if (type === 'supplier') {
                        this.supplierCompanies = res.data.company;
                        _.each(slf.supplierCompanies, function (c) {
                            if (slf.supplierIds.indexOf(c.id) === -1 && !!_.find(slf.discountDetailData.suppliers, {"id": c.id})) {
                                slf.supplierIds.push(c.id);
                            }
                        });
                        this.showSupplierDrop = true;
                    } else {
                        this.buyerCompanies = res.data.company;
                        _.each(slf.buyerCompanies, function (c) {
                            if (slf.buyerIds.indexOf(c.id) === -1
                                    && !!_.find(slf.discountDetailData.buyers, {"id": c.id})) {
                                slf.buyerIds.push(c.id);
                            }
                        });
                        this.showBuyerDrop = true;
                    }
                });
            },
            toggleSupplierDrop() {
                this.showSupplierDrop = false;
            },
            toggleBuyerDrop() {
                this.showBuyerDrop = false;
            },
            cancelSupplierChecked(supplier) {
                // let index = this.discountDetailData.suppliers.indexOf(supplier);
                // if (index > -1) {
                //     this.discountDetailData.suppliers.splice(index, 1);
                // }
                let id = this.supplierIds.indexOf(supplier.id);
                if (id > -1) {
                    this.supplierIds.splice(id, 1);
                }

            },
            cancelBuyerChecked(buyer) {
                // let index = this.discountDetailData.buyers.indexOf(buyer);
                // if (index > -1) {
                //     this.discountDetailData.buyers.splice(index, 1);
                // }
                let id = this.buyerIds.indexOf(buyer.id);
                if (id > -1) {
                    this.buyerIds.splice(id, 1);
                }

            },
            getDiscountInfo(id) {
                let slf = this;
                this.$http.get('/resources/discount/id/' + id).then(res => {
                    this.discountDetailData = res.data;
                    slf.groupIds = _.map(slf.discountDetailData.productGroups, function (g) {
                        return g.id;
                    });
                    if (slf.discountDetailData.appliedToAllProductGroups) {
                        slf.groupIds = _.map(this.allProductGroups, function (g) {
                            return g.id;
                        });
                    }
                    if (slf.discountDetailData.tiers) {
                        slf.discountDetailData.tiers.forEach(function (entry) {
                            entry.percentage = 100 - entry.percentage * 100;
                            entry.percentage = +entry.percentage.toFixed(1);
                        });
                    }
                });
            },
            getProductGroups() {
                this.$http.get('/resources/discount/groups').then(res => {
                    this.allProductGroups = res.data.productGroups;
                })
            },
            editDetail() {
                this.disabledEdit = false;
            },
            saveEditDetail() {
                if(new Date(this.discountDetailData.expirationDate) < new Date(this.discountDetailData.effectiveDate)){
                    this.$Notice.error({title: '起始日期不能大于结束日期'});
                    return;
                }
                if (!this.discountDetailData.tiers) {
                    this.discountDetailData.tiers = [];
                }
                this.discountDetailData.tiers.forEach(entry => {
                    entry.percentage = (100 - entry.percentage) / 100;
                    entry.percentage = +entry.percentage.toFixed(3);
                });
                this.discountDetailData.appliedToAllProductGroups = this.discountDetailData.productGroups.length === this.allProductGroups.length;
                if (!!this.discountDetailData.id) {
                    this.editDiscount();
                } else {
                    this.createDiscount();
                }
                this.showPage = false;
            },
            createDiscount() {
              this.$http.post('/resources/discount', this.discountDetailData).then(res => {
                  if (res.data.code === 'BAD_REQUEST') {
                      this.$Notice.error({title: '折扣日期有重叠，请调整您的折扣起止日期。'});
                  } else if (res.data.code === 'SUCCESS') {
                      this.$Notice.success({desc: 'Success'});
                  }
              }).catch(err => {
                  this.$Notice.error({title: 'Discount create error!'});
              });
            },
            editDiscount() {
                this.$http.post('/resources/discount/' + this.discountDetailData.id, this.discountDetailData).then(res => {
                    if (res.data.code === 'BAD_REQUEST') {
                        this.$Notice.error({title: '折扣日期有重叠，请调整您的折扣起止日期。'});
                    } else if (res.data.code === 'SUCCESS') {
                        this.$Notice.success({desc: 'Success'});
                    }
                }).catch(e => {
                    this.$Notice.error({title: 'Discount save error!'});
                });
            },
            popTier(tier) {
                let idx = this.discountDetailData.tiers.indexOf(tier);
                if (idx > -1) {
                    this.discountDetailData.tiers.splice(idx, 1);
                }
            },
            pushTier(tier) {
                if (!this.discountDetailData.tiers) {
                    this.discountDetailData.tiers = [];
                }
                this.discountDetailData.tiers.push(_.extend({}, tier));
                this.newTier.percentage = 0;
                this.newTier.quantity = 0;
            },
            allowOverlapChange(val) {
                if (val === 'Y') {
                    this.discountDetailData.allowOverlap = true;
                } else {
                    this.discountDetailData.allowOverlap = false;
                }
            },
            appliedToLabNetworkCostChange(val) {
                if (val === 'Y') {
                    this.discountDetailData.appliedToLabNetworkCost = true;
                } else {
                    this.discountDetailData.appliedToLabNetworkCost = false;
                }
            },
            selectAllSupplier(val) {
                if (val) {
                    this.discountDetailData.suppliers = [];
                }
            },
            selectAllBuyer(val) {
                if (val) {
                    this.discountDetailData.buyers = [];
                }
            }
        },
        watch: {
            searchSupplier: function (newVal) {
                this.getCompanies(newVal, 'supplier');
            },
            searchBuyer: function (newVal) {
                this.getCompanies(newVal, 'buyer');
            },
            buyerIds: function (val) {
                let slf = this;
                if (!slf.discountDetailData.buyers) {
                    slf.discountDetailData.buyers = [];
                }
                _.each(slf.buyerCompanies, function (s) {
                    let index = val.indexOf(s.id);
                    if (index > -1 && !_.find(slf.discountDetailData.buyers, {"id": s.id})) {
                        slf.discountDetailData.buyers.push(s);
                    }
                    if (index === -1) {
                        _.remove(slf.discountDetailData.buyers, {"id": s.id});
                    }
                });
            },
            supplierIds: function (val) {
                let slf = this;
                if (!slf.discountDetailData.suppliers) {
                    slf.discountDetailData.suppliers = [];
                }
                _.each(slf.supplierCompanies, function (s) {
                    let index = val.indexOf(s.id);
                    if (index > -1 && ! _.find(slf.discountDetailData.suppliers, {"id": s.id})) {
                        slf.discountDetailData.suppliers.push(s);
                    }
                    if (index === -1) {
                        _.remove(slf.discountDetailData.suppliers, {"id": s.id});
                    }
                });
            },
            groupIds: function (val) {
                let slf = this;
                let selected = _.filter(slf.allProductGroups, function (p) {
                    return val.indexOf(p.id) > -1;
                });
                slf.discountDetailData.productGroups = selected;
            },
            show(val) {
                let slf = this;
                this.searchSupplier = '';
                this.searchBuyer = '';
                this.showSupplierDrop = false;
                this.showBuyerDrop = false;
                this.supplierIds = [];
                this.buyerIds = [];
                this.groupIds = [];
                if (this.discountId && this.discountId !== 0) {
                    this.getDiscountInfo(this.discountId);
                    this.showPage = true;
                    this.disabledEdit = true;
                } else {
                    this.showPage = true;
                    this.disabledEdit = false;
                    this.discountDetailData = {};
                    this.startDate = '';
                    this.endDate = '';
                    this.allowOverlapRadio = undefined;
                    this.appliedToLabNetworkCostRadio = undefined;
                    this.supplierCompanies =  [];
                    this.buyerCompanies = [];
                    _.each(slf.allProductGroups, function (p) {
                        slf.groupIds.push(p.id);
                    });
                }
                this.showPage = val;
            },
            showPage(val) {
                this.$emit('change-show', val);
            },
            disabledEdit(val) {
                if (!val && this.discountDetailData.tiers) {
                    this.discountDetailData.tiers.forEach(entry => {
                        entry.percentage = (100 - entry.percentage) / 100;
                        entry.percentage = +entry.percentage.toFixed(3);
                    });
                }
            }
        },
        filters: {
            dateYMD(date) {
                if (date) {
                    return moment(date).format('YYYY-MM-DD');
                }
            }
        },
        computed: {
            startDate: {
                get() {
                    if (!!this.discountDetailData.effectiveDate) {
                        return moment(this.discountDetailData.effectiveDate).toDate();
                    } else {
                        return '';
                    }
                },
                set(val) {
                    if (val) {
                        this.discountDetailData.effectiveDate = val.valueOf();
                    } else {
                        this.discountDetailData.effectiveDate = val;
                    }
                }
            },
            endDate: {
                get() {
                    if (!!this.discountDetailData.expirationDate) {
                        return moment(this.discountDetailData.expirationDate).toDate();
                    } else {
                        return '';
                    }
                },
                set(val) {
                    if (val) {
                        this.discountDetailData.expirationDate = val.valueOf();
                    } else {
                        console.log(val)
                        this.discountDetailData.expirationDate = val;
                    }
                }

            },
            allowOverlapRadio: {
                get() {
                    return this.discountDetailData.allowOverlap ? 'Y' : 'N';
                },
                set(val) {
                    this.discountDetailData.allowOverlap = !!val;
                }
            },
            appliedToLabNetworkCostRadio: {
                get() {
                    return this.discountDetailData.appliedToLabNetworkCost ? 'Y' : 'N';
                },
                set(val) {
                    this.discountDetailData.appliedToLabNetworkCost = !!val;
                }
            }
        },
        mounted() {
            this.getProductGroups();
        }
    }
</script>
<style scoped>
    .detail-line {
        height: 1px;
        background-color: silver;
        width: 90%;
        margin-bottom: 10px;
    }
    .detail-separator {
        border-right: solid silver 1px;
    }
    .supplier-drop {
        width: 260px;
        position: absolute;
        z-index: 100;
        top: 35px;
        right: 30px;
        max-height: 370px;
    }
    .supplier-drop-list {
        max-height: 350px;
        overflow-y: auto;
        border: 0;
    }
    .supplier-drop-close {
        position: absolute;
        top: -15px;
        cursor: pointer;
    }
    .selected-suppliers-list {
        width: 350px;
        margin: 10px 20px;
        height: 200px;
        overflow-y: auto;
        float: right;
    }
    .detail-input {
        width: 200px;
        float: right;
        margin-right: 20px;
    }
    .detail-group {
        width: 300px;
        float: right;
    }
    .company-search-input {
        width: 260px;
        float: right;
        margin-right: 30px;
    }
    .group-list {
        width: 250px;
        word-break: break-all;
        float: right;
    }
    .group-list span {
        padding-left: 10px;
    }
</style>
