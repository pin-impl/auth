<template>
    <Modal v-model="showDetail" width="60">
        <div slot="header" class="del-header">
            <div class="del-header-title"><p>DEL Quote NO.: {{quote.quoteNo}}</p></div>
            <div class="del-header-editor">
                <Button type="info" @click="editable = true;quote.status = 'RESPONSED';">{{$t('edit')}}</Button>
            </div>
        </div>
        <Row>
            <Col span="8">
                <div>
                    <span class="del-font-bold">Quote By:</span> {{quote.inquirerEmail}}<br/>
                    <span class="del-font-bold">Phone:</span> {{quote.contactInfo}}
                </div>
            </Col>
            <Col span="8">
                <div>
                    <span class="del-font-bold">Company:</span> {{quote.inquirerCompanyName}}<br/>
                    <span class="del-font-bold">Create Date:</span> {{ createDate }}
                </div>
            </Col>
            <Col span="8">
                <div>
                    <span v-if="!editable"><span class="del-font-bold">Status:</span> {{quote.status}}</span>
                    <span v-if="editable"> <span class="del-font-bold">Status:</span>
                        <Select :disabled="!editable" v-model="quote.status" @on-change="accessChanged = true">
                            <Option v-for="s in requestStatus" :value="s" :key="s" :label="s"></Option>
                        </Select>
                    </span>
                </div>
            </Col>
        </Row>
        <Row class="del-quote-title del-font-bold">
            <Col span="8">DEL Lib ID</Col>
            <Col span="8">RFP By Customer</Col>
            <Col span="8">Allow To Visit &nbsp;<Tooltip v-show="editable" content="Select All" placement="top-start"><Checkbox @on-change="allowVisitChanged"></Checkbox></Tooltip></Col>
        </Row>
        <Row v-for="item in quoteItems" :key="item.externalLibId" class="del-quote-content">
            <Col span="8">{{item.externalLibId}}</Col>
            <Col span="8"><Checkbox :disabled="true" v-model="item.requestStatus === 'REQUEST_FOR_PROPOSAL'"></Checkbox></Col>
            <Col span="8"><Checkbox :disabled="!editable" v-model="item.access" @on-change="changeAccess(item)"></Checkbox></Col>
        </Row>
        <Row v-show="editable" class="del-quote-content">
            <Col span="8">
                <Input v-model="newItem.externalLibId"></Input>
            </Col>
            <Col span="8" offset="8">
                <Checkbox :disabled="!editable" v-model="newItem.access" @on-change="changeAccess(newItem)"></Checkbox>
            </Col>
        </Row>
        <Row v-show="editable">
            <Button type="info" shape="circle" @click="addItem">Add</Button>
        </Row>
        <Row class="del-condition-panel">
            <Card :bordered="false">
                <p slot="title">Special Request:</p>
                <p class="del-word-break">{{quote.specialRequest}}</p>
            </Card>
        </Row>
        <Row class="del-condition-panel">
            <h3>Descriptor:</h3>
            <Row class="del-quote-title del-font-bold">
                <Col span="8">Descriptor</Col>
                <Col span="8">&nbsp;&nbsp;&nbsp;&nbsp;</Col>
                <Col span="8">Allow To Visit</Col>
            </Row>
            <Row v-for="item in descriptors" :key="item.externalLibId" class="del-quote-content">
                <Col span="8">{{item.displayedName}}</Col>
                <Col span="8">&nbsp;&nbsp;&nbsp;&nbsp;</Col>
                <Col span="8"><Checkbox :disabled="!editable" v-model="item.access" @on-change="changeAccess(item)"></Checkbox></Col>
            </Row>
            <Row v-show="editable">
                <Button type="info" shape="circle" @click="addDescriptor">Add</Button>
            </Row>
            <form v-show="isAddDescriptor && editable">
                <table class="table-spacing">
                    <tr>
                        <td><span class="del-font-bold">Displaying Name</span></td>
                        <td><span class="del-font-bold">File</span></td>
                        <td style="width: 15%"></td>
                        <td style="width: 10%"></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td><input class="ivu-input" style="width: auto;" type="text" v-model="displayedName"/></td>
                        <td><input ref="filePath" type="file" @change="getFile($event)" /></td>
                        <td></td>
                        <td></td>
                        <td><button class="ivu-btn ivu-btn-info" :disabled="isSubmitting || !descriptorFile || !displayedName || (displayedName && displayedName.length > 50)" @click="submitForm($event)">Submit</button></td>
                        <td><Button type="ghost" @click="isAddDescriptor = false">Cancel</Button></td>
                    </tr>
                    <tr v-if="displayedName && displayedName.length > 50">
                        <td colspan="2" style="color: red">{{$t('nameTooLong')}}</td>
                    </tr>
                </table>
            </form>
        </Row>
        <Row class="del-condition-panel">
            <h2>Customer prefered screending conditions:</h2>
        </Row>
        <Row>
            <div class="del-condition-line">
                <div class="del-condition" v-for="c in allConditions" :key="c.c">
                    <Checkbox :value="c.checked" :disabled="true">{{c.c}}</Checkbox>
                </div>
            </div>
        </Row>
        <Row slot="footer">
            <Col span="2" offset="20">
                <Button type="info" :disabled="!dataChanged || !editable" @click="submit">{{$t('confirm')}}</Button>
            </Col>
            <Col span="2" v-show="editable">
                <Button type="ghost" @click="editable = false">{{$t('cancel')}}</Button>
            </Col>
        </Row>
    </Modal>
</template>

<script>
    import moment from 'moment';
    export default {
        components: {},
        name: "del-quote-detail",
        data() {
            return {
                quote: {status: 'RESPONSED'},
                quoteItems: [],
                showDetail: false,
                requestStatus: ['REQUEST_FOR_INFORMATION', 'RESPONSED', 'REQUEST_FOR_PROPOSAL', 'COMPLETED'],
                editable: false,
                newItem: {},
                conditions: [],
                itemSize: 0,
                accessChanged: false,
                constConditions: ['High-concentration protein group', 'Low-concentration protein group', 'No protein control group'],
                descriptors: [],
                showUpload:false,
                isAddDescriptor: false,
                isSubmitting: false,
                descriptorFile: undefined,
                displayedName: ''
            }
        },
        props: {
            show: false,
            passQuote: {},
            changeShow: {},
            updateHandler: {}
        },
        methods: {
            getQuoteItems() {
                let slf = this;
                slf.quoteItems = [];
                let param = {params: {quoteId: this.quote.id}};
                slf.$http.get('/resources/del/quote/items', param).then(res => {
                    let items = res.data;
                    if (items) {
                        items.forEach(q => {
                            slf.quoteItems.push(Object.assign({}, q, {access: q.accessStatus === 'ALLOWED'}));
                        });
                        slf.itemSize = items.length;
                    }
                });
            },
            changeAccess(item) {
                if (item.access) {
                    item.accessStatus = 'ALLOWED';
                } else {
                    item.accessStatus = 'NOT_ALLOWED';
                }
                this.accessChanged = true;
            },
            addItem() {
                let slf = this;
                if (slf.newItem.externalLibId) {
                    let hasIn = _.filter(slf.quoteItems, function (i) {
                        return i.externalLibId === slf.newItem.externalLibId;
                    });
                    if (hasIn.length > 0) {
                        this.$Notice.error({desc: 'DEL Lib ID should be duplicate.'});
                        return;
                    }
                    this.changeAccess(this.newItem);
                    this.newItem.quoteId = this.quote.id;
                    this.quoteItems.push(JSON.parse(JSON.stringify(this.newItem)));
                    this.newItem = {};
                }
            },
            submit() {
                let param = {id: this.quote.id, status: this.quote.status, delQuoteItems: this.quoteItems, descriptorItems: this.descriptors};
                this.$http.post('/resources/del/quote/update', param).then(res => {
                    this.$Notice.success({desc: 'Success'});
                    this.$emit('update-handler');
                });
                this.showDetail = false;
            },
            getFile(event) {
                if (!event.target.files[0]) {
                    this.descriptorFile = undefined;
                    return;
                }
                if (event.target.files[0].type !== 'application/pdf') {
                    this.$Notice.error({desc: 'Only supports PDF files.'});
                    this.descriptorFile = undefined;
                    return;
                }
                this.descriptorFile = event.target.files[0];
            },
            submitForm(event) {
                let slf = this;
                slf.isSubmitting = true;
                event.preventDefault();
                let formData = new FormData();
                formData.append('file', slf.descriptorFile);
                formData.append('quoteId', slf.quote.id);
                formData.append('displayedName', slf.displayedName);
                let config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };

                this.$http.post('/resources/del/quote/upload/descriptor', formData, config).then(function (res) {
                    slf.getDescriptors();
                    slf.isAddDescriptor = false;
                    slf.isSubmitting = false;
                })
            },
            getDescriptors() {
                let slf = this;
                let param = {params: {quoteId: this.quote.id}};
                slf.$http.get('/resources/del/quote/list/descriptor', param).then(res => {
                    slf.descriptors = res.data;
                    if (slf.descriptors) {
                        slf.descriptors.forEach(q => {
                            q.access = q.accessStatus === 'ALLOWED';
                        });
                    }
                });
            },
            addDescriptor() {
                let slf = this;
                slf.isAddDescriptor = true;
                slf.descriptorFile = undefined;
                slf.displayedName = '';
                slf.$refs.filePath.value ='';
            },
            allowVisitChanged(val) {
                if (val) {
                    this.quoteItems.forEach(item => {
                        item.accessStatus = 'ALLOWED';
                        item.access = true;
                    });
                } else {
                    this.quoteItems.forEach(item => {
                        item.accessStatus = 'NOT_ALLOWED';
                        item.access = false;
                    });
                }
                this.accessChanged = true;
            }
        },
        watch: {
            passQuote: {
                handler(val) {
                    this.quote = JSON.parse(JSON.stringify(val));
                    if (val.id) {
                        this.getQuoteItems();
                        this.getDescriptors();
                    }
                    if (this.quote.conditions) {
                        this.conditions = this.quote.conditions.split('^^^');
                    }
                },
                deep: true
            },
            showDetail(val) {
                this.$emit('change-show', val);
                this.editable = false;
            },
            show(val) {
                this.showDetail = val;
            }
        },
        computed: {
            createDate() {
                return moment(this.quote.createTime).format('YYYY-MM-DD LT');
            },
            dataChanged() {
                return this.itemSize !== this.quoteItems.length || this.accessChanged;
            },
            allConditions() {
                let slf = this;
                let allComn = [];
                _.each(slf.constConditions, function (cc) {
                    if (!slf.conditions.includes(cc)) {
                        allComn.push({c: cc, checked: false});
                    }
                });
                _.each(slf.conditions, function (c) {
                    allComn.push({c: c, checked: true});
                });
                return allComn;
            }
        }
    }
</script>

<style scoped>
    .del-quote-title {
        font-size: 20px;
        background: #88bcf3;
        text-align: center;
        margin: 10px 0;
    }
    .del-quote-content {
        text-align: center;
        margin-bottom: 10px;
    }
    .del-condition-panel {
        margin-top: 20px;
    }
    .del-condition-line {
        text-align: center;
    }
    .del-condition {
        display: inline-block;
        margin: 10px 20px 0 20px;
    }
    .del-font-bold {
        font-weight: bold;
    }
    .del-word-break {
        word-wrap: break-word;
    }
    .table-spacing {
        border-collapse: separate;
        border-spacing: 12px 12px;
    }
    .del-header {
        display: flex;
        width: 80%;
    }
    .del-header-title {
        flex: 9;
    }
    .del-header-editor {
        flex: 1;
    }
</style>
