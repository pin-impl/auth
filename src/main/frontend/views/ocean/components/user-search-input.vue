<template>
    <div class="search-content">
        <Select icon="ios-search" v-model="user" placeholder="Please Search" clearable filterable :loading="loading" remote :remote-method="userSearch" @on-change="userSelected">
            <Option v-show="false" :value="0">{{''}}</Option>
            <Option v-for="u in users" :key="u.email" :value="u.id">{{ u.email }}</Option>
        </Select>
    </div>
</template>
<script>
    export default {
        name: 'user-search-input',
        data() {
            return {
                loading: false,
                users: []
                // user: ''
            }
        },
        props: {
            roles: Array,
            status: '',
            defaultUser: {
                id: undefined,
                email: '',
                index: 0
            },
            returnData: {}
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
            userSelected(val) {
                this.$emit('return-data', val);
            }
        },

        computed: {
            user: {
                get: function() {
                    // this.user = val;
                    console.log('user: ' + this.defaultUser.id)

                    return this.defaultUser.id;
                },
                set: function (val) {
                    this.defaultUser.id = val;
                    return val;
                }
            }

        }
    }
</script>
<style>
    .search-content {
        display: flex;
    }
</style>
