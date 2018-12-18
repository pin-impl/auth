<template>
    <div>
        <Card>
            <Form class="landing-page">
                <FormItem :label="$t('templates')" class="landing-page-width">
                    <div class="template">
                        <Select class="template-drop" v-model="template.id" @on-change="templateSelected">
                            <Option value="">Add Template</Option>
                            <Option v-for="t in templates" :key="t.id" :value="t.id">
                                <span>{{ t.name }}</span>
                            </Option>
                        </Select>
                        <Button class="template-delete" type="error" @click="dropPromotion">Delete</Button>
                    </div>
                </FormItem>
                <FormItem :label="$t('templateName') + ' *'" class="landing-page-width">
                    <Input v-model="template.name"></Input>
                </FormItem>
                <FormItem :label="$t('title') + ' *'" class="landing-page-width">
                    <Input v-model="template.title"></Input>
                </FormItem>
                <Row>
                    <Col span="6">
                        <FormItem :label="$t('image') + ' *'">
                            <Upload accept="image/*"
                                    :show-upload-list="false"
                                    :on-success="imageUploadSuccess"
                                    :format="['jpg','jpeg','png']"
                                    :max-size="5120"
                                    :on-exceeded-size="handleMaxSize"
                                    multiple
                                    type="drag"
                                    :with-credentials="true"
                                    :action="imageAction"
                                    :data="imageUUID"
                                    style="display: inline-block;width:200px;">
                                <div style="width: 200px;height:300px;line-height: 300px;">
                                    <Icon type="camera" size="20"></Icon>
                                </div>
                                <img style="width: 200px;height: 300px;position: absolute;top: 0;left: 0;" :src="template.image">
                            </Upload>
                        </FormItem>
                    </Col>
                    <Col span="18">
                        <FormItem :label="$t('content')">
                            <textEditor :init-content="templateContent" @callback="contentChanged"></textEditor>
                        </FormItem>
                    </Col>
                </Row>
                <FormItem :label="$t('attachment')" class="landing-page-width">
                    <Upload
                            :with-credentials="true"
                            multiple
                            :show-upload-list="false"
                            :data="imageUUID"
                            :on-success="fileUploadSuccess"
                            :max-size="15360"
                            :on-exceeded-size="handleMaxSize"
                            :action="attachmentAction">
                        <Button type="ghost" icon="ios-cloud-upload-outline">Upload files</Button>
                    </Upload>
                    <div style="display: flex;">
                        <Button class="attach-file" v-for="a in template.attachments" :key="a.name" @click="download()"><a download :href="a.url">{{ a.name }}</a></Button>
                    </div>
                </FormItem>
                <FormItem>
                    <Button type="primary" @click="saveTemplate">{{ $t('confirm') }}</Button>
                </FormItem>
            </Form>
        </Card>
    </div>
</template>
<script>
    import textEditor from '../text-editor/text-editor.vue';
    import env from '../../../build/env.js'
    export default {
        name: 'landing-page',
        components: { textEditor },
        data() {
            return {
                template: {
                    image: '',
                    attachments: []
                },
                templates: [],
                imageUUID: {
                    uuid: (new Date()).valueOf()
                },
                templateContent: '',
                imageAction: env === 'development' ? 'http://localhost:8282/resources/image/upload' : '/resources/image/upload',
                attachmentAction: env === 'development' ? 'http://localhost:8282/resources/file/upload' : '/resources/file/upload'
            }
        },
        methods: {
            getTemplates() {
                this.$http.get('/resources/promotion/list').then(res => {
                    this.templates = res.data;
                });
            },
            dropPromotion() {
                let slf = this;
                this.$http.post('/resources/promotion/template/' + slf.template.id + '/drop').then(res => {
                    slf.$Message.success('Delete Success.');
                    slf.getTemplates();
                    slf.template = {};
                    slf.templateContent = '';
                }).catch(error => {
                    slf.$Message.error('Delete Error !');
                });
            },
            contentChanged(content) {
                if (content && content.length > 4000) {
                    this.$Notice.warning({desc: 'Content size limit is 2000 char.'});
                }
                this.template.content = content;
            },
            templateSelected(val) {
                let slf = this;
                if (!val) {
                    slf.template = {};
                    slf.templateContent = '';
                    return;
                }
                this.$http.get('/resources/promotion/template', {params: {id: val}}).then(res => {
                    slf.template = res.data;
                    slf.templateContent = slf.template.content;
                    slf.$Message.success('Template Switch Success ！');
                }).catch(error => {
                    slf.$Message.error('Template Switch Error ！');
                });
            },
            saveTemplate() {
                let slf = this;
                this.$http.post('/resources/promotion/save', this.template).then(res => {
                    slf.$Message.success('Save Success!');
                    slf.getTemplates();
                }).catch(error => {
                    slf.$Message.error('Save Error!')
                });
            },
            imageUploadSuccess(response, file, fileList) {
                this.template.image = response;
            },
            fileUploadSuccess(response, file, fileList) {
                let att = {name: file.name, url: response}
                this.template.attachments.push(att);
            },
            handleMaxSize(file, fileList) {
                this.$Notice.error({desc: 'File size limited ！'})
            }
        },
        mounted() {
            this.getTemplates();
        }
    }
</script>
<style lang="less" scoped>
    .template {
        display: flex;
    }
    .template-drop {
        flex: 5;
    }
    .template-delete {
        flex: 1;
        margin-left: 10px;
    }
    .landing-page-width{
        width: 500px;
    }
    .attach-file {
        margin-left: 10px;
        background-color: #f0ad4e;
    }
</style>
