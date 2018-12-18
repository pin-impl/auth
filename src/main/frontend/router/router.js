import Main from '@/views/Main.vue';

// 不作为Main组件的子页面展示的页面单独写，如下
export const loginRouter = {
    path: '/login',
    name: 'login',
    meta: {
        title: 'Labnetwork Ocean - Login'
    },
    component: () => import('@/views/login.vue')
};

export const locking = {
    path: '/locking',
    name: 'locking',
    component: () => import('@/views/main-components/lockscreen/components/locking-page.vue')
};

// 作为Main组件的子页面展示但是不在左侧菜单显示的路由写在otherRouter里
export const otherRouter = {
    path: '/',
    name: 'otherRouter',
    redirect: '/home',
    access: ['ROLE_SITE_ADMIN', 'ROLE_CSR', 'ROLE_SUPPLIER_ADMIN'],
    component: Main,
    children: [
        {
            path: 'home',
            title: {i18n: 'home'},
            name: 'home_index',
            component: () => import('@/views/home/home.vue')
        }
    ]
};

// 作为Main组件的子页面展示并且在左侧菜单显示的路由写在appRouter里
export const appRouter = [
    {
        path: '/company',
        icon: 'key',
        name: 'company',
        title: 'Company Manage',
        component: Main,
        children: [
            {
                path: 'list',
                title: 'Company Manage',
                name: 'edit',
                component: () => import('@/views/auth/company/company.vue')
            }
        ]
    },
    {
        path: '/user',
        icon: '',
        name: 'user',
        title: 'User Manage',
        component: Main,
        children: [
            {
                path: 'list',
                title: 'User Manage',
                name: 'user-edit',
                component: () => import('@/views/auth/user/user.vue')
            }
        ]
    },
    {
        path: '/application',
        icon: '',
        name: 'application',
        title: 'App Manage',
        component: Main,
        children: [
            {
                path: 'list',
                title: 'App Manage',
                name: 'app-edit',
                component: () => import('@/views/auth/app/application.vue')
            }
        ]
    },
    {
        path: '/role',
        icon: '',
        name: 'role',
        title: 'Role Manage',
        component: Main,
        children: [
            {
                path: 'list',
                title: 'Role Mange',
                name: 'role-edit',
                component: () => import('@/views/auth/role/role.vue')
            }

        ]
    }
];

// 所有上面定义的路由都要写在下面的routers里
export const routers = [
    loginRouter,
    otherRouter,
    locking,
    ...appRouter
];
