<style lang="less">
    @import '../../../styles/loading.less';
</style>

<template>
    <div>
        <textarea class='tinymce-textarea' v-model="initContent" id="tinymceEditer"></textarea>
        <Spin fix v-if="spinShow">
            <Icon type="load-c" size=18 class="demo-spin-icon-load"></Icon>
            <div>加载组件中...</div>
        </Spin>
    </div>
</template>

<script>
import tinymce from 'tinymce';
export default {
    name: 'text-editor',
    data () {
        return {
            spinShow: true,
            editor: {}
        };
    },
    props: {
        tinymceLang: 'en_GB',
        initContent: '',
        callback: {}
    },
    methods: {
        init () {
            this.$nextTick(() => {
                let vm = this;
                let height = document.body.offsetHeight - 300;
                tinymce.init({
                    selector: '#tinymceEditer',
                    branding: false,
                    elementpath: false,
                    height: height,
                    language: vm.tinymceLang,
                    menubar: 'edit insert view format table tools',
                    plugins: [
                        'advlist autolink lists link charmap print preview hr anchor pagebreak',
                        'searchreplace visualblocks visualchars code fullpage',
                        'insertdatetime nonbreaking save table contextmenu directionality',
                        'paste textcolor colorpicker textpattern'
                    ],
                    toolbar1: ' newnote print preview | undo redo | insert | styleselect | forecolor backcolor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image emoticons media codesample',
                    autosave_interval: '20s',
                    image_advtab: true,
                    table_default_styles: {
                        width: '100%',
                        borderCollapse: 'collapse'
                    },
                    setup: function (editor) {
                        editor.on('init', function (e) {
                            vm.spinShow = false;
                            if (localStorage.editorContent) {
                                tinymce.get('tinymceEditer').setContent(vm.initContent);
                            }
                        });
                        editor.on('keyup', function (e) {
                            let content = tinymce.get('tinymceEditer').getContent();
                            vm.$emit('callback', content);
                        });
                        vm.editor = tinymce;
                    }
                });
            });
        }
    },
    watch: {
        initContent: function (val) {
            this.editor.get('tinymceEditer').setContent(val);
        }
    },
    mounted () {
        this.init();
    },
    destroyed () {
        tinymce.get('tinymceEditer').destroy();
    }
};
</script>

<style>

</style>
