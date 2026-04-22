import request from '@/utils/request'

export function getDashboardStats() {
    return request({
        url: '/dashboard/stats',
        method: 'get'
    })
}

export function getSystemResources() {
    return request({
        url: '/dashboard/resources',
        method: 'get'
    })
}

export function getAlgorithmPerformance() {
    return request({
        url: '/dashboard/performance',
        method: 'get'
    })
}

export function getRecentTasks() {
    return request({
        url: '/dashboard/tasks',
        method: 'get'
    })
}

export function getDashboardOverview() {
    return request({
        url: '/dashboard/overview',
        method: 'get'
    })
}
