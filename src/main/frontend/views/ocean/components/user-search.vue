<template>
    <Modal :title="$t('searchUser')" v-model="open" @on-visible-change="showChanged">
        <div class="search-content">
            <Select icon="ios-search" v-model="user" placeholder="Please Search" clearable filterable :loading="loading" remote :remote-method="userSearch" @on-change="userSelected">
                <Option v-for="u in users" :key="u.id" :value="u.email">{{ u.email }}</Option>
            </Select>
        </div>
        <div slot="footer">
            <Button size="large" long  @click="open=false">Confirm</Button>
        </div>
    </Modal>
</template>
<script>
    export default {
        name: 'user-search',
        data() {
            return {
                loading: false,
                users: [],
                user: '',
                open: false
            }
        },
        props: {
            show: Boolean,
            roles: Array,
            status: '',
            defaultUser: {
                email: '',
                index: 0
            },
            returnData: {},
            callback: {}
        },
        methods: {
            userSearch(query) {
                this.loading = true;
                this.$http.get('/resources/admin/users/list', {
                    params: {
                        email: query,
                        roles: this.roles,
                        status: this.status
                    }
                }).then(res => {
                    this.users = res.data.user;
                    this.loading = false;
                });
            },
            showChanged(val) {
                this.$emit('callback', val);
            },
            userSelected(val) {
                this.$emit('return-data', {value: val, index: this.defaultUser.index});
            }
        },
        watch: {
            'defaultUser.email': function(val) {
                this.user = val;
            },
            show(val) {
                this.open = val;
            }
        }
    }
</script>
<style>
    .search-content {
        display: flex;
    }
</style>
