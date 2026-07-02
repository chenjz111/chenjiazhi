import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/',
      component: () => import('../views/Layout.vue'),
      redirect: '/home',
      children: [
        {
          path: 'home',
          name: 'Home',
          component: () => import('../views/Home.vue')
        },
        {
          path: 'strategy',
          name: 'Strategy',
          component: () => import('../views/Strategy.vue')
        },
        {
          path: 'notice',
          name: 'Notice',
          component: () => import('../views/Notice.vue')
        },
        {
          path: 'route',
          name: 'Route',
          component: () => import('../views/Route.vue')
        },
        {
          path: 'partner',
          name: 'Partner',
          component: () => import('../views/Partner.vue')
        },
        {
          path: 'bill',
          name: 'Bill',
          component: () => import('../views/Bill.vue')
        },
        {
          path: 'admin',
          name: 'Admin',
          component: () => import('../views/Admin.vue')
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../views/Profile.vue')
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router